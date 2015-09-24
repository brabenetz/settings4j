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

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.settings4j.Constants;

/**
 * The JNDI Context implementation of an {@link org.settings4j.Connector}.
 * <h3>Normal Use</h3>
 * <p>
 * This JNDI connector is used in the default settings4j-config:
 * </p>
 *
 * <pre>
 * &lt;connector name="JNDIConnector"
 *     class="org.settings4j.connector.JNDIConnector"&gt;
 *     &lt;contentResolver-ref ref="DefaultContentResolver" /&gt;
 *     &lt;objectResolver-ref ref="DefaultObjectResolver" /&gt;
 * &lt;/connector&gt;
 * </pre>
 * <p>
 * During the first use it will check if JNDI is accessible. If no JNDI context exists, The connector will deactivate itself. A INFO-Log message will print this
 * information.
 * </p>
 * <p>
 * The default contextPathPrefix is "java:comp/env/". This JNDI Connector will first check if a value for <code>"contextPathPrefix + key"</code> exists and
 * second if a value for the <code>"key"</code> only exists.
 * </p>
 * <h3>Custom Use</h3>
 * <p>
 * You can also configure the JNDI Connector to connect to another JNDI Context as the default one.
 * </p>
 *
 * <pre>
 * &lt;connector name="JNDIConnector"
 *     class="org.settings4j.connector.JNDIConnector"&gt;
 *     &lt;param name="initialContextFactory" value="org.apache.naming.java.javaURLContextFactory"/&gt;
 *     &lt;param name="providerUrl" value="localhost:1099"/&gt;
 *     &lt;param name="urlPkgPrefixes" value="org.apache.naming"/&gt;
 * &lt;/connector&gt;
 * </pre>
 * <p>
 * All three parameters must be set "initialContextFactory", "providerUrl", "urlPkgPrefixes" if you want use another JNDI Context.
 * </p>
 * <h3>getString(), getContent(), getObject()</h3>
 * <h4>getString()</h4>
 * <p>
 * If the getString() JNDI lookup returns an Object which isn't a String, a WARN-Log message will be printed.
 * </p>
 * <h4>getContent()</h4>
 * <p>
 * If the getContent() JNDI lookup returns a String it will try to get a byte-Array Content from the ContentResolvers (assuming the String is as FileSystemPath
 * or ClassPath). <br>
 * Else if the getContent() JNDI lookup returns an Object which isn't a byte[], a WARN-Log message will be printed.
 * </p>
 * <h4>getObject()</h4>
 * <p>
 * If the getObject() JNDI lookup returns a String it will try to get an Object from the ObjectResolvers (assuming the String is as FileSystemPath or ClassPath
 * which can be resolved to an Object).
 * </p>
 *
 * @author Harald.Brabenetz
 */
public class JNDIConnector extends AbstractConnector {

    /** General Logger for this Class. */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(JNDIConnector.class);

    private String providerUrl;

    private String initialContextFactory;

    private String urlPkgPrefixes;

    private String contextPathPrefix = "java:comp/env/";

    private Boolean isJNDIAvailable;

    @Override
    // SuppressWarnings PMD.ReturnEmptyArrayRatherThanNull: returning null for this byte-Arrays is OK.
    @SuppressWarnings("PMD.ReturnEmptyArrayRatherThanNull")
    public byte[] getContent(final String key) {
        Validate.notNull(key);
        final Object obj = lookupInContext(key);
        if (obj == null) {
            return null;
        }

        // if obj is a String and an Object resolver is available
        // obj could be a Path.
        if (obj instanceof String && getContentResolver() != null) {
            final byte[] content = getContentResolver().getContent((String) obj);
            if (content != null) {
                return content;
            }
        }

        if (obj instanceof byte[]) {
            return (byte[]) obj;
        }

        LOG.warn("Wrong Type: {} for Key: {}", obj.getClass().getName(), key);
        return null;
    }

    @Override
    public Object getObject(final String key) {
        Validate.notNull(key);
        final Object obj = lookupInContext(key);

        // if obj is a String and an Object resolver is available
        // obj could be a Path to a XML who can be converted to an Object.
        if (obj instanceof String && getObjectResolver() != null) {
            final Object convertedObject = getObjectResolver().getObject((String) obj, getContentResolver());
            if (convertedObject != null) {
                return convertedObject;
            }
        }

        return obj;
    }

