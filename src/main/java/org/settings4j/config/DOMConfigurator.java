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
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.taglibs.standard.lang.jstl.ELException;
import org.settings4j.Connector;
import org.settings4j.ContentResolver;
import org.settings4j.Filter;
import org.settings4j.ObjectResolver;
import org.settings4j.Settings4jInstance;
import org.settings4j.Settings4jRepository;
import org.settings4j.connector.CachedConnectorWrapper;
import org.settings4j.connector.FilteredConnectorWrapper;
import org.settings4j.connector.SystemPropertyConnector;
import org.settings4j.contentresolver.ClasspathContentResolver;
import org.settings4j.contentresolver.FilteredContentResolverWrapper;
import org.settings4j.objectresolver.AbstractObjectResolver;
import org.settings4j.objectresolver.FilteredObjectResolverWrapper;
import org.settings4j.settings.DefaultFilter;
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

    private static final Class[] NO_PARAM = new Class[] {};

    private static final String CONFIGURATION_TAG = "settings4j:configuration";

    private static final String CONNECTOR_TAG = "connector";

    private static final String CONNECTOR_REF_TAG = "connector-ref";

    private static final String OBJECT_RESOLVER_TAG = "objectResolver";

    private static final String OBJECT_RESOLVER_REF_TAG = "objectResolver-ref";

    private static final String CONTENT_RESOLVER_TAG = "contentResolver";

    private static final String CONTENT_RESOLVER_REF_TAG = "contentResolver-ref";

    private static final String MAPPING_TAG = "mapping";

    private static final String FILTER_TAG = "filter";

    private static final String EXCLUDE_TAG = "exclude";

    private static final String INCLUDE_TAG = "include";
    
    private static final String ENTRY_TAG = "entry";

    private static final String ENTRY_KEY_ATTR = "key";

    private static final String ENTRY_REFKEY_ATTR = "ref-key";

    private static final String PARAM_TAG = "param";

    private static final String NAME_ATTR = "name";

    private static final String CLASS_ATTR = "class";

    private static final String PATTERN_ATTR = "pattern";

    private static final String CACHED_ATTR = "cached";

    private static final String VALUE_ATTR = "value";

    private static final String REF_ATTR = "ref";

    private final static String dbfKey = "javax.xml.parsers.DocumentBuilderFactory";


    // key: ConnectorName, value: Connector
    private Map connectorBag;
    // key: contentResolver-Name, value: ContentResolver
    private Map contentResolverBag;
    // key: objectResolver-Name, value: ObjectResolver
    private Map objectResolverBag;
    // key: mapping-Name, value: Map
    private Map mappingBag;
    
    private Settings4jRepository repository;
    
    private Map expressionAttributes = new HashMap();
    
    public DOMConfigurator(Settings4jRepository repository) {
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
    private void setParameter(final Element elem, final Object bean, Connector[] connectors) {
        String name = elem.getAttribute(NAME_ATTR);
        String valueStr = (elem.getAttribute(VALUE_ATTR));
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
     * A static version of {@link #doConfigure(URL)}.
     * 
     * @param url The location of the configuration file.
     * @param repository the Repository to configure.
     * @throws FactoryConfigurationError
     */
    static public void configure(URL url, Settings4jRepository repository) throws FactoryConfigurationError {
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

        // TODO hbrabenetz 15.06.2008 : reset Configuration
//        //
//        //   reset repository before configuration if reset="true"
//        //       on configuration element.
//        //
//      String resetAttrib = subst(element.getAttribute(RESET_ATTR));
//      LogLog.debug("reset attribute= \"" + resetAttrib +"\".");
//      if(!("".equals(resetAttrib))) {
//           if (OptionConverter.toBoolean(resetAttrib, false)) {
//               repository.resetConfiguration();
//           }
//      }
        
        
        /*
         * Building Appender objects, placing them in a local namespace for future reference
         */

        // settings = (catFactory == null) ? repository.getSettings(settingsName) :
        // repository.getSettings(settingsName, catFactory);
    	Settings4jInstance root = repository.getSettings();
        // settings configuration needs to be atomic
        synchronized (root) {
            parseChildrenOfSettingsElement(element, root);
        }
    }

    /**
     * Used internally to parse an Filter element.
     */
    protected Filter parseFilter(Element filterElement) {
    	
    	Filter filter;


        String className = filterElement.getAttribute(CLASS_ATTR);
        if (StringUtils.isEmpty(className)){
        	className = DefaultFilter.class.getName();
        }
        
        LOG.debug("Desired Connector class: [" + className + "]");
        try {
            Class clazz = loadClass(className);
            Constructor constructor = clazz.getConstructor(NO_PARAM);
            filter = (Filter) constructor.newInstance(null);
        } catch (Exception oops) {
            LOG.error("Could not retrieve connector [filter: " + className + "]. Reported error follows.", oops);
            return null;
        }

        NodeList children = filterElement.getChildNodes();
        final int length = children.getLength();

        for (int loop = 0; loop < length; loop++) {
            Node currentNode = children.item(loop);

            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                Element currentElement = (Element) currentNode;
                String tagName = currentElement.getTagName();

                if (tagName.equals(INCLUDE_TAG)) {
                    Element includeTag = (Element) currentNode;
                    String patteren = includeTag.getAttribute(PATTERN_ATTR);
                    filter.addInclude(patteren);

                } else if (tagName.equals(EXCLUDE_TAG)) {
                    Element excludeTag = (Element) currentNode;
                    String patteren = excludeTag.getAttribute(PATTERN_ATTR);
                    filter.addExclude(patteren);
                } else {
                    quietParseUnrecognizedElement(filter, currentElement);
                }
            }
        }
        
        return filter;
    }
    
    /**
     * Used internally to parse an connector element.
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
        
        connector.setName(connectorName);
        
        Connector[] subConnectors = getConnectors(connectorElement);
        for (int i = 0; i < subConnectors.length; i++) {
            connector.addConnector(subConnectors[i]);
        }
        
        Filter filter = null;
        // Setting up a connector needs to be an atomic operation, in order
        // to protect potential setXXX operations while connector
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

                    } else if (tagName.equals(FILTER_TAG)) {
                        Element filterElement = (Element) currentNode;
                        filter = parseFilter(filterElement);
                    } else if (tagName.equals(PARAM_TAG)) {
                        setParameter(currentElement, connector, subConnectors);
                    } else {
                        quietParseUnrecognizedElement(connector, currentElement);
                    }
                }
            }

            Boolean cached = (Boolean)subst(connectorElement.getAttribute(CACHED_ATTR), subConnectors, Boolean.class);
            if (cached != null && cached.booleanValue()){
                connector = new CachedConnectorWrapper(connector);
            }
            
            if (filter != null){
            	connector = new FilteredConnectorWrapper(connector, filter);
            }
            
            // initial the connector
            connector.init();
        }
        return connector;
    }

    /**
     * Only logs out unrecognized Elements
     * 
     * @param instance instance, may be null.
     * @param element element, may not be null.
     */
    private static void quietParseUnrecognizedElement(final Object instance, final Element element) {
        String elementName = "UNKNOWN";
        String instanceClassName = "UNKNOWN";
        
        try{
            elementName = element.getNodeName();
            instanceClassName = instance.getClass().getName();
        }catch (Exception e) {
            LOG.warn("Error in quietParseUnrecognizedElement(): " + e.getMessage());
            if (LOG.isDebugEnabled()){
                LOG.debug(e.getMessage(), e);
            }
        }
        LOG.warn("Unrecognized Element will be ignored: " + elementName + " for Instance: " + instanceClassName);
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
     * Used internally to parse the children of a settings element.
     */
    protected void parseChildrenOfSettingsElement(Element settingsElement, Settings4jInstance settings) {

        Node currentNode = null;
        Element currentElement = null;
        String tagName = null;
        
        // Remove all existing appenders from settings. They will be
        // reconstructed if need be.
        settings.removeAllConnectors();

        // first parse Connectors (are needed to parse param Tags
        NodeList connectorElements = settingsElement.getElementsByTagName(CONNECTOR_TAG);
        int length = connectorElements.getLength();
        for (int i = 0; i < length; i++) {
            currentNode = connectorElements.item(i);
            currentElement = (Element) currentNode;

        	Connector connector = parseConnector(currentElement);
        	if (connector != null){
                connectorBag.put(connector.getName(), connector);
        		settings.addConnector(connector);
        	}
        }
        
        List list = settings.getConnectors();
        Connector[] connectors = (Connector[])list.toArray(new Connector[list.size()]);

        NodeList children = settingsElement.getChildNodes();
        
        // Now parse other Tags like PARAM or MAPPING
        length = children.getLength();
        for (int i = 0; i < length; i++) {
            currentNode = children.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                currentElement = (Element) currentNode;
                tagName = currentElement.getTagName();

                if (tagName.equals(MAPPING_TAG)) {
                	Map mapping = parseMapping(currentElement);
                	if (mapping != null){
                		settings.setMapping(mapping);
                	}
                } else if (tagName.equals(PARAM_TAG)) {
                    setParameter(currentElement, settings, connectors);
                } else if (tagName.equals(CONNECTOR_TAG)) {
                    LOG.trace("CONNECTOR_TAG already parsed");
                } else if (tagName.equals(CONTENT_RESOLVER_TAG)) {
                    LOG.trace("CONTENT_RESOLVER_TAG will be parsed on the maned");
                } else if (tagName.equals(OBJECT_RESOLVER_TAG)) {
                    LOG.trace("OBJECT_RESOLVER_TAG will be parsed on the maned");
                } else {
                    quietParseUnrecognizedElement(settings, currentElement);
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
        Document doc = connectorRef.getOwnerDocument();
        return findConnectorByName(doc, connectorName);
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
        } catch (NoClassDefFoundError e) {
            LOG.warn("The ObjectResolver '" + objectResolverName + "' cannot be created. There are not all required Libraries inside the Classpath: " + e.getMessage(), e);
            return null;
        }
        
        // get connectors for ExpressionLanguage validation
        Connector[] connectors = getConnectors(objectResolverElement);
//        for (int i = 0; i < connectors.length; i++) {
//            objectResolver.addConnector(connectors[i]);
//        }

        Filter filter = null;
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
                    } else if (tagName.equals(FILTER_TAG)) {
                        Element filterElement = (Element) currentNode;
                        filter = parseFilter(filterElement);
                    } else {
                        quietParseUnrecognizedElement(objectResolver, currentElement);
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
            
            if (filter != null){
            	objectResolver = new FilteredObjectResolverWrapper(objectResolver, filter);
            }
        }
        
        return objectResolver;
    }


    /**
     * Used internally to parse an mapping element.
     */
    protected Map parseMapping(Element mappingElement) {
        String mappingName = mappingElement.getAttribute(NAME_ATTR);

        Map mapping;

        String className = mappingElement.getAttribute(CLASS_ATTR);
        if (StringUtils.isEmpty(className)){
            className = "java.util.HashMap";
        }
        
        LOG.debug("Desired Map class: [" + className + ']');
        try {
            Class clazz = loadClass(className);
            Constructor constructor = clazz.getConstructor(NO_PARAM);
            mapping = (Map) constructor.newInstance(null);
        } catch (Exception oops) {
            LOG.error("Could not retrieve mapping [" + mappingName + "]. Reported error follows.", oops);
            return null;
        } catch (NoClassDefFoundError e) {
            LOG.warn("The Mapping '" + mappingName + "' cannot be created. There are not all required Libraries inside the Classpath: " + e.getMessage(), e);
            return null;
        }
        
        // Setting up a mapping needs to be an atomic operation, in order
        // to protect potential settings operations while settings
        // configuration is in progress.
        synchronized (mapping) {

            NodeList children = mappingElement.getChildNodes();
            final int length = children.getLength();

            for (int loop = 0; loop < length; loop++) {
                Node currentNode = children.item(loop);

                if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element currentElement = (Element) currentNode;
                    String tagName = currentElement.getTagName();

                    if (tagName.equals(ENTRY_TAG)) {
                        String key = currentElement.getAttribute(ENTRY_KEY_ATTR);
                        String keyRef = currentElement.getAttribute(ENTRY_REFKEY_ATTR);
                        mapping.put(key, keyRef);
                    } else {
                        quietParseUnrecognizedElement(mapping, currentElement);
                    }
                }
            }
        }
        
        return mapping;
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
        } catch (NoClassDefFoundError e) {
            LOG.warn("The ContentResolver '" + contentResolverName + "' cannot be created. There are not all required Libraries inside the Classpath: " + e.getMessage(), e);
            return null;
        }
        
        // get connectors for ExpressionLanguage validation
        Connector[] connectors = getConnectors(contentResolverElement);
//        for (int i = 0; i < connectors.length; i++) {
//            contentResolver.addConnector(connectors[i]);
//        }

        Filter filter = null;
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
                    } else if (tagName.equals(FILTER_TAG)) {
                        Element filterElement = (Element) currentNode;
                        filter = parseFilter(filterElement);
                    } else {
                        quietParseUnrecognizedElement(contentResolver, currentElement);
                    }
                }
            }
            
            if (filter != null){
            	contentResolver = new FilteredContentResolverWrapper(contentResolver, filter);
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
            Element element = getElementByNameAttr(doc, contentResolverName, CONTENT_RESOLVER_TAG);

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
            Element element = getElementByNameAttr(doc, objectResolverName, OBJECT_RESOLVER_TAG);

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
     * Used internally to parse mappings by IDREF name.
     */
    protected Map findMappingByName(Document doc, String mappingName) {
        Map mapping = (Map) mappingBag.get(mappingName);

        if (mapping != null) {
            return mapping;
        } else {
            Element element = getElementByNameAttr(doc, mappingName, MAPPING_TAG);

            if (element == null) {
                LOG.error("No mapping named [" + mappingName + "] could be found.");
                return null;
            } else {
                mapping = parseMapping(element);
                mappingBag.put(mappingName, mapping);
                return mapping;
            }
        }
    }

    /**
     * Used internally to parse objectResolvers by IDREF element.
     */
    protected ObjectResolver findObjectResolverByReference(Element objectResolverRef) {
        String objectResolverName = objectResolverRef.getAttribute(REF_ATTR);
        Document doc = objectResolverRef.getOwnerDocument();
        return findObjectResolverByName(doc, objectResolverName);
    }

    
    
    
    /**
     * Used internally to parse contentResolvers by IDREF element.
     */
    protected ContentResolver findContentResolverByReference(Element contentResolverRef) {
        String contentResolverName = contentResolverRef.getAttribute(REF_ATTR);
        Document doc = contentResolverRef.getOwnerDocument();
        return findContentResolverByName(doc, contentResolverName);
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
    protected String subst(final String value, Connector[] connectors) {
        return (String)subst(value, connectors, String.class);
    }

    /**
     * this function will replace expressions like ${connectors.object['']}
     * or simply ${true}
     * 
     * @param value
     * @param clazz
     * @param connectors
     * @return
     */
    protected Object subst(final String value, Connector[] connectors, final Class clazz) {
        if (StringUtils.isEmpty(value)){
            return null;
        }
        
        if (value.indexOf("${") >= 0){
            try {
                Map context = new HashMap(expressionAttributes);
                if (connectors != null){
                    // Expression like ${connectors.object['...']} or ${connectors.string['...']} 
                    context.put("connectors", new ELConnectorWrapper(connectors));

                    // Expression like ${connector.FsConnector.object['...']} or ${connector.ClassPathConnector.string['...']} 
                    Map connectorMap = new HashMap();
                    for (int i = 0; i < connectors.length; i++) {
                        Connector connector = connectors[i];
                        connectorMap.put(connector.getName(), new ELConnectorWrapper(new Connector[]{connector}));
                    }
                    context.put("connector", connectorMap);
                }
                if (SystemUtils.isJavaVersionAtLeast(1.5f)) {
                    // Expression like ${env['...']} e.g.:  ${env['TOMCAT_HOME']} or ${env.TOMCAT_HOME}
                    // Only since jdk 1.5 ....
                    context.put("env", System.getenv());
                }
                Object result = ExpressionLanguageUtil.evaluateExpressionLanguage(value, context, clazz);
                return result;
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                return null;
            }
        } else {
            if (clazz.equals(String.class)){
                return value.trim();
            } else if (clazz.equals(Boolean.class)){
                return BooleanUtils.toBooleanObject(value.trim());
            } else {
                throw new UnsupportedOperationException("The following Type is not supported now: " + clazz + "; found value: " + value);
            }
        }
    }
    
    /**
     * Add a ExpressionAttribute.
     * 
     * @param key
     * @param value
     */
    public void addExpressionAttribute(String key, Object value){
        expressionAttributes.put(key, value);
    }

    static public Class loadClass (String clazz) throws ClassNotFoundException{
        return ClasspathContentResolver.getClassLoader().loadClass(clazz);
    }
}