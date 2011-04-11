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
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

public class SpringConfigObjectResolver extends AbstractObjectResolver{

    /** General Logger for this Class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(SpringConfigObjectResolver.class);
    
    private static final String PROPERTY_BEAN_REF = "bean-ref";

    protected Object contentToObject(String key, Properties properties, byte[] content, ContentResolver contentResolver) {
        AbstractApplicationContext context = new ByteArrayXMLApplicationContext(content);
        context.refresh();
        
        String beanRef = properties.getProperty(PROPERTY_BEAN_REF);
        if (StringUtils.isEmpty(beanRef)) {
            beanRef = key.replace('/', '.');
        }
        
        Object result = context.getBean(beanRef);
        if (result == null) {
            LOG.warn("SpringContext-Object with ID '" + beanRef + "' for Key '" + key + "' is null!");
        }
        context.close();
        return result;
    }

    private static class ByteArrayXMLApplicationContext extends AbstractXmlApplicationContext{

        private Resource[] configResources;

        public ByteArrayXMLApplicationContext(byte[] content) {
            super();
            this.configResources = new Resource[1];
            this.configResources[0] = new ByteArrayResource(content);
        }

        protected Resource[] getConfigResources() {
            return this.configResources;
        }
    }
    
}
