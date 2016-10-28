/*
 * #%L
 * settings4j
 * ===============================================================
 * Copyright (C) 2008 - 2016 Brabenetz Harald, Austria
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
package org.settings4j;

/**
 * Interface to implement an cumied filter.
 * <p>
 * The default implementation is in {@link org.settings4j.settings.DefaultFilter}.
 * </p>
 * <p>
 * A simple "Never-Filter" whould look like:
 * </p>
 *
 * <pre>
 * /\u200b*\u200b*
 *  * The simplest default implementation doesn't filter.
 *  *\u200b/
 * public static final Filter NO_FILTER = new Filter() {
 *     public void addExclude(final String pattern) {
 *         throw new java.lang.IllegalStateException("This instance of Filter cannot be modified.");
 *     }
 *     public void addInclude(final String pattern) {
 *         throw new java.lang.IllegalStateException("This instance of Filter cannot be modified.");
 *     }
 *     public boolean isValid(final String key) {
 *         return true;
 *     }
 * };
 * </pre>
 *
 * @author Harald.Brabenetz
 */
public interface Filter {

    /**
     * Add an include Pattern (Which pattern-Syntax is determinate by the implementation).
     *
     * @param pattern the pattern.
     */
    void addInclude(String pattern);

    /**
     * Add an exclude Pattern (Which pattern-Syntax is determinate by the implementation).
     *
     * @param pattern the pattern.
     */
    void addExclude(String pattern);

    /**
     * Return true if the key is not filtered out.
     * @param key The key to check.
     * @return true if the key is not filtered out.
     */
    boolean isValid(String key);
}
