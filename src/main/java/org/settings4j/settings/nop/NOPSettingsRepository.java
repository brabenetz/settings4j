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
package org.settings4j.settings.nop;

import org.settings4j.Settings4jFactory;
import org.settings4j.Settings4jInstance;
import org.settings4j.Settings4jRepository;

public class NOPSettingsRepository implements Settings4jRepository {

    public Settings4jInstance getSettings() {
        return new NOPSettings(this);
    }

    public Settings4jInstance getSettings(Settings4jFactory factory) {
        return new NOPSettings(this);
    }

    public int getConnectorCount() {
        return 0;
    }

    public void resetConfiguration() {
        // do nothing in NOP-Implementation
    }

}
