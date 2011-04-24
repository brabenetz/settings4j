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
 *
 */
public class SettingsDAOHibernate implements SettingsDAO {

    /** General Logger for this Class */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(SettingsDAOHibernate.class);

    private static final String QUERY_GET_BY_KEY = "from SettingsDTO where key = :key";

    private SessionFactory sessionFactory;

    /**
     * The SessionFactory Must be Set before the first usage
     * 
     * @param sessionFactory the required SessionFactory
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /** {@inheritDoc} */
    public SettingsDTO getById(Long id) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            return getById(id, session);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    /** {@inheritDoc} */
    public SettingsDTO getByKey(String key) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            SettingsDTO settingsDTO;
            Query query = session.createQuery(QUERY_GET_BY_KEY);
            query.setString("key", key);
            List settingsDTOs = query.list();
            if (settingsDTOs.size() == 0) {
                return null;
            } else {
                if (settingsDTOs.size() > 1) {
                    LOG.warn("More than one SettingsDTO (size:" + settingsDTOs.size() + ") found for query: '"
                        + QUERY_GET_BY_KEY + "'");
                }
                settingsDTO = (SettingsDTO) settingsDTOs.get(0);
            }

            return settingsDTO;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    /** {@inheritDoc} */
    public void store(SettingsDTO settingsDTO) {
        Session session = null;
        try {
            session = sessionFactory.openSession();

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
    public void remove(Long id) {
        Session session = null;
        try {
            session = sessionFactory.openSession();

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
    private SettingsDTO getById(Long id, Session session) {
        SettingsDTO entity = (SettingsDTO) session.get(SettingsDTO.class, id);

        if (entity == null) {
            String message = "'" + SettingsDTO.class + "' object with id '" + id + "' not found...";
            LOG.warn(message);
            // TODO hbrabenetz 24.05.2008 : Create Exceptionhandling
            throw new RuntimeException(message);
            // throw new ObjectRetrievalFailureException(this.persistentClass, id);
        }

        return entity;
    }

}
