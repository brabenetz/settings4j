/*
 * #%L
 * settings4j
 * ===============================================================
 * Copyright (C) 2008 - 2015 Brabenetz Harald, Austria
 * ===============================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.settings4j.helper.spring;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

/**
 * A SpringFramework Application Context which can process a spring-XML Content from byte[] as input.
 *
 * @author Harald.Brabenetz
 */
public class ByteArrayXMLApplicationContext extends AbstractXmlApplicationContext {

    private final Resource[] configResources;

    /**
     * Static method to get a single Bean from a Spring Configuration XML.
     *
     * @param content The Spring configuration XML as byte[].
     * @param beanName The bean name/identifier.
     * @return The Bean-Object or <code>null</code>.
     */
    public static Object getBean(final byte[] content, final String beanName) {

        final AbstractApplicationContext context = new ByteArrayXMLApplicationContext(content);
        context.refresh();

        final Object result = context.getBean(beanName);
        context.close();
        return result;
    }

    /**
     * @param content The Spring configuration XML as byte[].
     */
    public ByteArrayXMLApplicationContext(final byte[] content) {
        super();
        this.configResources = new Resource[1];
        this.configResources[0] = new ByteArrayResource(content);
    }

    @Override
    protected Resource[] getConfigResources() {
        return this.configResources;
    }
}
