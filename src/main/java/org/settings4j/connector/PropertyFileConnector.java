/*
 * #%L
 * settings4j
 * ===============================================================
 * Copyright (C) 2008 - 2015 Brabenetz Harald, Austria
 * ===============================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.settings4j.connector;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.settings4j.contentresolver.ClasspathContentResolver;
import org.settings4j.contentresolver.FSContentResolver;

/**
 * The {@link Properties}-File implementation of an {@link org.settings4j.Connector}.
 * <h3>1. Example - Use Property-File from Classpath or Filepath</h3>
 * <p>
 * Example usage read property File from Classpath or Filepath: <br>
 * In this case a prefix "file:" or "classpath:" is required.
 * </p>
 *
 * <pre>
 * &lt;settings4j:configuration xmlns:settings4j='http://settings4j.org/'&gt;
 *
 *     &lt;connector name="PropertyFileConnector" class="org.settings4j.connector.PropertyFileConnector"&gt;
 *         &lt;param  name="propertyFromPath" value="classpath:org/settings4j/config/propertyFile.properties" /&gt;
 *     &lt;/connector&gt;
 *
 * &lt;/settings4j:configuration&gt;
 * </pre>
 *
 * <h3>2. Example - get the path to the Property File from any other Connector</h3>
 * <p>
 * In this Example you can configure the path to your Property-File via System-Properties or per Server-JNDI-Context.<br>
 * E.g.: Start your application with: "-DmyAppConfig=file:/somePath/myConfig.properties"
 * </p>
 *
 * <pre>
 * &lt;settings4j:configuration xmlns:settings4j='http://settings4j.org/'&gt;
 *
 *     &lt;connector name="PropertyFileConnector" class="org.settings4j.connector.PropertyFileConnector"&gt;
 *         &lt;param  name="propertyFromPath" value="${connectors.content['myAppConfig']}" /&gt;
 *         &lt;connector-ref ref="SystemPropertyConnector" /&gt;
 *         &lt;connector-ref ref="JNDIConnector" /&gt;
 *     &lt;/connector&gt;
 *
 *     &lt;connector name="SystemPropertyConnector" class="org.settings4j.connector.SystemPropertyConnector"/&gt;
 *
 *     &lt;connector name="JNDIConnector" class="org.settings4j.connector.JNDIConnector"/&gt;
 *
 * &lt;/settings4j:configuration&gt;
 * </pre>
 *
 * <h3>3. Resolve relative Paths in your Property File</h3>
 * <p>
 * In many application it is required to configure some paths.<br>
 * Some Examples are:
 * </p>
 * <ul>
 * <li>path to your message-Properties
 * <li>path to other configuration-Files (e.g.: log4j.xml)
 * <li>path to some folders (file-upload, cache-directory, log-directory)
 * <li>other paths
 * </ul>
 * <p>
 * The chance are good, that you want place this folders somewhere relative to the property file.<br>
 * You must only set the parameter "resolveRelativePaths" on the PropertyFileConnector to "true", and all values from the Property-File which starts with
 * "file:." will be resolved relative to the Property-File.
 * </p>
 *
 * <pre>
 * &lt;settings4j:configuration xmlns:settings4j='http://settings4j.org/'&gt;
 *
 *     &lt;connector name="SystemPropertyConnector" class="org.settings4j.connector.SystemPropertyConnector"/&gt;
 *
 *     &lt;connector name="PropertyFileConnector" class="org.settings4j.connector.PropertyFileConnector"&gt;
 *         &lt;param  name="propertyFromPath" value="${connectors.content['myAppConfig']}" /&gt;
 *         &lt;param  name="resolveRelativePaths" value="true /&gt;
 *         &lt;connector-ref ref="SystemPropertyConnector" /&gt;
 *     &lt;/connector&gt;
 *
 * &lt;/settings4j:configuration&gt;
 * </pre>
 * <p>
 * With this config, you can start your app with <code>"-DmyAppConfig=file:/somePath/myConfig.properties"</code>.<br>
 * In "myConfig.properties" you have somewhere configured the property:<br>
 * <code>xyz=file:./test.xml</code><br>
 * The Settings4j.getString("xyz") will return the {@link URL}-Qualified path: "file:/somePath/test.xml"
 * </p>
 *
 * @author Harald.Brabenetz
 */
public class PropertyFileConnector extends AbstractPropertyConnector {

