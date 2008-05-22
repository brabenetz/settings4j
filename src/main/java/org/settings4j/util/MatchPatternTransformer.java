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

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.map.LazyMap;

/**
 * This Transformer implements the function for a {@link LazyMap} with Key=String (=RegEx) and Value=Boolean (=Result)
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
 * 
 * @author Harald.Brabenetz
 *
 */
public class MatchPatternTransformer implements Transformer {

    /** General Logger for this Class */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(MatchPatternTransformer.class);
    
    private String compareValue;
    
    public MatchPatternTransformer(String compareValue) {
        super();
        this.compareValue = compareValue;
    }


    public Object transform(Object input) {
        Boolean result = Boolean.FALSE;
        if (input != null && input instanceof String){
            String patternString = input.toString();
            try {
                Pattern pattern = Pattern.compile(patternString);
                Matcher matcher = pattern.matcher(compareValue);
                if (matcher.matches()){
                    result = Boolean.TRUE;
                    if (LOG.isDebugEnabled()){
                        LOG.debug("TRUE '" + patternString + "'; '" + compareValue + "'");
                    }
                } else {
                    result = Boolean.FALSE;
                    if (LOG.isDebugEnabled()){
                        LOG.debug("FALSE '" + patternString + "'; '" + compareValue + "'");
                    }
                }
            } catch (Exception e) {
                LOG.warn("Cann't matche Pattern '" + patternString + "' with compareValue '" + compareValue + "'", e);
                result = Boolean.FALSE;
            }
        }
        return result;
    }

}
