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

package org.settings4j.helper.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


/**
 * {@link javax.servlet.ServletContextListener} to initialize the {@link DefaultPropertiesLoader}.
 * 
 * @author brabenetz
 */
public class DefaultPropertiesLoaderListener implements ServletContextListener {

    /** {@inheritDoc} */
    public void contextInitialized(final ServletContextEvent event) {
        new DefaultPropertiesLoader().initDefaultProperties(event.getServletContext());
    }


    /** {@inheritDoc} */
    public void contextDestroyed(final ServletContextEvent event) {
        // do nothing
    }

}
