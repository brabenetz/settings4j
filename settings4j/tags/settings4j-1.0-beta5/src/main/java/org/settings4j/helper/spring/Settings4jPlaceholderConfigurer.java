/* ***************************************************************************
 * Copyright (c) 2011 Brabenetz Harald, Austria.
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
package org.settings4j.helper.spring;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.settings4j.Settings4j;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * Subclass of PropertyPlaceholderConfigurer uses the Settings4j API.
 * <p>
 * You can also configure a Prefix if
 * 
 * @see Settings4j#getString(String)
 */
public class Settings4jPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    private String prefix = StringUtils.EMPTY;

    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }

    /** {@inheritDoc} */
    protected String resolvePlaceholder(final String placeholder, final Properties props) {

        String value = Settings4j.getString(this.prefix + placeholder);
        if (value == null) {
            value = props.getProperty(this.prefix + placeholder);
            if (value == null) {
                value = props.getProperty(placeholder);
            }
        }
        return value;
    }


}
