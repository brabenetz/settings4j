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
package org.settings4j.connector;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.settings4j.contentresolver.ClasspathContentResolver;
import org.settings4j.contentresolver.FSContentResolver;


/**
 * The {@link Properties}-File implementation of an {@link org.settings4j.Connector}.
 * <p>
 * Example usage read property File from Classpath with {@link ClasspathConnector} (&lt;param name="propertyFromContent"
 * ... /&gt;):
 * 
 * <pre>
 * &lt;settings4j:configuration xmlns:settings4j='http://settings4j.org/'&gt;
 * 
 *     &lt;connector name="PropertyFileConnector" class="org.settings4j.connector.PropertyFileConnector"&gt;
 *         &lt;param  name="propertyFromContent"
 *                 value="${connectors.content['org/settings4j/config/propertyFile.properties']}" /&gt;
 *         &lt;connector-ref ref="ClasspathConnector" /&gt;
 *     &lt;/connector&gt;
 *     
 *     &lt;connector name="ClasspathConnector"
 *         class="org.settings4j.connector.ClasspathConnector" /&gt;
 * 
 * &lt;/settings4j:configuration&gt;
 * </pre>
 * 
 * You can also use other connectors which have a nativ implementation for
 * {@link org.settings4j.Connector#getContent(String)} (doesn't use contentResolver). <br />
 * e.g.: {@link FSConnector} for FileSystem paths.
 * <p>
 * Example usage read property File from Classpath with {@link ClasspathConnector}:
 * <p>
 * If you don't like ExpressionLanguage, then you can set the path to your PropertyFile directly. But in this case a
 * prefix "file:" or "classpath" is required. And only "file:" and "classpath" are supported.<br />
 * Example usage read property File from Classpath with classpathString (&lt;param name="propertyFromPath" ... /&gt;):
 * 
 * <pre>
 * &lt;settings4j:configuration xmlns:settings4j='http://settings4j.org/'&gt;
 * 
 *     &lt;connector name="PropertyFileConnector" class="org.settings4j.connector.PropertyFileConnector"&gt;
 *         &lt;param  name="propertyFromPath"
 *                 value="classpath:org/settings4j/config/propertyFile.properties" /&gt;
 *     &lt;/connector&gt;
 * 
 * &lt;/settings4j:configuration&gt;
 * </pre>
 * 
 * @author Harald.Brabenetz
 */
public class PropertyFileConnector extends AbstractPropertyConnector {

    /** General Logger for this Class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(PropertyFileConnector.class);


    private Properties property = new Properties();


    /** {@inheritDoc} */
    @Override
    protected String getProperty(final String key, final String def) {
        return property.getProperty(key, def);
    }

    public void setProperty(final Properties property) {
        this.property = property;
    }

    /**
     * @param content The byte[] Content of a Property-File.
     */
    public void setPropertyFromContent(final byte[] content) {
        final Properties tmpProperty = new Properties();
        try {
            tmpProperty.load(new ByteArrayInputStream(content));
            property = tmpProperty;
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /**
     * @param propertyPath The filepath to a Property-File. Supported prefixes: "file:" and "classpath:".
     */
    public void setPropertyFromPath(final String propertyPath) {
        if (StringUtils.isEmpty(propertyPath)) {
            throw new IllegalArgumentException("The Property Path cannot be empty");
        }
        
        if (propertyPath.startsWith(FSContentResolver.FILE_URL_PREFIX)) {
            setPropertyFromContent(new FSContentResolver().getContent(propertyPath));
        } else if (propertyPath.startsWith(ClasspathContentResolver.CLASSPATH_URL_PREFIX)) {
            setPropertyFromContent(new ClasspathContentResolver().getContent(propertyPath));
        } else {
            throw new IllegalArgumentException("The Property Path must start with 'file:' or 'classpath:'. " //
                + "But the File Property Path was: '" + propertyPath + "'.");
        }
    }
}
