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

import org.settings4j.ContentResolver;

/**
 * @author Harald.Brabenetz
 */
public class UnionContentResolver implements ContentResolver {

    private ContentResolver[] contentResolvers = new ContentResolver[0];

    /**
     * Default Constructor.
     */
    public UnionContentResolver() {
        super();
    }

    /**
     * Constructor with the first {@link ContentResolver}.
     * 
     * @param contentResolver with the first ContentResolver.
     */
    public UnionContentResolver(final ContentResolver contentResolver) {
        super();
        addContentResolver(contentResolver);
    }

    /** {@inheritDoc} */
    public synchronized void addContentResolver(final ContentResolver contentResolver) {

        final ContentResolver[] contentResolversNew = new ContentResolver[this.contentResolvers.length + 1];
        for (int i = 0; i < this.contentResolvers.length; i++) {
            contentResolversNew[i] = this.contentResolvers[i];
        }
        contentResolversNew[this.contentResolvers.length] = contentResolver;

        this.contentResolvers = contentResolversNew;
    }

    /** {@inheritDoc} */
    public byte[] getContent(final String key) {
        byte[] result = null;
        for (int i = 0; i < this.contentResolvers.length; i++) {
            result = this.contentResolvers[i].getContent(key);
            if (result != null) {
                return result;
            }
        }
        return result;
    }
}
