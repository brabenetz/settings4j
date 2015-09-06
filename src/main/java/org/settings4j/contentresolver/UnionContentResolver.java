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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.settings4j.ContentResolver;

/**
 * The UnionContentResolver can be an container for many other {@link ContentResolver} which will be processed in sequence.
 * <p>
 * the first result of a ContentResolver which are a not-null value will be used as result.
 * </p>
 *
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
     * @param contentResolver
     *        with the first ContentResolver.
     */
    public UnionContentResolver(final ContentResolver contentResolver) {
        super();
        addContentResolverInternal(contentResolver);
    }

    /** {@inheritDoc} */
    public void addContentResolver(final ContentResolver contentResolver) {
        synchronized (this) {
            addContentResolverInternal(contentResolver);
        }
    }

    private void addContentResolverInternal(final ContentResolver contentResolver) {
        final ContentResolver[] contentResolversNew = new ContentResolver[this.contentResolvers.length + 1];
        for (int i = 0; i < this.contentResolvers.length; i++) {
            contentResolversNew[i] = this.contentResolvers[i];
        }
        contentResolversNew[this.contentResolvers.length] = contentResolver;

        this.contentResolvers = contentResolversNew;
    }

    /** {@inheritDoc} */
    // SuppressWarnings PMD.ReturnEmptyArrayRatherThanNull: returning null for this byte-Arrays is OK.
    @SuppressWarnings("PMD.ReturnEmptyArrayRatherThanNull")
    public byte[] getContent(final String key) {
        byte[] result = null;
        for (final ContentResolver contentResolver : this.contentResolvers) {
            result = contentResolver.getContent(key);
            if (result != null) {
                return result;
            }
        }
        return result;
    }

    public ContentResolver[] getContentResolvers() {
        return ArrayUtils.clone(this.contentResolvers);
    }

}