    /** General Logger for this Class. */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PropertyFileConnector.class);

    private Properties property = new Properties();

    private boolean resolveRelativePaths;
    private URL propertyFileFolderUrl;

    /** {@inheritDoc} */
    public String getString(final String key) {
        return this.property.getProperty(key, null);
    }

    public void setProperty(final Properties property) {
        this.property = property;
    }

    // SuppressWarnings PMD.DefaultPackage: used for UnitTests validation.
    @SuppressWarnings("PMD.DefaultPackage")
    URL getPropertyFileFolderUrl() {
        return this.propertyFileFolderUrl;
    }

    /**
     * @param resolveRelativePaths
     *        set to true if Property Values wit a relative path (starting with "file:.") should be replace with a full qualified URL Path relative to the
     *        Property-File
     */
    public void setResolveRelativePaths(final boolean resolveRelativePaths) {
        this.resolveRelativePaths = resolveRelativePaths;
        resolveRelativePaths();
    }

    /**
     * @param content
     *        The byte[] Content of a Property-File.
     * @deprecated will be removed with Settings4j-2.1. Please use setPropertyFromPath instead.
     */
    @Deprecated
    public void setPropertyFromContent(final byte[] content) {
        LOG.warn("PropertyFileConnector.setPropertyFromContent is deprected and  will be removed with Settings4j-2.1. Please use setPropertyFromPath instead.");
        setPropertyFromContentInternal(content);
    }

    private void setPropertyFromContentInternal(final byte[] content) {
        final Properties tmpProperty = new Properties();
        try {
            tmpProperty.load(new ByteArrayInputStream(content));
            this.property = tmpProperty;
        } catch (final IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * @param propertyPath
     *        The filepath to a Property-File. Supported prefixes: "file:" and "classpath:".
     */
    public void setPropertyFromPath(final String propertyPath) {
        if (StringUtils.isEmpty(propertyPath)) {
            throw new IllegalArgumentException("The Property Path cannot be empty");
        }

        if (propertyPath.startsWith(FSContentResolver.FILE_URL_PREFIX)) {
            setPropertyFromContentInternal(new FSContentResolver().getContent(propertyPath));
            this.propertyFileFolderUrl = getParentFolderUrlFromFile(propertyPath);
        } else if (propertyPath.startsWith(ClasspathContentResolver.CLASSPATH_URL_PREFIX)) {
            setPropertyFromContentInternal(new ClasspathContentResolver().getContent(propertyPath));
            this.propertyFileFolderUrl = getParentFolderUrlFromClasspath(propertyPath);
        } else {
            throw new IllegalArgumentException("The Property Path must start with 'file:' or 'classpath:'. " //
                + "But the File Property Path was: '" + propertyPath + "'.");
        }

        resolveRelativePaths();
    }

    private static URL getParentFolderUrlFromClasspath(final String propertyPath) {
        final URL resource = ClasspathContentResolver.getResource(propertyPath);
        final String fullPathNoEndSeparator = FilenameUtils.getFullPathNoEndSeparator(resource.toExternalForm());
        return createURL(fullPathNoEndSeparator + "/");
    }

    private static URL getParentFolderUrlFromFile(final String propertyPath) {
        final File propertyFile = new File(StringUtils.removeStart(propertyPath, FSContentResolver.FILE_URL_PREFIX));
        try {
            return propertyFile.getParentFile().toURL();
        } catch (final IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void resolveRelativePaths() {

        if (!this.resolveRelativePaths) {
            return;
        }

        if (this.propertyFileFolderUrl == null) {
            return;
        }
        final Set<Entry<Object, Object>> entrySet = this.property.entrySet();
        for (final Entry<Object, Object> entry : entrySet) {
            final String propValue = entry.getValue().toString();
            if (propValue.startsWith(FSContentResolver.FILE_URL_PREFIX + ".")) {

                final String valuePath = StringUtils.removeStart(propValue, FSContentResolver.FILE_URL_PREFIX);
                final String newPath = createURL(this.propertyFileFolderUrl, valuePath).toString();
                this.property.setProperty(entry.getKey().toString(), newPath);

            }
        }
    }

    private static URL createURL(final String fullPathNoEndSeparator) {
        try {
            return new URL(fullPathNoEndSeparator);
        } catch (final Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static URL createURL(final URL context, final String spec) {
        try {
            return new URL(context, spec);
        } catch (final Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
