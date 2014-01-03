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
package org.settings4j.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.settings4j.Filter;

/**
 * The Default Implementation uses the RegEx-{@link Pattern} Expressions to evaluate the includes and excludes.
 * 
 * @author Harald.Brabenetz
 */
public class DefaultFilter implements Filter {

    /** General Logger for this Class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(DefaultFilter.class);
    private final List<Pattern> includePatterns = new ArrayList<Pattern>();
    private final List<Pattern> excludePatterns = new ArrayList<Pattern>();

    /** {@inheritDoc} */
    public void addExclude(final String pattern) {
        try {
            final Pattern p = Pattern.compile(pattern);
            this.excludePatterns.add(p);
        } catch (final Exception e) {
            LOG.warn("cannnot compile Pattern-String '" + pattern + "'" + e.getMessage(), e);
        }
    }

    /** {@inheritDoc} */
    public void addInclude(final String pattern) {
        try {
            final Pattern p = Pattern.compile(pattern);
            this.includePatterns.add(p);
        } catch (final Exception e) {
            LOG.warn("cannnot compile Pattern-String '" + pattern + "'" + e.getMessage(), e);
        }
    }

    /** {@inheritDoc} */
    public boolean isValid(final String key) {
        // if exclude match, return always false.
        for (final Pattern pattern : this.excludePatterns) {
            if (pattern.matcher(key).matches()) {
                return false;
            }
        }

        // if no include is defined, return always true
        if (this.includePatterns.isEmpty()) {
            return true;
        }

        // if include match, return true.
        for (final Pattern pattern : this.includePatterns) {
            if (pattern.matcher(key).matches()) {
                return true;
            }
        }

        // no includePattern matched.
        return false;
    }

}
