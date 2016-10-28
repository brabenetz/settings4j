/*
 * #%L
 * settings4j
 * ===============================================================
 * Copyright (C) 2008 - 2016 Brabenetz Harald, Austria
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
package org.settings4j.settings;

import org.settings4j.Settings4jFactory;
import org.settings4j.Settings4jInstance;

/**
 * This default Factory uses the {@link DefaultSettings} Implementation.
 * 
 * @author Harald.Brabenetz
 *
 */
public class DefaultSettingsFactory implements Settings4jFactory {

    @Override
    public Settings4jInstance makeNewSettingsInstance() {
        return new DefaultSettings();
    }

}
