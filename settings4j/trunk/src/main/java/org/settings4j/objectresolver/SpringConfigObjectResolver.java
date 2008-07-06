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

import org.settings4j.ContentResolver;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

public class SpringConfigObjectResolver extends AbstractObjectResolver{

    protected Object contentToObject(String key, Properties properties, byte[] content, ContentResolver contentResolver) {
        AbstractApplicationContext context = new ByteArrayXMLApplicationContext(content);
        context.refresh();
        Object result = context.getBean(key.replace('/', '.'));
        context.close();
        return result;
    }

    protected byte[] objectToContent(String key, Properties properties, Object value) {
        return null;
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
    };
    
}
