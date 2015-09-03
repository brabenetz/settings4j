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

package org.settings4j.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.Transformer;

/**
 * This Transformer implements the function for a {@link org.apache.commons.collections4.map.LazyMap}
 * with Key=String (=RegEx) and Value=Boolean (=Result).
 *
 * <pre>
 * Example:
 *
 * &lt;%
 * request.setAttribute("matchPatternMap", LazyMap.decorate(new HashMap(), new MatchPatternTransformer("testString")));
 * %&gt;
 * &lt;html&gt;
 *   &lt;head&gt;&lt;/head&gt;
 *   &lt;body&gt;
 *     ${matchPatternMap['testString']} == true &lt;br /&gt;
 *     ${matchPatternMap['.*String']} == true &lt;br /&gt;
 *     ${matchPatternMap['test.*']} == true &lt;br /&gt;
 *     ${matchPatternMap['.*string']} == false &lt;br /&gt;
 *   &lt;/body&gt;
 * &lt;html&gt;
 * </pre>
 *
 * @see java.util.regex.Pattern
 * @author Harald.Brabenetz
 */
public class MatchPatternTransformer implements Transformer<String, Boolean> {

    /** General Logger for this Class. */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MatchPatternTransformer.class);

    private final String compareValue;

    /**
     * @param compareValue The String which should be Compared with a RegEx. see {@link Pattern}.
     */
    public MatchPatternTransformer(final String compareValue) {
        super();
        this.compareValue = compareValue;
    }


    /** {@inheritDoc} */
    public Boolean transform(final String patternString) {
        Boolean result = Boolean.FALSE;
        if (patternString != null) {
            try {
                final Pattern pattern = Pattern.compile(patternString);
                final Matcher matcher = pattern.matcher(this.compareValue);
                if (matcher.matches()) {
                    result = Boolean.TRUE;
                    LOG.debug("TRUE - found '{}' in '{}'", patternString, this.compareValue);
                } else {
                    result = Boolean.FALSE;
                    LOG.debug("FALSE - do not found '{}' in '{}'", patternString, this.compareValue);
                }
            } catch (final Exception e) {
                LOG.warn("Cann't matche Pattern '{}' with compareValue '{}'", patternString, this.compareValue, e);
                result = Boolean.FALSE;
            }
        }
        return result;
    }

}
