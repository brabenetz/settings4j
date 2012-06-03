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

package org.settings4j.objectresolver;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.settings4j.ContentResolver;
import org.settings4j.helper.spring.ByteArrayXMLApplicationContext;

/**
 * This implementation parses a Spring-Beans XML File and returns
 * the Object from the generated Spring Application Context.
 * <p>
 * Per Default the bean with id = key.replace('/', '.') will be returned.
 * But you can configure a key "bean-ref" in the {@link Properties}-File for the given Object.
 * 
 * @author Harald.Brabenetz
 */
public class SpringConfigObjectResolver extends AbstractObjectResolver {

    /** General Logger for this Class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(SpringConfigObjectResolver.class);

    private static final String PROPERTY_BEAN_REF = "bean-ref";

    /** {@inheritDoc} */
    protected Object contentToObject(final String key, final Properties properties, final byte[] content,
            final ContentResolver contentResolver) {

        String beanRef = properties.getProperty(PROPERTY_BEAN_REF);
        if (StringUtils.isEmpty(beanRef)) {
            beanRef = key.replace('/', '.');
        }

        final Object result = ByteArrayXMLApplicationContext.getBean(content, beanRef);
        if (result == null) {
            LOG.warn("SpringContext-Object with ID '" + beanRef + "' for Key '" + key + "' is null!");
        }
        return result;
    }

}
