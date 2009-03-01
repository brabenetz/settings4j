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

package org.settings4j.exception;

import java.text.MessageFormat;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.settings4j.Constants;

public class Settings4jException extends RuntimeException {
    private static final long serialVersionUID = -624459285533673984L;
    
    private String key;
    private Object[] args;

    public Object[] getArgs() {
        return args;
    }

    public String getKey() {
        return key;
    }

    public Settings4jException(String key) {
        super(getMessage(key, null));
        this.key = key;
        this.args = new Object[0] ;
    }

    public Settings4jException(String key, Throwable cause) {
        super(getMessage(key, null), cause);
        this.key = key;
        this.args = new Object[0] ;
    }

    public Settings4jException(String key, Object[] args, Throwable cause) {
        super(getMessage(key, args), cause);
        this.key = key;
        this.args = args;
    }

    public Settings4jException(String key, Object[] args) {
        super(getMessage(key, args));
        this.key = key;
        this.args = args;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        ToStringBuilder result = new ToStringBuilder(this).append("key", this.key);
        for (int i = 0; i < args.length; i++) {
            result.append("Param" + i, args[i]);
        }
        return result.toString();
    }

    protected static String getMessage(String key, Object[] args) {
        // Localize ExceptionMessage
        if (args == null){
            return Constants.SETTINGS4J_MESSAGES.getString(key);
        }
        String messagePattern = Constants.SETTINGS4J_MESSAGES.getString(key);
        // escape single quote like in Struts
        messagePattern = escape(messagePattern);
        String message = MessageFormat.format(messagePattern, args);
        return message;

    }

    /**
     * COMMENT from harald brabenetz 13.02.2008 : this function copied from
     * org.apache.struts.util.MessageResources <br />
     * 
     * Escape any single quote characters that are included in the specified message string.
     * 
     * @param string The string to be escaped
     */
    protected static String escape(String string) {

        if ((string == null) || (string.indexOf('\'') < 0)) {
            return string;
        }

        int n = string.length();
        StringBuffer sb = new StringBuffer(n);

        for (int i = 0; i < n; i++) {
            char ch = string.charAt(i);

            if (ch == '\'') {
                sb.append('\'');
            }

            sb.append(ch);
        }

        return sb.toString();

    }
}
