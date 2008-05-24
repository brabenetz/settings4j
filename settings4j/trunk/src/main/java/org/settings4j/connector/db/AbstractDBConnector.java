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

package org.settings4j.connector.db;

import org.settings4j.ContentResolver;
import org.settings4j.connector.AbstractConnector;
import org.settings4j.connector.db.dao.SettingsDAO;
import org.settings4j.contentresolver.UnionContentResolver;

/**
 * The basic Database implementation of a Connector-Interface. <br />
 * A concrete Subclass must implement the Methode {@link #getSettingsDAO()} <br />
 * 
 * The implementation of the SettingsDAO could be Hibernate, JPA, Ibatis, JDBC or something else. 
 * 
 * @author hbrabenetz
 *
 */
public abstract class AbstractDBConnector extends AbstractConnector {
    
    /** General Logger for this Class */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(AbstractDBConnector.class);

    public AbstractDBConnector() {
        super();
    }
    
    /**
     * This implementation adds a {@link DBContentResolverAdapter} to the contentResolver-List (at the first position).
     * This ContentResolver will be used by the ObjectResolver
     * */
    public void init() {
        super.init();
        ContentResolver currentContentResolver = getContentResolver();

        ContentResolver unionContentResolver = new UnionContentResolver();
        unionContentResolver.addContentResolver(new DBContentResolverAdapter(this));
        unionContentResolver.addContentResolver(currentContentResolver);
        
        setContentResolver(unionContentResolver);
    }

    /** {@inheritDoc} */
    public byte[] getContent(String key) {
        SettingsDTO settingsDTO = getSettingsDAO().getByKey(key);
        if (settingsDTO != null){
            return settingsDTO.getContentValue();
        } else {
            return null;
        }
    }

    /** {@inheritDoc} */
    public Object getObject(String key) {
        Object object = getObjectResolver().getObject(key, getContentResolver());
        return object;
    }

    /** {@inheritDoc} */
    public String getString(String key) {
        SettingsDTO settingsDTO = getSettingsDAO().getByKey(key);
        if (settingsDTO != null){
            return settingsDTO.getStringValue();
        } else {
            return null;
        }
    }

    /** {@inheritDoc} */
    public int setContent(String key, byte[] value) {
        try {
            SettingsDTO settingsDTO = getSettingsDAO().getByKey(key);
            if (settingsDTO == null) {
                settingsDTO = new SettingsDTO();
                settingsDTO.setKey(key);
            }
            settingsDTO.setContentValue(value);
            getSettingsDAO().store(settingsDTO);
            return SETTING_SUCCESS;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return SETTING_NOT_POSSIBLE;
        }
    }

    /** {@inheritDoc} */
    public int setObject(String key, Object value) {
        return getObjectResolver().setObject(key, getContentResolver(), value);
    }

    /** {@inheritDoc} */
    public int setString(String key, String value) {
        try {
            SettingsDTO settingsDTO = getSettingsDAO().getByKey(key);
            if (settingsDTO == null) {
                settingsDTO = new SettingsDTO();
                settingsDTO.setKey(key);
            }
            settingsDTO.setStringValue(value);
            getSettingsDAO().store(settingsDTO);
            return SETTING_SUCCESS;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return SETTING_NOT_POSSIBLE;
        }
    }
    
    /**
     * A concrete Subclass must implement this Methode<br />
     * 
     * The implementation of the SettingsDAO could be Hibernate, JPA, Ibatis, JDBC or something else. 
     * 
     * @return a SettingsDAO Object
     */
    protected abstract SettingsDAO getSettingsDAO();
}
