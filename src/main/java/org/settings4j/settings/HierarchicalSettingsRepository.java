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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.settings4j.settings.HierarchicalSettings;
import org.settings4j.Settings;
import org.settings4j.SettingsFactory;
import org.settings4j.SettingsRepository;

/**
 * This class is specialized in retrieving settings by name and also
 * maintaining the settings hierarchy.
 * 
 * <p><em>The casual user does not have to deal with this class
 * directly.</em>
 * 
 * <p>The structure of the settings hierarchy is maintained by the
 * {@link #getSettings} method. The hierarchy is such that children link
 * to their parent but parents do not have any pointers to their
 * children. Moreover, settings can be instantiated in any order, in
 * particular descendant before ancestor.
 * 
 * <p>In case a descendant is created before a particular ancestor,
 * then it creates a provision node for the ancestor and adds itself
 * to the provision node. Other descendants of the same ancestor add
 * themselves to the previously created provision node.
 * 
 * 
 * @author hbrabenetz
 * @author Ceki G&uuml;lc&uuml; (the original log4j org.apache.log4j.Hierarchy)
 *
 */
public class HierarchicalSettingsRepository implements SettingsRepository {

    /** General Logger for this Class */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(HierarchicalSettingsRepository.class);
    
    private static final SettingsFactory DEFAULT_FACTORY = new DefaultSettingsFactory();
    
    private Map map = Collections.synchronizedMap(new HashMap());
    
    private HierarchicalSettings root;
    
    private int connectorCount;
    
    
    public HierarchicalSettingsRepository(HierarchicalSettings root) {
        super();
        this.root = root;
        root.setSettingsRepository(this);
    }

    public Settings exists(String name) {
        Object o = map.get(new SettingsKey(name));
        if (o instanceof HierarchicalSettings) {
            return (HierarchicalSettings) o;
        } else {
            return null;
        }
    }

    public List getCurrentSettingsList() {
        // The accumlation in list is necessary because not all elements in
        // ht are HierarchicalSettings objects as there might be some ProvisionNodes
        // as well.
        List list = new ArrayList(map.size());

        Iterator iterator = map.values().iterator();
        while (iterator.hasNext()) {
            Object o = iterator.next();
            if (o instanceof HierarchicalSettings) {
                list.add(o);
            }
        }
        return list;
    }

    public Settings getRootSettings() {
        return root;
    }

    public Settings getSettings(String name) {
        return getSettings(name, DEFAULT_FACTORY);
    }

    public Settings getSettings(String name, SettingsFactory factory) {
        // System.out.println("getInstance("+name+") called.");
        SettingsKey key = new SettingsKey(name);
        // Synchronize to prevent write conflicts. Read conflicts (in
        // getChainedLevel method) are possible only if variable
        // assignments are non-atomic.
        HierarchicalSettings settings;

        synchronized (map) {
            Object o = map.get(key);
            if (o == null) {
                settings = (HierarchicalSettings)factory.makeNewSettingsInstance(name);
                settings.setSettingsRepository(this);
                map.put(key, settings);
                updateParents(settings);
                return settings;
            } else if (o instanceof HierarchicalSettings) {
                return (HierarchicalSettings) o;
            } else if (o instanceof ProvisionNode) {
                // System.out.println("("+name+") ht.get(this) returned ProvisionNode");
                settings = (HierarchicalSettings)factory.makeNewSettingsInstance(name);
                settings.setSettingsRepository(this);
                map.put(key, settings);
                updateChildren((ProvisionNode) o, settings);
                updateParents(settings);
                return settings;
            } else {
                // It should be impossible to arrive here
                return null; // but let's keep the compiler happy.
            }
        }
    }

    /**
     * This method loops through all the *potential* parents of 'settings'. There 3 possible cases:
     * 
     * 1) No entry for the potential parent of 'settings' exists
     * 
     * We create a ProvisionNode for this potential parent and insert 'settings' in that provision node.
     * 
     * 2) There entry is of type HierarchicalSettings for the potential parent.
     * 
     * The entry is settings nearest existing parent. We update settings parent field with this entry. We
     * also break from the loop because updating our parent's parent is our parent's responsibility.
     * 
     * 3) There entry is of type ProvisionNode for this potential parent.
     * 
     * We add 'settings' to the list of children for this potential parent.
     */
    final private void updateParents(HierarchicalSettings settings) {
        String name = settings.getName();
        int length = name.length();
        boolean parentFound = false;

        // System.out.println("UpdateParents called for " + name);

        // if name = "w.x.y.z", loop thourgh "w.x.y", "w.x" and "w", but not "w.x.y.z"
        for (int i = name.lastIndexOf('.', length - 1); i >= 0; i = name.lastIndexOf('.', i - 1)) {
            String substr = name.substring(0, i);

            LOG.debug("Updating parent : " + substr);
            SettingsKey key = new SettingsKey(substr); // simple constructor
            Object o = map.get(key);
            // Create a provision node for a future parent.
            if (o == null) {
                // System.out.println("No parent "+substr+" found. Creating ProvisionNode.");
                ProvisionNode pn = new ProvisionNode(settings);
                map.put(key, pn);
            } else if (o instanceof HierarchicalSettings) {
                parentFound = true;
                settings.setParent((HierarchicalSettings) o);
                break; // no need to update the ancestors of the closest ancestor
            } else if (o instanceof ProvisionNode) {
                ((ProvisionNode) o).addElement(settings);
            } else {
                Exception e = new IllegalStateException("unexpected object type " + o.getClass() + " in ht.");
                e.printStackTrace();
            }
        }
        // If we could not find any existing parents, then link with root.
        if (!parentFound)
            settings.setParent(root);
    }

    /**
     * We update the links for all the children that placed themselves in the provision node 'pn'.
     * The second argument 'settings' is a reference for the newly created HierarchicalSettings, parent of all the
     * children in 'pn'
     * 
     * We loop on all the children 's' in 'pn':
     * 
     * If the child 's' has been already linked to a child of 'setting' then there is no need to update
     * 's'.
     * 
     * Otherwise, we set settings parent field to s's parent and set s's parent field to settings.
     * 
     */
    final private void updateChildren(ProvisionNode pn, HierarchicalSettings settings) {
        // System.out.println("updateChildren called for " + settings.name);
        final int last = pn.size();

        for (int i = 0; i < last; i++) {
            HierarchicalSettings s = (HierarchicalSettings) pn.elementAt(i);
            // System.out.println("Updating child " +s.name);

            // Unless this child already points to a correct (lower) parent,
            // make settings.parent point to s.parent and s.parent to settings.
            if (!s.getParent().getName().startsWith(settings.getName())) {
                settings.setParent(s.getParent());
                s.setParent(settings);
            }
        }
    }
    

    public int getConnectorCount() {
        return connectorCount;
    }

    public void setConnectorCount(int connectorCount) {
        this.connectorCount = connectorCount;
    }
}
