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
package org.settings4j.contentresolver;

import org.settings4j.ContentResolver;
import org.settings4j.Filter;

/**
 * Wrapper to add a {@link Filter} which is used before the given {@link ContentResolver} is called.
 * 
 * @author Harald.Brabenetz
 *
 */
public class FilteredContentResolverWrapper implements ContentResolver {

    private final ContentResolver targetContentResolver;
    private final Filter filter;

    /**
     * @param targetContentResolver The {@link ContentResolver} where the settings
     *  should be read if the Filter allows it.
     * @param filter the {@link Filter} which defines if an key should be read from the given {@link ContentResolver}.
     */
    public FilteredContentResolverWrapper(final ContentResolver targetContentResolver, final Filter filter) {
        super();
        if (targetContentResolver == null) {
            throw new IllegalArgumentException("FilteredConnectorWrapper needs a ContentResolver Object");
        }
        if (filter == null) {
            throw new IllegalArgumentException("FilteredConnectorWrapper needs a Filter Object");
        }
        this.targetContentResolver = targetContentResolver;
        this.filter = filter;
    }

    /** {@inheritDoc} */
    public void addContentResolver(final ContentResolver contentResolver) {
        this.targetContentResolver.addContentResolver(contentResolver);
    }

    /** {@inheritDoc} */
    public byte[] getContent(final String key) {
        if (!this.filter.isValid(key)) {
            return null;
        }
        return this.targetContentResolver.getContent(key);
    }
}
