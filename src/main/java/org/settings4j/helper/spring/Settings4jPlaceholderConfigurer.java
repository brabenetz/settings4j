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

import org.apache.commons.lang3.StringUtils;
import org.settings4j.Settings4j;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver;

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
     * @throws BeanDefinitionStoreException if invalid values are encountered (Placeholders where no values where
     *             found).
     */
    public static String parseStringValue(final String strVal) throws BeanDefinitionStoreException {
        return parseStringValue(strVal, StringUtils.EMPTY);
    }

    /**
     * Parse the given String with Placeholder "${...}" and returns the result.
     * <p>
     * Placeholders will be resolved with Settings4j.
     * 
     * @param strVal the String with the Paceholders
     * @param prefix for all placehodlers.
     * @return the parsed String
     * @throws BeanDefinitionStoreException if invalid values are encountered (Placeholders where no values where
     *             found).
     */
    public static String parseStringValue(final String strVal, final String prefix) //
            throws BeanDefinitionStoreException {
        return parseStringValue(strVal, prefix, new Properties());
    }

    /**
     * Parse the given String with Placeholder "${...}" and returns the result.
     * <p>
     * Placeholders will be resolved with Settings4j.
     * 
     * @param strVal the String with the Paceholders
     * @param prefix for all placehodlers.
     * @param props The default Properties if no Value where found
     * @return the parsed String
     * @throws BeanDefinitionStoreException if invalid values are encountered (Placeholders where no values where
     *             found).
     */
    public static String parseStringValue(final String strVal, final String prefix, final Properties props)
            throws BeanDefinitionStoreException {
        final PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper(DEFAULT_PLACEHOLDER_PREFIX,
            DEFAULT_PLACEHOLDER_SUFFIX, DEFAULT_VALUE_SEPARATOR, false);
        return helper.replacePlaceholders(strVal, new Settings4jPlaceholderConfigurerResolver(prefix, props));
    }

    /**
     * PlaceholderResolver implementation can be used for
     * {@link PropertyPlaceholderHelper#replacePlaceholders(String, PlaceholderResolver)}.
     * 
     * @author brabenetz
     */
    private static final class Settings4jPlaceholderConfigurerResolver implements PlaceholderResolver {

        private final String prefix;
        private final Properties props;


        private Settings4jPlaceholderConfigurerResolver(final String prefix, final Properties props) {
            this.prefix = prefix;
            this.props = props;
        }

        public String resolvePlaceholder(final String placeholderName) {
            final Settings4jPlaceholderConfigurer placeholderConfigurer = new Settings4jPlaceholderConfigurer();
            placeholderConfigurer.setPrefix(prefix);
            return placeholderConfigurer.resolvePlaceholder(placeholderName, props);
        }
    }

}
