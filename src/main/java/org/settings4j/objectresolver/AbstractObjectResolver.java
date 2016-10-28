/*
 * #%L
 * settings4j
 * ===============================================================
 * Copyright (C) 2008 - 2016 Brabenetz Harald, Austria
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
package org.settings4j.objectresolver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.settings4j.ContentResolver;
import org.settings4j.ObjectResolver;

/**
 * Basic Connector implementations like getter and Setter of contentResolver, objectResolver.
 *
 * @author Harald.Brabenetz
 *
 */
public abstract class AbstractObjectResolver implements ObjectResolver {

    /** General Logger for this Class. */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AbstractObjectResolver.class);

    /** The Key which ObjectResolver implementation should be used. */
    public static final String PROP_OBJECT_RESOLVER_KEY = "objectResolverKey";
    /** The Key if the found Object should be cached (this will handle the Object as singleton). */
    public static final String PROP_CACHED = "cached";

    private String propertySuffix = ".properties";

    private final Map<String, Object> cachedObjects = new HashMap<String, Object>();

    private boolean cached;

    @Override
    public void addObjectResolver(final ObjectResolver objectResolver) {
        throw new UnsupportedOperationException(this.getClass().getName() + " cannot add other ObjectResolvers");
    }

    @Override
    public Object getObject(final String key, final ContentResolver contentResolver) {

        Object result = this.cachedObjects.get(key);
        if (result != null) {
            return result;
        }


        final byte[] content = contentResolver.getContent(key);
        if (content == null) {
            return null;
        }
        final Properties properties = getObjectProperties(key, contentResolver);
        if (properties == null) {
            return null;
        }

        final String propObjectResolverKey = properties.getProperty(PROP_OBJECT_RESOLVER_KEY);
        if (StringUtils.isEmpty(propObjectResolverKey)) {
            LOG.warn("The property-File for Key '{}' doesn't have the required Property '{}'", //
                key, PROP_OBJECT_RESOLVER_KEY);
            return null;
        }
        if (!getObjectResolverKey().equals(propObjectResolverKey)) {
            return null;
        }

        result = contentToObject(key, properties, content, contentResolver);
        if (result != null) {
            if (isCacheEnabled(properties)) {
                this.cachedObjects.put(key, result);
            }
            return result;
        }
        return null;
    }

    private boolean isCacheEnabled(final Properties properties) {
        final String propCached = properties.getProperty(PROP_CACHED);
        return "true".equalsIgnoreCase(propCached) || (propCached == null && isCached());
    }

    /**
     * To get the additional Properties for the given byte[] content can be overwriten by subclasses.
     * <p>
     * The default implementation reads the PropertyFile from key + ".properties". If no property where found, or an error occurs, this method return null.
     * </p>
     *
     * @param key
     *        the key of the byte[] Content to convert.
     * @param contentResolver
     *        the ContentResolver to read the additional Property-File.
     * @return the parsed {@link Properties} Object or null if an error occurred.
     */
    protected Properties getObjectProperties(final String key, final ContentResolver contentResolver) {

        final byte[] propertyContent = contentResolver.getContent(key + this.propertySuffix);
        if (propertyContent == null) {
            return null;
        }
        // else
        final Properties properties = new Properties();
        try {
            properties.load(new ByteArrayInputStream(propertyContent));
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
        return properties;

    }

    public String getPropertySuffix() {
        return this.propertySuffix;
    }

    public void setPropertySuffix(final String propertySuffix) {
        this.propertySuffix = propertySuffix;
    }

    protected String getObjectResolverKey() {
        return this.getClass().getName();
    }

    /**
     * Method to convert the given content-File to an Object must be implemented by SubClasses.
     *
     * @param key The Original Key of the Object
     * @param properties The Property-File which where Found under key + ".properties"
     * @param content The byte[] Content to convert.
     * @param contentResolver the contentResolver to get possible additional content Files.
     * @return the parsed Object from the byte[] Content.
     */
    protected abstract Object contentToObject(String key, Properties properties, byte[] content,
        ContentResolver contentResolver);

    public boolean isCached() {
        return this.cached;
    }

    public void setCached(final boolean cached) {
        this.cached = cached;
    }
}
