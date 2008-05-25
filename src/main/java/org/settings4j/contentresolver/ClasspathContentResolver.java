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
package org.settings4j.contentresolver;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.settings4j.Constants;
import org.settings4j.ContentResolver;

public class ClasspathContentResolver implements ContentResolver {
    /** General Logger for this Class */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(ClasspathContentResolver.class);
    

    /** Pseudo URL prefix for loading from the class path: "classpath:" */
    public static final String CLASSPATH_URL_PREFIX = "classpath:";
    
    
    public void addContentResolver(ContentResolver contentResolver) {
        throw new UnsupportedOperationException("ClasspathContentResolver cannot add other ContentResolvers");
    }

    public byte[] getContent(String key) {
        if (key.startsWith(CLASSPATH_URL_PREFIX)){
            key = key.substring(CLASSPATH_URL_PREFIX.length());
        }
        if (key.startsWith("/")){
            key = key.substring(1);
        }
        
        try {
            InputStream is = getClassLoader().getResourceAsStream(key);
            if (is != null){
                return IOUtils.toByteArray(getClassLoader().getResourceAsStream(key));
            } else {
                return null;
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }
    
    public static URL getResource(String key){

        if (key.startsWith(CLASSPATH_URL_PREFIX)){
            key = key.substring(CLASSPATH_URL_PREFIX.length());
        }
        if (key.startsWith("/")){
            key = key.substring(1);
        }
        
        return getClassLoader().getResource(key);
    }
    
    public int setContent(String key, byte[] value) {
        return Constants.SETTING_NOT_POSSIBLE;
    }
    

    /**
     * Return the default ClassLoader to use: typically the thread context
     * ClassLoader, if available; the ClassLoader that loaded the ClasspathContentResolver
     * class will be used as fallback.
     * <p>Call this method if you intend to use the thread context ClassLoader
     * in a scenario where you absolutely need a non-null ClassLoader reference:
     * for example, for class path resource loading (but not necessarily for
     * <code>Class.forName</code>, which accepts a <code>null</code> ClassLoader
     * reference as well).
     * @return the default ClassLoader (never <code>null</code>)
     * @see java.lang.Thread#getContextClassLoader()
     */
    private static ClassLoader getClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        }
        catch (Throwable ex) {
            LOG.debug("Cannot access thread context ClassLoader - falling back to system class loader", ex);
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = ClasspathContentResolver.class.getClassLoader();
        }
        return cl;
    }
}
