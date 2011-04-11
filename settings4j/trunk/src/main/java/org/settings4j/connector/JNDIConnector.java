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

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.settings4j.Constants;

public class JNDIConnector extends AbstractConnector {
    /** General Logger for this Class */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(JNDIConnector.class);


    private String providerUrl;

    private String initialContextFactory;

    private String urlPkgPrefixes;
    
    private String contextPathPrefix = "java:comp/env/";

    private Boolean isJNDIAvailable;
    
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
        Object obj = lookupInContext(key);
        try {
            return (String) obj;
        } catch (ClassCastException e) {
            LOG.warn("Wrong Type: " + obj.getClass().getName() + " for Key: " + key);
            LOG.debug(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Set or replace a new Value for the given key.<br />
     * If set or replace a value is not possible because of a readonly JNDI-Context,
     * then {@link Constants#SETTING_NOT_POSSIBLE} must be returned.
     * If set or replace was successful, then {@link Constants#SETTING_SUCCESS} must be returned.
     * 
     * @param key the Key for the configuration-property. e.g.: "com/mycompany/myapp/myParameterKey"
     * @param value the new Object-Value for the given key 
     * @return Returns {@link Constants#SETTING_SUCCESS} or {@link Constants#SETTING_NOT_POSSIBLE}
     */
    public int setObject(String key, Object value) {
        return rebindToContext(key, value);
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
    
    /**
     * check if a JNDI context is available and sets the internal Flag setIsJNDIAvailable(Boolean).
     * <p>
     * If the internal Flag IsJNDIAvailable is <code>False</code> this Connector is disabled.
     * 
     * @return true if a JNDI Context could be initialized.
     */
    public boolean isJNDIAvailable() {
        if (isJNDIAvailable == null) {
            try {
                getJNDIContext().lookup(getContextPathPrefix());
                LOG.info("JNDI Context is available.");
                isJNDIAvailable = Boolean.TRUE;
            } catch (NoInitialContextException e) {
                LOG.info("No JNDI Context available! JNDIConnector will be disabled: " + e.getMessage());
                isJNDIAvailable = Boolean.FALSE;
            } catch (NamingException e) {
                LOG.info("JNDI Context is available but " + e.getMessage());
                LOG.debug("NamingException in isJNDIAvailable: " + e.getMessage(), e);
                isJNDIAvailable = Boolean.TRUE;
            }
        }
        
        return isJNDIAvailable.booleanValue();
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
        if (!isJNDIAvailable()) {
            return null;
        }
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
    
    /**
     * @param key the JNDI-Key (will be normalized: add contextPathPrefix, replace '\' with '/').
     * @param value the JNDI-Value.
     * @return Constants.SETTING_NOT_POSSIBLE if the JNDI Context ist readonly.
     */
    public int rebindToContext(String key, Object value) {
        // don't do a check, but use the result if a check was done.
        if (BooleanUtils.isFalse(isJNDIAvailable)) {
            // only if isJNDIAvailable() was called an evaluated to false.
            return Constants.SETTING_NOT_POSSIBLE;
        }
        String normalizedKey = normalizeKey(key);
        if (LOG.isDebugEnabled()){
            LOG.debug("Try to rebind Key '" + key + "' (" + normalizedKey + ")" + " with value: " + value);
        }
        InitialContext ctx = null;
        int result = Constants.SETTING_NOT_POSSIBLE;
        try {
            ctx = getJNDIContext();
            createParentContext(ctx, normalizedKey);
            ctx.rebind(normalizedKey, value);
            result = Constants.SETTING_SUCCESS;
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

        if (LOG.isDebugEnabled()) {
            LOG.debug("createParentContext: " + key);
        }
        String[] path = key.split("/");

        int lastIndex = path.length - 1;

        Context tmpCtx = ctx;

        for (int i = 0; i < lastIndex; i++) {
            Object obj = null;
            try {
                obj = tmpCtx.lookup(path[i]);
            } catch (NameNotFoundException e) {
                // obj is null and subcontext must be generated.
            }
            
            if (obj == null) {
                tmpCtx = tmpCtx.createSubcontext(path[i]);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("createSubcontext: " + path[i]);
                }
            } else if (obj instanceof Context) {
                tmpCtx = (Context) obj;
            } else {
                throw new RuntimeException("Illegal node/branch clash. At branch value '" + path[i]
                    + "' an Object was found: " + obj);
            }
        }
    }
    
    private String normalizeKey(final String key){
        if (key == null){
            return null;
        }
        String normalizeKey = key;
        if (normalizeKey.startsWith(contextPathPrefix)){
            return normalizeKey;
        }
        
        normalizeKey = normalizeKey.replace('\\', '/');
        
        if (normalizeKey.startsWith("/")){
            normalizeKey = normalizeKey.substring(1);
        }
        return contextPathPrefix + normalizeKey;
    }
    
    public String getContextPathPrefix() {
        return contextPathPrefix;
    }

    public void setContextPathPrefix(String contextPathPrefix) {
        this.contextPathPrefix = contextPathPrefix;
    }

    protected Boolean getIsJNDIAvailable() {
        return isJNDIAvailable;
    }

    protected void setIsJNDIAvailable(Boolean isJNDIAvailable) {
        this.isJNDIAvailable = isJNDIAvailable;
    }
    
}
