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

import java.util.Properties;


/**
 * Summy SessionFactory to demonstrate an example who a {@link Properties} Object can be read by Settings4j an set by
 * the SpringFramework.
 * 
 * @author <a href="mailto:harald.brabenetz@infonova.com">Harald Brabenetz (hbrabenetz)</a>
 */
public class DummySessionFactory {

    private Properties hibernateProperties;

    public Properties getHibernateProperties() {
        return this.hibernateProperties;
    }

    public void setHibernateProperties(final Properties hibernateProperties) {
        this.hibernateProperties = hibernateProperties;
    }

}
