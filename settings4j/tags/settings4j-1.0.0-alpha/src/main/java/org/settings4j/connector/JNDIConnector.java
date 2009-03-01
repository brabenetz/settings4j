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

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;

import org.apache.commons.lang.StringUtils;

public class JNDIConnector extends AbstractConnector {
    /** General Logger for this Class */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(JNDIConnector.class);


    private String providerUrl;

    private String initialContextFactory;

    private String urlPkgPrefixes;
    
    private String contextPathPrefix = "java:comp/env/";

    public byte[] getContent(String key) {
        Object obj = lookupInContext(key);

        // if obj is a String and an Object resolver is available
        // obj could be a Path.
        if (obj instanceof String && getContentResolver() != null) {
            byte[] content = getContentResolver().getContent((String) obj);
            if (content != null) {
                return content;
            }
        }

        if (obj instanceof byte[]) {
            return (byte[]) obj;
        }
        return null;
    }

    public Object getObject(String key) {
        Object obj = lookupInContext(key);

        // if obj is a String and an Object resolver is available
        // obj could be a Path to a XML who can be converted to an Object.
        if (obj instanceof String && getObjectResolver() != null) {
            Object convertedObject = getObjectResolver().getObject((String) obj, getContentResolver());
            if (convertedObject != null) {
                return convertedObject;
            }
        }

        return obj;
    }

    public String getString(String key) {
        Object obj = null;
        try {
            obj = lookupInContext(key);
            return (String) obj;
        } catch (ClassCastException e) {
            LOG.warn("Wrong Type: " + obj.getClass().getName() + " for Key: " + key);
            LOG.debug(e.getMessage(), e);
            return null;
        }
    }

    public int setContent(String key, byte[] value) {
        int result = SETTING_NOT_POSSIBLE;
        String path = getString(key);
        if (path != null && getContentResolver() != null) {
            // assume that the value from JNDI-Context is a Path.
            // store the content into the defined Path
            result = getContentResolver().setContent(path, value);
        } else {
            // the current value is not a String or NULL
            // or no Contentresolver is set
            result = rebindToContext(key, value);
        }
        return result;
    }

    public int setObject(String key, Object value) {
        int result = SETTING_NOT_POSSIBLE;
        String path = getString(key);
        if (path != null && getObjectResolver() != null) {
            // assume that the value from JNDI-Context is a Path.
            // serialize the Object into the defined Path
            result = getObjectResolver().setObject(path, getContentResolver(), value);
        } else {
            // the current value is not a String or NULL
            // or no ObjectResolver is set
            result = rebindToContext(key, value);
        }
        return result;
    }

    public int setString(String key, String value) {
        int result = SETTING_NOT_POSSIBLE;
        result = rebindToContext(key, value);
        return result;
    }

    private InitialContext getJNDIContext() throws NamingException {
        InitialContext initialContext;
        
        if (StringUtils.isEmpty(providerUrl) && StringUtils.isEmpty(initialContextFactory)
            && StringUtils.isEmpty(urlPkgPrefixes)) {

            initialContext = new InitialContext();
        } else {
            Properties prop = new Properties();
            prop.put(Context.PROVIDER_URL, providerUrl);
            prop.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
            prop.put(Context.URL_PKG_PREFIXES, urlPkgPrefixes);
            initialContext = new InitialContext(prop);
        }
        
        return initialContext;
    }

    public void setProviderUrl(String providerUrl) {
        this.providerUrl = providerUrl;
    }

    public void setInitialContextFactory(String initialContextFactory) {
        this.initialContextFactory = initialContextFactory;
    }

    public void setUrlPkgPrefixes(String urlPkgPrefixes) {
        this.urlPkgPrefixes = urlPkgPrefixes;
    }

    private Object lookupInContext(String key) {
        String normalizedKey = normalizeKey(key);
        InitialContext ctx = null;
        try {
            Object result;
            ctx = getJNDIContext();
            result = ctx.lookup(normalizedKey);
            return result;
        } catch (NoInitialContextException e) {
            LOG.info("Maybe no JNDI-Context available.");
            LOG.debug(e.getMessage(), e);
        } catch (NamingException e) {
            LOG.debug("cannot lookup key: " + key + " (" + normalizedKey + ")", e);
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException e) {
                    LOG.info("cannot close context: " + key + " (" + normalizedKey + ")", e);
                }
            }
        }
        return null;
    }
    
    private int rebindToContext(String key, Object value) {
        String normalizedKey = normalizeKey(key);
        if (LOG.isDebugEnabled()){
            LOG.debug("Try to rebind Key '" + key + "' (" + normalizedKey + ")" + " with value: " + value);
        }
        InitialContext ctx = null;
        int result = SETTING_NOT_POSSIBLE;
        try {
            ctx = getJNDIContext();
            createParentContext(ctx, normalizedKey);
            ctx.rebind(normalizedKey, value);
            result = SETTING_SUCCESS;
        } catch (NoInitialContextException e) {
            LOG.info("Maybe no JNDI-Context available.");
            LOG.debug(e.getMessage(), e);
        } catch (NamingException e) {
            // the JNDI-Context from TOMCAT is readonly
            // if you try to write it, The following Exception will be thrown:
            // javax.naming.NamingException: Context is read only
            LOG.info("cannot bind key: " + key + " (" + normalizedKey + ")" + e.getMessage());
            if (LOG.isDebugEnabled()){
                LOG.debug("cannot bind key: " + key + " (" + normalizedKey + ")", e);
            }
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException e) {
                    LOG.info("cannot close context: " + key + " (" + normalizedKey + ")", e);
                }
            }
        }
        return result;
    }
    
    private static void createParentContext(Context ctx, String key) throws NamingException {
        // here we need to break by the specified delimiter

        // can't use String.split as the regexp will clash with the types of chars
        // used in the delimiters. Could use Commons Lang. Quick hack instead.
        // String[] path = key.split( (String) this.table.get(SIMPLE_DELIMITER));
        String[] path = key.split("/");

        int lastIndex = path.length - 1;

        Context tmpCtx = ctx;

        for (int i = 0; i < lastIndex; i++) {
            Object obj = null;
            try {
                obj = tmpCtx.lookup(path[i]);
            } catch (NameNotFoundException e) {
            }
            
            if (obj == null) {
                tmpCtx = tmpCtx.createSubcontext(path[i]);
            } else if (obj instanceof Context) {
                tmpCtx = (Context) obj;
            } else {
                throw new RuntimeException("Illegal node/branch clash. At branch value '" + path[i]
                    + "' an Object was found: " + obj);
            }
        }

        Object obj = null;
        try {
            obj = tmpCtx.lookup(path[lastIndex]);
        } catch (NameNotFoundException e) {
        }

        if (obj instanceof Context) {
            tmpCtx.destroySubcontext(path[lastIndex]);
            obj = null;
        }
    }
    
    private String normalizeKey(String key){
        if (key == null){
            return null;
        }
        
        if (key.startsWith(contextPathPrefix)){
            return key;
        }
        
        key = key.replace('\\', '/');
        
        if (key.startsWith("/")){
            key = key.substring(1);
        }
        return contextPathPrefix + key;
    }
    
    public String getContextPathPrefix() {
        return contextPathPrefix;
    }

    public void setContextPathPrefix(String contextPathPrefix) {
        this.contextPathPrefix = contextPathPrefix;
    }
}