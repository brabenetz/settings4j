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

import java.util.HashSet;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.settings4j.Settings4j;
import org.springframework.beans.factory.BeanDefinitionStoreException;
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
    @Override
    protected String resolvePlaceholder(final String placeholder, final Properties props) {

        String value = Settings4j.getString(prefix + placeholder);
        if (value == null) {
            value = props.getProperty(prefix + placeholder);
            if (value == null) {
                value = props.getProperty(placeholder);
            }
        }
        return value;
    }

    /**
     * Parse the given String with Placeholder "${...}" and returns the result.
     * <p>
     * Placeholders will be resolved with Settings4j.
     * 
     * @param strVal the String with the Paceholders
     * @return the parsed String
     * @throws BeanDefinitionStoreException
     *            if invalid values are encountered (Placeholders where no values where found).
     */
    public static String parseStringValue(final String strVal)
            throws BeanDefinitionStoreException {
        return parseStringValue(strVal, new Properties());
    }

    /**
     * Parse the given String with Placeholder "${...}" and returns the result.
     * <p>
     * Placeholders will be resolved with Settings4j.
     * 
     * @param strVal the String with the Paceholders
     * @param props The default Properties if no Value where found
     * @return the parsed String
     * @throws BeanDefinitionStoreException
     *            if invalid values are encountered (Placeholders where no values where found).
     */
    public static String parseStringValue(final String strVal, final Properties props)
            throws BeanDefinitionStoreException {
        return parseStringValue(strVal, StringUtils.EMPTY, props);
    }

    /**
     * Parse the given String with Placeholder "${...}" and returns the result.
     * <p>
     * Placeholders will be resolved with Settings4j.
     * 
     * @param strVal the String with the Paceholders
     * @param prefix for all placehodlers.
     * @return the parsed String
     * @throws BeanDefinitionStoreException
     *            if invalid values are encountered (Placeholders where no values where found).
     */
    public static String parseStringValue(final String strVal, final String prefix)
            throws BeanDefinitionStoreException {
        return parseStringValue(strVal, prefix, new Properties());
    }

    /**
     * Parse the given String with Placeholder "${...}" and returns the result.
     * <p>
     * A Prefix for all Placeholders can be defined.
     * e.g.: with the prefix "a/b/" the placeholder ${x} will be parsed as ${a/b/x})
     * <p>
     * Placeholders will be resolved with Settings4j.
     * 
     * @param strVal the String with the Placeholders.
     * @param prefix for all placehodlers.
     * @param props The default Properties if no Value where found
     * @return the parsed String
     * @throws BeanDefinitionStoreException
     *            if invalid values are encountered (Placeholders where no values where found).
     */
    public static String parseStringValue(final String strVal, final String prefix, final Properties props)
            throws BeanDefinitionStoreException {
        return createInstance(prefix).parseStringValue(strVal, props, new HashSet());
    }

    private static Settings4jPlaceholderConfigurer createInstance(final String prefix) {
        final Settings4jPlaceholderConfigurer placeholderConfigurer = new Settings4jPlaceholderConfigurer();
        placeholderConfigurer.setPrefix(prefix);
        return placeholderConfigurer;
    }


}
