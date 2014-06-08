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

package org.settings4j.connector.db.dao.hibernate;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.settings4j.connector.db.SettingsDTO;
import org.settings4j.connector.db.dao.SettingsDAO;

/**
 * The Hibernate Implementation of the {@link SettingsDAO} Interface<br />
 * <br />
 * pre required: The sessionFactory must be injected by there Setter before the first usage.
 * 
 * @author Harald.Brabenetz
 */
public class SettingsDAOHibernate implements SettingsDAO {

    /** General Logger for this Class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(SettingsDAOHibernate.class);

    private static final String QUERY_GET_BY_KEY = "from SettingsDTO where key = :key";

    private SessionFactory sessionFactory;

    /**
     * The SessionFactory Must be Set before the first usage.
     * 
     * @param sessionFactory the required SessionFactory
     */
    public void setSessionFactory(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /** {@inheritDoc} */
    public SettingsDTO getById(final Long id) {
        Session session = null;
        try {
            session = this.sessionFactory.openSession();
            return getById(id, session);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    /** {@inheritDoc} */
    public SettingsDTO getByKey(final String key) {
        Session session = null;
        try {
            session = this.sessionFactory.openSession();
            final Query query = session.createQuery(QUERY_GET_BY_KEY);
            query.setString("key", key);
            final List settingsDTOs = query.list();
            if (settingsDTOs.size() == 0) {
                return null;
            }
            // else
            if (settingsDTOs.size() > 1) {
                LOG.warn("More than one SettingsDTO (size:" + settingsDTOs.size() + ") found for query: '"
                    + QUERY_GET_BY_KEY + "'");
            }

            return (SettingsDTO) settingsDTOs.get(0);
        } catch (RuntimeException e) {
            LOG.warn("Cannot get Value for Key '" + key + "' from Database! " + e.getMessage());
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    /** {@inheritDoc} */
    public void store(final SettingsDTO settingsDTO) {
        Session session = null;
        try {
            session = this.sessionFactory.openSession();

            session.beginTransaction();

            session.saveOrUpdate(settingsDTO);

            session.getTransaction().commit();

        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    /** {@inheritDoc} */
    public void remove(final Long id) {
        Session session = null;
        try {
            session = this.sessionFactory.openSession();

            session.beginTransaction();

            session.delete(getById(id, session));

            session.getTransaction().commit();

        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    /**
     * @see #getById(Long)
     */
    private SettingsDTO getById(final Long id, final Session session) {
        final SettingsDTO entity = (SettingsDTO) session.get(SettingsDTO.class, id);

        if (entity == null) {
            final String message = "'" + SettingsDTO.class + "' object with id '" + id + "' not found...";
            LOG.warn(message);
            throw new IllegalArgumentException(message);
        }

        return entity;
    }

}