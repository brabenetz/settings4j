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

package org.settings4j.objectresolver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.settings4j.ContentResolver;
import org.settings4j.ObjectResolver;

public abstract class AbstractObjectResolver implements ObjectResolver{

    /** General Logger for this Class */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(AbstractObjectResolver.class);

    public static final String PROP_OBJECT_RESOLVER_KEY = "objectResolverKey";
    public static final String PROP_CACHED = "cached";
    public static final String PROP_READONLY = "readonly";
    
    private String propertySuffix = ".properties";
    
    private Map cachedObjects = new HashMap();
    
    private boolean cached = false;

    public void addObjectResolver(ObjectResolver objectResolver) {
        throw new UnsupportedOperationException(this.getClass().getName() + " cannot add other ObjectResolvers");
    }

    public Object getObject(String key, ContentResolver contentResolver) {
    	
        Object result = cachedObjects.get(key);
        if (result != null){
            return result;
        }
        
        
        byte[] content = contentResolver.getContent(key);
        if (content != null){
            Properties properties = getObjectProperties(key, contentResolver);
            if (properties != null) {
                String propObjectResolverKey = properties.getProperty(PROP_OBJECT_RESOLVER_KEY);
                String propCached = properties.getProperty(PROP_CACHED);
                if (StringUtils.isEmpty(propObjectResolverKey)) {
                    // TODO hbrabenetz 21.05.2008 :
                    // set new PropertyFile with this ObjectResolver (if possible)
                    // write with this ObjectResolver
                    return null;
                }
                
                if (getObjectReolverKey().equals(propObjectResolverKey)){
                    result = contentToObject(key, properties, content, contentResolver);
                    if (result != null){
                        if ("true".equalsIgnoreCase(propCached) || (propCached== null && isCached())){
                            cachedObjects.put(key, result);
                        }
                        return result;
                    }
                }
            }
        }
        return null;
    }
    
    protected Properties getObjectProperties(String key, ContentResolver contentResolver){
        
        byte[] propertyContent = contentResolver.getContent(key + propertySuffix);
        if (propertyContent == null){
            return null;
        } else {
            Properties properties = new Properties();
            try {
                properties.load(new ByteArrayInputStream(propertyContent));
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
                return null;
            }
            return properties;
        }
    }

    public String getPropertySuffix() {
        return propertySuffix;
    }

    public void setPropertySuffix(String propertySuffix) {
        this.propertySuffix = propertySuffix;
    }

    protected String getObjectReolverKey() {
        return this.getClass().getName();
    }
    
    protected abstract Object contentToObject(String key, Properties properties, byte[] content, ContentResolver contentResolver);

    public boolean isCached() {
        return cached;
    }

    public void setCached(boolean cached) {
        this.cached = cached;
    }
}