    @Override
    public String getString(final String key) {
        Validate.notNull(key);
        final Object obj = lookupInContext(key);
        try {
            return (String) obj;
        } catch (final ClassCastException e) {
            logInfoButExceptionDebug(String.format("Wrong Type: %s for Key: %s", obj.getClass().getName(), key), e);
            return null;
        }
    }

    /**
     * Set or replace a new Value for the given key.<br>
     * If set or replace a value is not possible because of a readonly JNDI-Context, then {@link Constants#SETTING_NOT_POSSIBLE} must be returned. If set or
     * replace was successful, then {@link Constants#SETTING_SUCCESS} must be returned.
     *
     * @param key
     *        the Key for the configuration-property (will not be normalized: add contextPathPrefix, replace '\' with '/'). e.g.:
     *        "com\mycompany\myapp\myParameterKey" =&gt; "java:comp/env/com/mycompany/myapp/myParameterKey"
     * @param value
     *        the new Object-Value for the given key
     * @return Returns {@link Constants#SETTING_SUCCESS} or {@link Constants#SETTING_NOT_POSSIBLE}
     */
    public int setObject(final String key, final Object value) {
        return rebindToContext(normalizeKey(key), value);
    }

    private InitialContext getJNDIContext() throws NamingException {
        InitialContext initialContext;

        if (StringUtils.isEmpty(this.providerUrl) && StringUtils.isEmpty(this.initialContextFactory)
            && StringUtils.isEmpty(this.urlPkgPrefixes)) {

            initialContext = new InitialContext();
        } else {
            final Properties prop = new Properties();
            prop.put(Context.PROVIDER_URL, this.providerUrl);
            prop.put(Context.INITIAL_CONTEXT_FACTORY, this.initialContextFactory);
            prop.put(Context.URL_PKG_PREFIXES, this.urlPkgPrefixes);
            initialContext = new InitialContext(prop);
        }

        return initialContext;
    }

    /**
     * check if a JNDI context is available and sets the internal Flag setIsJNDIAvailable(Boolean).
     * <p>
     * If the internal Flag IsJNDIAvailable is <code>False</code> this Connector is disabled.
     * </p>
     *
     * @return true if a JNDI Context could be initialized.
     */
    public boolean isJNDIAvailable() {
        if (this.isJNDIAvailable == null) {
            try {
                getJNDIContext().lookup(getContextPathPrefix());
                LOG.debug("JNDI Context is available.");
                this.isJNDIAvailable = Boolean.TRUE;
            } catch (final NoInitialContextException e) {
                LOG.info("No JNDI Context available! JNDIConnector will be disabled: {}", e.getMessage());
                this.isJNDIAvailable = Boolean.FALSE;
            } catch (final NamingException e) {
                logInfoButExceptionDebug(String.format("JNDI Context is available but %s", e.getMessage()), e);
                this.isJNDIAvailable = Boolean.TRUE;
            }
        }

        return this.isJNDIAvailable.booleanValue();
    }

    public void setProviderUrl(final String providerUrl) {
        this.providerUrl = providerUrl;
    }

    public void setInitialContextFactory(final String initialContextFactory) {
        this.initialContextFactory = initialContextFactory;
    }

    public void setUrlPkgPrefixes(final String urlPkgPrefixes) {
        this.urlPkgPrefixes = urlPkgPrefixes;
    }

    private Object lookupInContext(final String key) {
        return lookupInContext(key, true);
    }

    private Object lookupInContext(final String key, final boolean withPrefix) {
        if (!isJNDIAvailable()) {
            return null;
        }
        final String normalizedKey = normalizeKey(key, withPrefix);
        InitialContext ctx = null;
        Object result = null;
        try {
            ctx = getJNDIContext();
            result = ctx.lookup(normalizedKey);
        } catch (final NoInitialContextException e) {
            logInfoButExceptionDebug(String.format("Maybe no JNDI-Context available. %s", e.getMessage()), e);
        } catch (final NamingException e) {
            LOG.debug("cannot lookup key: {} ({})", key, normalizedKey, e);
            if (withPrefix) {
                result = lookupInContext(key, false);
            }
        } finally {
            closeQuietly(ctx);
        }
        return result;
    }

