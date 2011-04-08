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
package org.settings4j.settings;

import org.settings4j.Settings4jFactory;
import org.settings4j.Settings4jInstance;
import org.settings4j.Settings4jRepository;

/**
 * This class is specialized in retrieving settings by name and also maintaining the settings
 * hierarchy.
 * 
 * <p>
 * <em>The casual user does not have to deal with this class
 * directly.</em>
 * 
 * <p>
 * The structure of the settings hierarchy is maintained by the {@link #getSettings} method. The
 * hierarchy is such that children link to their parent but parents do not have any pointers to
 * their children. Moreover, settings can be instantiated in any order, in particular descendant
 * before ancestor.
 * 
 * <p>
 * In case a descendant is created before a particular ancestor, then it creates a provision node
 * for the ancestor and adds itself to the provision node. Other descendants of the same ancestor
 * add themselves to the previously created provision node.
 * 
 * 
 * @author hbrabenetz
 * @author Ceki G&uuml;lc&uuml; (the original log4j org.apache.log4j.Hierarchy)
 * 
 */
public class DefaultSettingsRepository implements Settings4jRepository {

    private static final Settings4jFactory DEFAULT_FACTORY = new DefaultSettingsFactory();

    private Settings4jInstance settings;

    public DefaultSettingsRepository() {
        super();
    }

    public Settings4jInstance getSettings() {
        return getSettings(DEFAULT_FACTORY);
    }

    public Settings4jInstance getSettings(Settings4jFactory factory) {
        if (this.settings == null) {
        	this.settings = factory.makeNewSettingsInstance();
        }
        return this.settings;
    }

    /** {@inheritDoc} */
    public int getConnectorCount() {
        if (this.settings == null) {
            return 0;
        } else {
            return settings.getConnectors().size();
        }
    }

    /** {@inheritDoc} */
    public void resetConfiguration() {
        settings.removeAllConnectors();
    }
}
