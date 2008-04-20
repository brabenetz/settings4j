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

import java.io.UnsupportedEncodingException;

import org.settings4j.contentresolver.FSContentResolver;

public class FSConnector extends AbstractConnector {
    
    /** General Logger for this Class */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(FSConnector.class);
    
    private FSContentResolver fsContentResolver = new FSContentResolver();
    private String charset = "UTF-8";
    
    public byte[] getContent(String key) {
        return fsContentResolver.getContent(key);
    }

    public Object getObject(String key) {
        if (getObjectResolver() != null){
            return getObjectResolver().getObject(key, fsContentResolver);
        } else {
            return null;
        }
    }

    public String getString(String key) {
        try {
            byte[] content =  getContent(key);
            if (content != null){
                return new String(fsContentResolver.getContent(key), charset);
            } else {
                return null;
            }
        } catch (UnsupportedEncodingException e) {
            // should never occure with "UTF-8"
            LOG.error("Charset not found: " + charset, e);
            return null;
        }
    }

    public int setContent(String key, byte[] value) {
        return fsContentResolver.setContent(key, value);
    }

    public int setObject(String key, Object value) {
        if (getObjectResolver() != null){
            return getObjectResolver().setObject(key, fsContentResolver, value);
        } else {
            return SETTING_NOT_POSSIBLE;
        }
    }

    public int setString(String key, String value) {
        try {
            return setContent(key, value.getBytes(charset));
        } catch (UnsupportedEncodingException e) {
            // should never occure with "UTF-8"
            LOG.error("Charset not found: " + charset, e);
            return SETTING_NOT_POSSIBLE;
        }
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public void setRootFolderPath(String rootFolderPath) {
        fsContentResolver.setRootFolderPath(rootFolderPath);
    }
    
}