    /**
     * Calls {@link InitialContext#close()} with null-checks and Exception handling: log exception with log level info.
     * 
     * @param ctx
     *        the InitialContextto close ( )
     */
    protected void closeQuietly(final InitialContext ctx) {
        if (ctx != null) {
            try {
                ctx.close();
            } catch (final NamingException e) {
                LOG.info("cannot close context: {}", ctx, e);
            }
        }
    }

    /**
     * @param key the JNDI-Key (will NOT be normalized).
     * @param value the JNDI-Value.
     * @return Constants.SETTING_NOT_POSSIBLE if the JNDI Context ist readonly.
     */
    public int rebindToContext(final String key, final Object value) {
        // don't do a check, but use the result if a check was done.
        if (BooleanUtils.isFalse(this.isJNDIAvailable)) {
            // only if isJNDIAvailable() was called an evaluated to false.
            return Constants.SETTING_NOT_POSSIBLE;
        }

        LOG.debug("Try to rebind Key '{}' with value: {}", key, value);

        InitialContext ctx = null;
        int result = Constants.SETTING_NOT_POSSIBLE;
        try {
            ctx = getJNDIContext();
            createParentContext(ctx, key);
            ctx.rebind(key, value);
            result = Constants.SETTING_SUCCESS;
        } catch (final NoInitialContextException e) {
            logInfoButExceptionDebug(String.format("Maybe no JNDI-Context available. %s", e.getMessage()), e);
        } catch (final NamingException e) {
            // the JNDI-Context from TOMCAT is readonly
            // if you try to write it, The following Exception will be thrown:
            // javax.naming.NamingException: Context is read only
            logInfoButExceptionDebug(String.format("cannot bind key: '%s'. %s", key, e.getMessage()), e);
        } finally {
            closeQuietly(ctx);
        }
        return result;
    }

    private static void createParentContext(final Context ctx, final String key) throws NamingException {
        // here we need to break by the specified delimiter

        LOG.debug("createParentContext: {}", key);

        final String[] path = key.split("/");

        final int lastIndex = path.length - 1;

        Context tmpCtx = ctx;

        for (int i = 0; i < lastIndex; i++) {
            Object obj = null;
            try {
                obj = tmpCtx.lookup(path[i]);
            } catch (@SuppressWarnings("unused") final NameNotFoundException e) {
                LOG.debug("obj is null and subcontext will be generated: {}", path[i]);
            }

            if (obj == null) {
                tmpCtx = tmpCtx.createSubcontext(path[i]);
                LOG.debug("createSubcontext: {}", path[i]);
            } else if (obj instanceof Context) {
                tmpCtx = (Context) obj;
            } else {
                throw new RuntimeException("Illegal node/branch clash. At branch value '" + path[i]
                    + "' an Object was found: " + obj);
            }
        }
    }

    private String normalizeKey(final String key) {
        return normalizeKey(key, true);
    }

    private String normalizeKey(final String key, final boolean withPrefix) {
        Validate.notNull(key);
        String normalizeKey = key;
        if (normalizeKey.startsWith(this.contextPathPrefix)) {
            return normalizeKey;
        }

        normalizeKey = normalizeKey.replace('\\', '/');

        if (startsWithSlash(normalizeKey)) {
            normalizeKey = normalizeKey.substring(1);
        }
        if (withPrefix) {
            return this.contextPathPrefix + normalizeKey;
        }
        return normalizeKey;
    }

    private boolean startsWithSlash(final String normalizeKey) {
        return normalizeKey.charAt(0) == '/';
    }

    public String getContextPathPrefix() {
        return this.contextPathPrefix;
    }

    public void setContextPathPrefix(final String contextPathPrefix) {
        this.contextPathPrefix = contextPathPrefix;
    }

    protected Boolean getIsJNDIAvailable() {
        return this.isJNDIAvailable;
    }

    protected void setIsJNDIAvailable(final Boolean isJNDIAvailable) {
        this.isJNDIAvailable = isJNDIAvailable;
    }

    /**
     * @param message
     *        The message.
     * @param exc
     *        {@link Exception}
     */
    protected void logInfoButExceptionDebug(final String message, final Exception exc) {
        LOG.info(message);
        LOG.debug(message, exc);
    }
}
