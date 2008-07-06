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
package org.settings4j;

import org.settings4j.contentresolver.ReadOnlyContentResolverWrapper;
import org.settings4j.contentresolver.UnionContentResolver;

/**
 * a ContentResolver is a Helper for read/write byte[] content for a given key.
 * 
 * <pre>
 * 
 * Example configuration in settings4j.xml:
 * --------------------------------------
 * &lt;contentResolver name="ClasspathContentResolver" class="org.settings4j.contentresolver.ClasspathContentResolver"&gt;
 * &lt;/contentResolver&gt;
 * --------------------------------------
 * 
 * </pre>
 * 
 * @author hbrabenetz
 *
 */
public interface ContentResolver {
    
    /**
     * Reads the Content for the given Key or null if nothing where found.
     * 
     * @param key The key
     * @return The byte[] Content or null if nothing where found.
     */
    byte[] getContent(String key);
    
    /**
     * Set a new byte[] Content for the given Key.
     * If the Implementation can write the Content,
     * then the Connector must return {@link Constants#SETTING_SUCCESS}<br />
     * 
     * If there was a Problem, or The Connector doesn't supports write-access,
     * then the Connector must return {@link Constants#SETTING_NOT_POSSIBLE}
     * 
     * @param key The Key for the new content
     * @param value The new byte[] Content for the given Key
     * @return {@link Constants#SETTING_SUCCESS} or {@link Constants#SETTING_NOT_POSSIBLE}
     */
    int setContent(String key, byte[] value);
    
    /**
     * Some Implementations of a {@link ContentResolver} are delegating the functionality
     * to other ContentResolvers.<br />
     * Examples are: {@link ReadOnlyContentResolverWrapper} or {@link UnionContentResolver}
     * 
     * <pre>
     * --------------------------------------
     * &lt;contentResolver name="DefaultContentResolver" class="org.settings4j.contentresolver.UnionContentResolver"&gt;
     *     &lt;contentResolver-ref ref="FSContentResolver" /&gt;
     *     &lt;contentResolver-ref ref="ClasspathContentResolver" /&gt;
     * &lt;/contentResolver&gt;
     * --------------------------------------
     * </pre>
     * 
     * @param contentResolver the original contentResolver to delegate.
     */
    void addContentResolver(ContentResolver contentResolver);
}
