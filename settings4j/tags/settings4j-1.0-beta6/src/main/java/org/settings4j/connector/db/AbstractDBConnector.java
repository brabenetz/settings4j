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
 * The basic Database implementation of a Connector-Interface.
 * <p>
 * A concrete Subclass must implement the Method getSettingsDAO() <br />
 * The implementation of the SettingsDAO could be Hibernate, JPA, Ibatis, JDBC or something else.
 * 
 * @author Harald.Brabenetz
 */
public abstract class AbstractDBConnector extends AbstractConnector {


    /** General Logger for this Class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(AbstractDBConnector.class);

    /**
     * This implementation adds a {@link DBContentResolverAdapter} to the contentResolver-List (at the first position).
     * <p>
     * This ContentResolver will be used by the ObjectResolver
     */
    public void init() {
        super.init();
        final ContentResolver currentContentResolver = getContentResolver();

        final ContentResolver unionContentResolver = new UnionContentResolver();
        unionContentResolver.addContentResolver(new DBContentResolverAdapter(this));
        unionContentResolver.addContentResolver(currentContentResolver);

        setContentResolver(unionContentResolver);
    }

    /** {@inheritDoc} */
    public byte[] getContent(final String key) {
        final SettingsDTO settingsDTO = getSettingsDAO().getByKey(key);
        if (settingsDTO != null) {
            return settingsDTO.getContentValue();
        }
        // else
        return null;
    }

    /** {@inheritDoc} */
    public Object getObject(final String key) {
        final Object object = getObjectResolver().getObject(key, getContentResolver());
        return object;
    }

    /** {@inheritDoc} */
    public String getString(final String key) {
        final SettingsDTO settingsDTO = getSettingsDAO().getByKey(key);
        if (settingsDTO != null) {
            return settingsDTO.getStringValue();
        }
        // else
        return null;
    }


    /**
     * @param key The Settings4j Key.
     * @param value The value to Store.
     */
    public void setContent(final String key, final byte[] value) {
        try {
            SettingsDTO settingsDTO = getSettingsDAO().getByKey(key);
            if (settingsDTO == null) {
                settingsDTO = new SettingsDTO();
                settingsDTO.setKey(key);
            }
            settingsDTO.setContentValue(value);
            getSettingsDAO().store(settingsDTO);
        } catch (final RuntimeException e) {
            LOG.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * @param key The Settings4j Key.
     * @param value The value to Store.
     */
    public void setString(final String key, final String value) {
        try {
            SettingsDTO settingsDTO = getSettingsDAO().getByKey(key);
            if (settingsDTO == null) {
                settingsDTO = new SettingsDTO();
                settingsDTO.setKey(key);
            }
            settingsDTO.setStringValue(value);
            getSettingsDAO().store(settingsDTO);
        } catch (final RuntimeException e) {
            LOG.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * @param key The Settings4j Key.
     */
    public void deleteValue(final String key) {
        try {
            SettingsDTO settingsDTO = getSettingsDAO().getByKey(key);
            if (settingsDTO != null) {
                getSettingsDAO().remove(settingsDTO.getId());
            }
        } catch (final RuntimeException e) {
            LOG.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * A concrete Subclass must implement this Methode<br />
     * The implementation of the SettingsDAO could be Hibernate, JPA, Ibatis, JDBC or something else.
     * 
     * @return a SettingsDAO Object
     */
    protected abstract SettingsDAO getSettingsDAO();
}
