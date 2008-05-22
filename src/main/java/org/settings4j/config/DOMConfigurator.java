/* ***************************************************************************
 * Copyright (c) 2008 Brabenetz Harald, Austria.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 *****************************************************************************/
package org.settings4j.config;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.taglibs.standard.lang.jstl.ELException;
import org.settings4j.Connector;
import org.settings4j.ContentResolver;
import org.settings4j.ObjectResolver;
import org.settings4j.Settings;
import org.settings4j.SettingsRepository;
import org.settings4j.connector.AbstractConnector;
import org.settings4j.connector.CachedConnectorWrapper;
import org.settings4j.connector.ContentHasChangedNotifierConnectorWrapper;
import org.settings4j.connector.ReadOnlyConnector;
import org.settings4j.connector.SystemPropertyConnector;
import org.settings4j.contentresolver.ReadOnlyContentResolverWrapper;
import org.settings4j.objectresolver.AbstractObjectResolver;
import org.settings4j.objectresolver.ReadOnlyObjectResolverWrapper;
import org.settings4j.util.ELConnectorWrapper;
import org.settings4j.util.ExpressionLanguageUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DOMConfigurator {

    /** General Logger for this Class */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(DOMConfigurator.class);

    static final Class[] NO_PARAM = new Class[] {};
    
    static final Class[] ONE_STRING_PARAM = new Class[] {String.class};

    static final String CONFIGURATION_TAG = "settings4j:configuration";

    static final String CONNECTOR_TAG = "connector";

    static final String CONNECTOR_REF_TAG = "connector-ref";

    static final String OBJECT_RESOLVER_TAG = "objectResolver";

    static final String OBJECT_RESOLVER_REF_TAG = "objectResolver-ref";

    static final String CONTENT_RESOLVER_TAG = "contentResolver";

    static final String CONTENT_RESOLVER_REF_TAG = "contentResolver-ref";

    static final String PARAM_TAG = "param";

    static final String SETTINGS_TAG = "settings";

    static final String SETTINGS_REF_TAG = "settings-ref";

    static final String NAME_ATTR = "name";

    static final String CLASS_ATTR = "class";

    static final String CACHED_ATTR = "cached";

    static final String VALUE_ATTR = "value";

    static final String ROOT_TAG = "root";

    static final String REF_ATTR = "ref";

    static final String ADDITIVITY_ATTR = "additivity";

    static final String READONLY_ATTR = "readonly";

    final static String dbfKey = "javax.xml.parsers.DocumentBuilderFactory";


    // key: ConnectorName, value: Connector
    private Map connectorBag;
    // key: contentResolver, value: ContentResolver
    private Map contentResolverBag;
    // key: objectResolver, value: ObjectResolver
    private Map objectResolverBag;
    
    private SettingsRepository repository;

    public DOMConfigurator(SettingsRepository repository) {
        super();
        this.repository = repository;
        connectorBag = new HashMap();
        contentResolverBag = new HashMap();
        objectResolverBag = new HashMap();
    }
    
    /**
     * Sets a parameter based from configuration file content.
     * 
     * @param elem param element, may not be null.
     * @param propSetter property setter, may not be null.
     * @param props properties
     */
    private static void setParameter(final Element elem, final Object bean, Connector[] connectors) {
        String name = elem.getAttribute("name");
        String valueStr = (elem.getAttribute("value"));
        try {
            PropertyDescriptor propertyDescriptor = PropertyUtils.getPropertyDescriptor(bean, name);
            Method setter = PropertyUtils.getWriteMethod(propertyDescriptor);
            Object value;
            if(connectors != null){
                value = subst(valueStr, connectors, setter.getParameterTypes()[0]);
            } else {
                value = subst(valueStr, null, setter.getParameterTypes()[0]);
            }
            PropertyUtils.setProperty(bean, name, value);
        } catch (IllegalAccessException e) {
            LOG.warn("Cannnot set Property: " + name, e);
        } catch (InvocationTargetException e) {
            LOG.warn("Cannnot set Property: " + name, e);
        } catch (NoSuchMethodException e) {
            LOG.warn("Cannnot set Property: " + name, e);
        }
    }

    /**
     * A static version of {@link #doConfigure(URL, LoggerRepository)}.
     */
    static public void configure(URL url, SettingsRepository repository) throws FactoryConfigurationError {
        new DOMConfigurator(repository).doConfigure(url);
    }

    public void doConfigure(final URL url) {
        ParseAction action = new ParseAction() {
            public Document parse(final DocumentBuilder parser) throws SAXException, IOException {
                return parser.parse(url.toString());
            }

            public String toString() {
                return "url [" + url.toString() + "]";
            }
        };
        doConfigure(action);
    }

    private final void doConfigure(final ParseAction action) throws FactoryConfigurationError {
        DocumentBuilderFactory dbf = null;
        try {
            LOG.debug("System property is :" + new SystemPropertyConnector().getString(dbfKey));
            dbf = DocumentBuilderFactory.newInstance();
            LOG.debug("Standard DocumentBuilderFactory search succeded.");
            LOG.debug("DocumentBuilderFactory is: " + dbf.getClass().getName());
        } catch (FactoryConfigurationError fce) {
            Exception e = fce.getException();
            LOG.debug("Could not instantiate a DocumentBuilderFactory.", e);
            throw fce;
        }

        try {
            dbf.setValidating(true);

            DocumentBuilder docBuilder = dbf.newDocumentBuilder();

            docBuilder.setErrorHandler(new SAXErrorHandler());
            docBuilder.setEntityResolver(new Settings4jEntityResolver());

            Document doc = action.parse(docBuilder);
            parse(doc.getDocumentElement());
        } catch (Exception e) {
            // I know this is miserable...
            LOG.error("Could not parse " + action.toString() + ".", e);
        }
    }

    /**
     * Used internally to configure the settings4j framework by parsing a DOM tree of XML elements based
     * on <a href="doc-files/settings4j.dtd">settings4j.dtd</a>.
     * 
     */
    protected void parse(Element element) {

        String rootElementName = element.getTagName();

        if (!rootElementName.equals(CONFIGURATION_TAG)) {
            LOG.error("DOM element is - not a <" + CONFIGURATION_TAG + "> element.");
            return;
        }

        /*
         * Building Appender objects, placing them in a local namespace for future reference
         */

        // First configure each category factory under the root element.
        // Category factories need to be configured before any of
        // categories they support.
        //
        String tagName = null;
        Element currentElement = null;
        Node currentNode = null;
        NodeList children = element.getChildNodes();
        final int length = children.getLength();

        for (int loop = 0; loop < length; loop++) {
            currentNode = children.item(loop);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                currentElement = (Element) currentNode;
                tagName = currentElement.getTagName();

                if (tagName.equals(SETTINGS_TAG)) {
                    parseSettings(currentElement);
                } else if (tagName.equals(ROOT_TAG)) {
                    parseRoot(currentElement);
                }
            }
        }
        repository.setConnectorCount(connectorBag.values().size());
    }

    /**
     * Used internally to parse an category element.
     */
    protected void parseSettings(Element loggerElement) {
        // Create a new org.settings4j.Settings object from the <settings> element.
        String settingsName = loggerElement.getAttribute(NAME_ATTR);

        Settings settings;

        LOG.debug("Retreiving an instance of org.settings4j.Settings");
        // settings = (catFactory == null) ? repository.getSettings(settingsName) :
        // repository.getSettings(settingsName, catFactory);
        settings = repository.getSettings(settingsName);

        // Setting up a category needs to be an atomic operation, in order
        // to protect potential log operations while category
        // configuration is in progress.
        synchronized (settings) {
            Boolean additivity = (Boolean)subst(loggerElement.getAttribute(ADDITIVITY_ATTR), null, Boolean.class);

            LOG.debug("Setting [" + settings.getName() + "] additivity to [" + additivity + "].");
            if (additivity != null ){
                // TODO Harald.Brabenetz Apr 8, 2008 : settings.setAdditivity(additivity.booleanValue());
            }
            parseChildrenOfSettingsElement(loggerElement, settings, false);
        }
    }

    /**
     * Used internally to parse an category element.
     */
    protected Connector parseConnector(Element connectorElement) {
        String connectorName = connectorElement.getAttribute(NAME_ATTR);

        Connector connector;

        String className = connectorElement.getAttribute(CLASS_ATTR);

        
        LOG.debug("Desired Connector class: [" + className + ']');
        try {
            Class clazz = loadClass(className);
            Constructor constructor = clazz.getConstructor(NO_PARAM);
            connector = (Connector) constructor.newInstance(null);
        } catch (Exception oops) {
            LOG.error("Could not retrieve connector [" + connectorName + "]. Reported error follows.", oops);
            return null;
        }
        
        Connector[] subConnectors = getConnectors(connectorElement);
        for (int i = 0; i < subConnectors.length; i++) {
            connector.addConnector(subConnectors[i]);
        }
        
        // Setting up a category needs to be an atomic operation, in order
        // to protect potential log operations while category
        // configuration is in progress.
        synchronized (connector) {

            NodeList children = connectorElement.getChildNodes();
            final int length = children.getLength();

            for (int loop = 0; loop < length; loop++) {
                Node currentNode = children.item(loop);

                if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element currentElement = (Element) currentNode;
                    String tagName = currentElement.getTagName();

                    if (tagName.equals(CONTENT_RESOLVER_REF_TAG)) {
                        Element contentResolverRef = (Element) currentNode;
                        ContentResolver contentResolver = findContentResolverByReference(contentResolverRef);
                        connector.setContentResolver(contentResolver);

                    } else if (tagName.equals(OBJECT_RESOLVER_REF_TAG)) {
                        Element objectResolverRef = (Element) currentNode;
                        ObjectResolver objectResolver = findObjectResolverByReference(objectResolverRef);
                        connector.setObjectResolver(objectResolver);

                    } else if (tagName.equals(PARAM_TAG)) {
                        // TODO Harald.Brabenetz Apr 8, 2008 : include subConnectors to settings
                        setParameter(currentElement, connector, subConnectors);
                    } else {
                        // TODO Harald.Brabenetz Apr 17, 2008 :
                        //quietParseUnrecognizedElement(settings, currentElement, props);
                    }
                }
            }
            
            if (connector instanceof AbstractConnector){
                connector = new ContentHasChangedNotifierConnectorWrapper((AbstractConnector)connector);
            }

            Boolean cached = (Boolean)subst(connectorElement.getAttribute(CACHED_ATTR), null, Boolean.class);
            if (cached != null && cached.booleanValue()){
                connector = new CachedConnectorWrapper(connector);
            }
        }
        
        return connector;
    }

    /**
     * Return all referenced connectors from Child-Nodes {@link #CONNECTOR_REF_TAG}
     * 
     * @param element
     * @return
     */
    protected Connector[] getConnectors(Element element){
        List connectors = new ArrayList();
        
        NodeList children = element.getChildNodes();
        final int length = children.getLength();

        for (int loop = 0; loop < length; loop++) {
            Node currentNode = children.item(loop);

            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                Element currentElement = (Element) currentNode;
                String tagName = currentElement.getTagName();

                if (tagName.equals(CONNECTOR_REF_TAG)) {
                    Element connectorRef = (Element) currentNode;
                    Connector connector = findConnectorByReference(connectorRef);
                    connectors.add(connector);
                }
            }
        }
        
        return (Connector[])connectors.toArray(new Connector[connectors.size()]);
    }

    /**
     * Used internally to parse the roor category element.
     */
    protected void parseRoot(Element rootElement) {
        Settings root = repository.getRootSettings();
        // category configuration needs to be atomic
        synchronized (root) {
            parseChildrenOfSettingsElement(rootElement, root, true);
        }
    }
    

    /**
     * Used internally to parse the children of a category element.
     */
    protected void parseChildrenOfSettingsElement(Element settingsElement, Settings settings, boolean isRoot) {

        // Remove all existing appenders from cat. They will be
        // reconstructed if need be.
        settings.removeAllConnectors();

        NodeList children = settingsElement.getChildNodes();
        final int length = children.getLength();


        Connector[] connectors = getConnectors(settingsElement);
        for (int i = 0; i < connectors.length; i++) {
            settings.addConnector(connectors[i]);
        }
        
        for (int loop = 0; loop < length; loop++) {
            Node currentNode = children.item(loop);

            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                Element currentElement = (Element) currentNode;
                String tagName = currentElement.getTagName();

                if (tagName.equals(PARAM_TAG)) {
                    setParameter(currentElement, settings, connectors);
                } else {
                    // TODO Harald.Brabenetz Apr 17, 2008 :
                    //quietParseUnrecognizedElement(settings, currentElement, props);
                }
            }
        }
    }

    

    /**
     * Used internally to parse connectors by IDREF name.
     */
    protected Connector findConnectorByName(Document doc, String connectorName) {
        Connector connector = (Connector) connectorBag.get(connectorName);

        if (connector != null) {
            return connector;
        } else {
            Element element = getElementByNameAttr(doc, connectorName, "connector");

            if (element == null) {
                LOG.error("No connector named [" + connectorName + "] could be found.");
                return null;
            } else {
                connector = parseConnector(element);
                connectorBag.put(connectorName, connector);
                return connector;
            }
        }
    }

    /**
     * Used internally to parse connectors by IDREF element.
     */
    protected Connector findConnectorByReference(Element connectorRef) {
        String connectorName = connectorRef.getAttribute(REF_ATTR);
        Boolean readonly = (Boolean)subst(connectorRef.getAttribute(READONLY_ATTR), null, Boolean.class);
        Document doc = connectorRef.getOwnerDocument();
        Connector connector = findConnectorByName(doc, connectorName);
        if (readonly != null && readonly.booleanValue()){
            return new ReadOnlyConnector(connector);
        }
        return connector;
    }


    /**
     * Used internally to parse an objectResolver element.
     */
    protected ObjectResolver parseObjectResolver(Element objectResolverElement) {
        String objectResolverName = objectResolverElement.getAttribute(NAME_ATTR);

        ObjectResolver objectResolver;

        String className = objectResolverElement.getAttribute(CLASS_ATTR);

        
        LOG.debug("Desired ObjectResolver class: [" + className + ']');
        try {
            Class clazz = loadClass(className);
            Constructor constructor = clazz.getConstructor(NO_PARAM);
            objectResolver = (ObjectResolver) constructor.newInstance(null);
        } catch (Exception oops) {
            LOG.error("Could not retrieve objectResolver [" + objectResolverName + "]. Reported error follows.", oops);
            return null;
        }
        
        // get connectors for ExpressionLanguage validation
        Connector[] connectors = getConnectors(objectResolverElement);
//        for (int i = 0; i < connectors.length; i++) {
//            objectResolver.addConnector(connectors[i]);
//        }
        
        // Setting up a objectResolver needs to be an atomic operation, in order
        // to protect potential settings operations while settings
        // configuration is in progress.
        synchronized (objectResolver) {

            NodeList children = objectResolverElement.getChildNodes();
            final int length = children.getLength();

            for (int loop = 0; loop < length; loop++) {
                Node currentNode = children.item(loop);

                if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element currentElement = (Element) currentNode;
                    String tagName = currentElement.getTagName();

                    if (tagName.equals(OBJECT_RESOLVER_REF_TAG)) {
                        Element objectResolverRef = (Element) currentNode;
                        ObjectResolver subObjectResolver = findObjectResolverByReference(objectResolverRef);
                        objectResolver.addObjectResolver(subObjectResolver);

                    } else if (tagName.equals(PARAM_TAG)) {
                        setParameter(currentElement, objectResolver, connectors);
                    } else {
                        // TODO Harald.Brabenetz Apr 17, 2008 :
                        //quietParseUnrecognizedElement(settings, currentElement, props);
                    }
                }
            }

            Boolean cached = (Boolean)subst(objectResolverElement.getAttribute(CACHED_ATTR), null, Boolean.class);
            if (cached != null && cached.booleanValue()){
                if (objectResolver instanceof AbstractObjectResolver){
                    ((AbstractObjectResolver)objectResolver).setCached(cached.booleanValue());
                } else {
                    LOG.warn("Only AbstractObjectResolver can use the attribute cached=\"true\" ");
                    // TODO hbrabenetz 21.05.2008 : extract setCached into seperate Interface.
                }
            }
        }
        
        return objectResolver;
    }


    /**
     * Used internally to parse an contentResolver element.
     */
    protected ContentResolver parseContentResolver(Element contentResolverElement) {
        String contentResolverName = contentResolverElement.getAttribute(NAME_ATTR);

        ContentResolver contentResolver;

        String className = contentResolverElement.getAttribute(CLASS_ATTR);

        
        LOG.debug("Desired ContentResolver class: [" + className + ']');
        try {
            Class clazz = loadClass(className);
            Constructor constructor = clazz.getConstructor(NO_PARAM);
            contentResolver = (ContentResolver) constructor.newInstance(null);
        } catch (Exception oops) {
            LOG.error("Could not retrieve contentResolver [" + contentResolverName + "]. Reported error follows.", oops);
            return null;
        }
        
        // get connectors for ExpressionLanguage validation
        Connector[] connectors = getConnectors(contentResolverElement);
//        for (int i = 0; i < connectors.length; i++) {
//            contentResolver.addConnector(connectors[i]);
//        }
        
        // Setting up a contentResolver needs to be an atomic operation, in order
        // to protect potential settings operations while settings
        // configuration is in progress.
        synchronized (contentResolver) {

            NodeList children = contentResolverElement.getChildNodes();
            final int length = children.getLength();

            for (int loop = 0; loop < length; loop++) {
                Node currentNode = children.item(loop);

                if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element currentElement = (Element) currentNode;
                    String tagName = currentElement.getTagName();

                    if (tagName.equals(CONTENT_RESOLVER_REF_TAG)) {
                        Element contentResolverRef = (Element) currentNode;
                        ContentResolver subContentResolver = findContentResolverByReference(contentResolverRef);
                        contentResolver.addContentResolver(subContentResolver);

                    } else if (tagName.equals(PARAM_TAG)) {
                        setParameter(currentElement, contentResolver, connectors);
                    } else {
                        // TODO Harald.Brabenetz Apr 17, 2008 :
                        //quietParseUnrecognizedElement(settings, currentElement, props);
                    }
                }
            }
        }
        
        return contentResolver;
    }

    /**
     * Used internally to parse contentResolvers by IDREF name.
     */
    protected ContentResolver findContentResolverByName(Document doc, String contentResolverName) {
        ContentResolver contentResolver = (ContentResolver) contentResolverBag.get(contentResolverName);

        if (contentResolver != null) {
            return contentResolver;
        } else {
            Element element = getElementByNameAttr(doc, contentResolverName, "contentResolver");

            if (element == null) {
                LOG.error("No contentResolver named [" + contentResolverName + "] could be found.");
                return null;
            } else {
                contentResolver = parseContentResolver(element);
                contentResolverBag.put(contentResolverName, contentResolver);
                return contentResolver;
            }
        }
    }



    /**
     * Used internally to parse objectResolvers by IDREF name.
     */
    protected ObjectResolver findObjectResolverByName(Document doc, String objectResolverName) {
        ObjectResolver objectResolver = (ObjectResolver) objectResolverBag.get(objectResolverName);

        if (objectResolver != null) {
            return objectResolver;
        } else {
            Element element = getElementByNameAttr(doc, objectResolverName, "objectResolver");

            if (element == null) {
                LOG.error("No objectResolver named [" + objectResolverName + "] could be found.");
                return null;
            } else {
                objectResolver = parseObjectResolver(element);
                objectResolverBag.put(objectResolverName, objectResolver);
                return objectResolver;
            }
        }
    }

    /**
     * Used internally to parse objectResolvers by IDREF element.
     */
    protected ObjectResolver findObjectResolverByReference(Element objectResolverRef) {
        String objectResolverName = objectResolverRef.getAttribute(REF_ATTR);
        Boolean readonly = (Boolean)subst(objectResolverRef.getAttribute(READONLY_ATTR), null, Boolean.class);
        Document doc = objectResolverRef.getOwnerDocument();
        ObjectResolver objectResolver =  findObjectResolverByName(doc, objectResolverName);
        if (readonly != null && readonly.booleanValue()){
            return new ReadOnlyObjectResolverWrapper(objectResolver);
        }
        return objectResolver;
    }

    
    
    
    /**
     * Used internally to parse contentResolvers by IDREF element.
     */
    protected ContentResolver findContentResolverByReference(Element contentResolverRef) {
        String contentResolverName = contentResolverRef.getAttribute(REF_ATTR);

        Boolean readonly = (Boolean)subst(contentResolverRef.getAttribute(READONLY_ATTR), null, Boolean.class);
        Document doc = contentResolverRef.getOwnerDocument();
        ContentResolver contentResolver = findContentResolverByName(doc, contentResolverName);
        if (readonly != null && readonly.booleanValue()){
            return new ReadOnlyContentResolverWrapper(contentResolver);
        }
        return contentResolver;
    }
    

    private static Element getElementByNameAttr(Document doc, String nameAttrValue, String elementTagName) {
        // Doesn't work on DOM Level 1 :
        // Element element = doc.getElementById(nameAttrValue);

        // Endre's hack:
        Element element = null;
        NodeList list = doc.getElementsByTagName(elementTagName);
        for (int t = 0; t < list.getLength(); t++) {
            Node node = list.item(t);
            NamedNodeMap map = node.getAttributes();
            Node attrNode = map.getNamedItem("name");
            if (nameAttrValue.equals(attrNode.getNodeValue())) {
                element = (Element) node;
                break;
            }
        }
        // Hack finished.
        return element;
    }

    
    
    
    
    private interface ParseAction {
        Document parse(final DocumentBuilder parser) throws SAXException, IOException;
    }

    /**
     * In future implementation this function will replace expressions like ${connectors.string['']}
     * 
     * @param value
     * @param connectors required for validating Expression like ${connectors.string['']}
     * @return
     */
    protected static String subst(final String value, Connector[] connectors) {
        return (String)subst(value, connectors, String.class);
    }

    /**
     * In future implementation of this function will replace expressions like ${connectors.object['']}
     * or simply ${true}
     * 
     * @param value
     * @param clazz
     * @param connectors
     * @return
     */
    protected static Object subst(final String value, Connector[] connectors, final Class clazz) {
        if (StringUtils.isEmpty(value)){
            return null;
        }
        
        if (value.contains("${")){
            try {
                Map context = new HashMap();
                if (connectors != null){
                    // Expression like ${connectors.object['...']} or ${connectors.string['...']} 
                    context.put("connectors", new ELConnectorWrapper(connectors));
                }
                // Expression like ${env['...']} e.g.:  ${env['TOMCAT_HOME']} or ${env.TOMCAT_HOME}
                context.put("env", System.getenv());
                Object result = ExpressionLanguageUtil.evaluateExpressionLanguage(value, context, clazz);
                return result;
            } catch (ELException e) {
                LOG.error(e.getMessage(), e);
                return null;
            }
        } else {
            if (clazz.equals(String.class)){
                return value.trim();
            } else if (clazz.equals(Boolean.class)){
                String trimmedVal = value.trim();
                if("true".equalsIgnoreCase(trimmedVal)){
                  return Boolean.TRUE;
                } else {
                  //if("false".equalsIgnoreCase(trimmedVal))
                  return Boolean.FALSE;
                }
            } else {
                throw new UnsupportedOperationException("The following Type is not supported now: " + clazz + "; found value: " + value);
            }
        }
    }

    static public Class loadClass (String clazz) throws ClassNotFoundException{
        return Thread.currentThread().getContextClassLoader().loadClass(clazz);
    }
}