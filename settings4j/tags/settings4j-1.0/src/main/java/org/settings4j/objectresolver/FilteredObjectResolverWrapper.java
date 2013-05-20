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

import org.settings4j.ContentResolver;
import org.settings4j.Filter;
import org.settings4j.ObjectResolver;

/**
 * Wrapper to add a {@link Filter} which is used before the given {@link ObjectResolver} is called.
 * 
 * @author Harald.Brabenetz
 */
public class FilteredObjectResolverWrapper implements ObjectResolver {

    private final ObjectResolver targetObjectResolver;
    private final Filter filter;

    /**
     * 
     * @param targetObjectResolver The ObjectResolver where the settings should be read if the Filter allows it.
     * @param filter the {@link Filter} which defines if an key should be read from the given ObjectResolver.
     */
    public FilteredObjectResolverWrapper(final ObjectResolver targetObjectResolver, final Filter filter) {
        super();
        if (targetObjectResolver == null) {
            throw new IllegalArgumentException("FilteredConnectorWrapper needs a ObjectResolver Object");
        }
        if (filter == null) {
            throw new IllegalArgumentException("FilteredConnectorWrapper needs a Filter Object");
        }
        this.targetObjectResolver = targetObjectResolver;
        this.filter = filter;
    }

    /** {@inheritDoc} */
    public void addObjectResolver(final ObjectResolver objectResolver) {
        this.targetObjectResolver.addObjectResolver(objectResolver);
    }

    /** {@inheritDoc} */
    public Object getObject(final String key, final ContentResolver contentResolver) {
        if (!this.filter.isValid(key)) {
            return null;
        }
        return this.targetObjectResolver.getObject(key, contentResolver);
    }
}
