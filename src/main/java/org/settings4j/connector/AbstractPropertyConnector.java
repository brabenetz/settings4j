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

/**
 * Basic Implementation of {@link org.settings4j.Connector}s whiche are Property-related.
 * <p>
 * Only #getProperty(String, String) must be implemented. Example implementations are {@link PropertyFileConnector} or {@link SystemPropertyConnector}.
 * </p>
 *
 * @author Harald.Brabenetz
 */
public abstract class AbstractPropertyConnector extends AbstractConnector {

    /** {@inheritDoc} */
    // SuppressWarnings PMD.ReturnEmptyArrayRatherThanNull: returning null for this byte-Arrays is OK.
    @SuppressWarnings("PMD.ReturnEmptyArrayRatherThanNull")
    public byte[] getContent(final String key) {
        final String path = getString(key);
        if (path != null && getContentResolver() != null) {
            return getContentResolver().getContent(path);
        }
        // else
        return null;

    }

    /** {@inheritDoc} */
    public Object getObject(final String key) {
        final String path = getString(key);
        if (path != null && getObjectResolver() != null) {
            return getObjectResolver().getObject(path, getContentResolver());
        }
        // else
        return null;

    }
}
