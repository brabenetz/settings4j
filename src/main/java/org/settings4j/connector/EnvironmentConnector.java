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

import java.util.Locale;

/**
 * The Environment variable implementation of an {@link org.settings4j.Connector}.
 * <p>
 * see also {@link System#getenv(String)}.
 * </p>
 *
 * @author Harald.Brabenetz
 */
public class EnvironmentConnector extends AbstractPropertyConnector {

    /** General Logger for this Class. */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(EnvironmentConnector.class);

    /**
     * Very similar to <code>System.getenv</code> except that the {@link SecurityException} is hidden.
     *
     * @param key The key to search for.
     * @return the string value of the Environment variable, or the default value if there is no property with that key.
     */
    @Override
    public String getString(final String key) {
        final String value = getEnv(key);
        if (value == null) {
            final String uppercaseKey = key.toUpperCase(Locale.ENGLISH).replaceAll("\\W", "_");
            LOG.debug("Try to find value for KEY: {}", uppercaseKey);
            return getEnv(uppercaseKey);
        }
        return value;
    }

    private String getEnv(final String key) {
        try {
            return System.getenv(key);
        } catch (final SecurityException e) {
            LOG.info("Was not allowed to read environment value for key '{}'.", key, e);
            return null;
        } catch (final Throwable e) {
            LOG.warn("Exception reading environment value for key '{}': {}", key, e.getMessage());
            return null;
        }
    }
}
