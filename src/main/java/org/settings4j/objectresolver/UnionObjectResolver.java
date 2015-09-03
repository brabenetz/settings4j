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
import org.settings4j.ObjectResolver;

/**
 * The UnionObjectResolver can be an container for many other {@link ObjectResolver} which will be processed in sequence.
 * <p>
 * the first result of a ObjectResolver which are a not-null value will be used as result.
 * </p>
 *
 * @author Harald.Brabenetz
 */
public class UnionObjectResolver implements ObjectResolver {

    private ObjectResolver[] objectResolvers = new ObjectResolver[0];

    /** {@inheritDoc} */
    public void addObjectResolver(final ObjectResolver objectResolver) {

        final ObjectResolver[] objectResolversNew = new ObjectResolver[this.objectResolvers.length + 1];
        for (int i = 0; i < this.objectResolvers.length; i++) {
            objectResolversNew[i] = this.objectResolvers[i];
        }
        objectResolversNew[this.objectResolvers.length] = objectResolver;

        this.objectResolvers = objectResolversNew;
    }

    /** {@inheritDoc} */
    public Object getObject(final String key, final ContentResolver contentResolver) {

        Object result = null;
        for (int i = 0; i < this.objectResolvers.length; i++) {
            result = this.objectResolvers[i].getObject(key, contentResolver);
            if (result != null) {
                return result;
            }
        }
        return result;
    }

}
