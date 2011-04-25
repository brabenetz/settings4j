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

import java.io.ByteArrayInputStream;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.settings4j.ContentResolver;
import org.settings4j.connector.db.dao.SettingsDAO;
import org.settings4j.connector.db.dao.hibernate.ConfigurationByteArray;
import org.settings4j.connector.db.dao.hibernate.SettingsDAOHibernate;
import org.settings4j.contentresolver.ClasspathContentResolver;

/**
 * This Hibernate Connector uses the {@link SettingsDAOHibernate}.<br />
 * <br />
 * Before the Data Access Object will be used, the SessionFactory will be injected into the DAO. <br />
 * 
 * @author Harald.Brabenetz
 */
public class HibernateDBConnector extends AbstractDBConnector {

    /** General Logger for this Class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(HibernateDBConnector.class);

    private static final ContentResolver DEFAULT_CONTENT_RESOLVER = new ClasspathContentResolver();

    private final SettingsDAOHibernate settingsDAO = new SettingsDAOHibernate();

    private String hibernateConfigXmlKeys = "hibernate.cfg.xml";
    private String hibernateMappingXmlKeys = "org/settings4j/connector/db/SettingsDTO.hbm.xml";


    /**
     * You can set a customized hibernateConfigXml Key in your settings4j.xml<br />
     * Default: "hibernate.cfg.xml"<br />
     * You can set more than one File with the ',' Seperator<br />
     * e.g.: "hibernate1.cfg.xml,hibernate2.cfg.xml"<br />
     * <br />
     * The files will be readed by the given ContentResolvers. (default is ClasspathConntentResolver)<br />
     * <br />
     * Example:<br />
     * 
     * <pre>
     * &lt;connector name="HibernateDBConnector" class="org.settings4j.connector.db.HibernateDBConnector"&gt;
     *     &lt;param name="<b>hibernateConfigXmlKeys</b>" value="com/mycompany/myapp/hibernate.cfg.xml" /&gt;
     *     &lt;contentResolver-ref ref="DefaultContentResolver" /&gt;
     *     &lt;objectResolver-ref ref="DefaultObjectResolver" /&gt;
     * &lt;/connector&gt;
     * </pre>
     * 
     * @param hibernateConfigXmlKeys the comma seperated list of Hibernate-Configs.
     */
    public void setHibernateConfigXmlKeys(final String hibernateConfigXmlKeys) {
        this.hibernateConfigXmlKeys = hibernateConfigXmlKeys;
    }

    /**
     * You can set a customized HibernateMappingXml in your settings4j.xml<br />
     * Default: "org/settings4j/connector/db/SettingsDTO.hbm.xml"<br />
     * <br />
     * The files will be readed by the given ContentResolvers (default is ClasspathConntentResolver).<br />
     * <br />
     * Example:<br />
     * 
     * <pre>
     * &lt;connector name="HibernateDBConnector" class="org.settings4j.connector.db.HibernateDBConnector"&gt;
     *     &lt;param name="<b>hibernateMappingXmlKeys</b>" value="C:/CustomizedSettingsDTO.hbm.xml" /&gt;
     *     &lt;contentResolver-ref ref="DefaultContentResolver" /&gt;
     *     &lt;objectResolver-ref ref="DefaultObjectResolver" /&gt;
     * &lt;/connector&gt;
     * </pre>
     * 
     * @param hibernateMappingXmlKeys the comma seperated list of Hibernate-Mappings.
     */
    public void setHibernateMappingXmlKeys(final String hibernateMappingXmlKeys) {
        this.hibernateMappingXmlKeys = hibernateMappingXmlKeys;
    }

    /** {@inheritDoc} */
    protected SettingsDAO getSettingsDAO() {
        return this.settingsDAO;
    }

    /**
     * This implementation creates a Hibernate SessionFactory an inject it into the settingsDAO. After this, the
     * {@link AbstractDBConnector#init()} will be called to add this Object to the contentResolver list
     **/
    public void init() {
        final SessionFactory sessionFactory = getConfiguration().buildSessionFactory();
        this.settingsDAO.setSessionFactory(sessionFactory);
        super.init();
    }

    /**
     * Reading and parsing of the Hibernate Configurations und Mappings.
     * 
     * @return the Hibernate Configuration
     */
    private Configuration getConfiguration() {
        final ConfigurationByteArray configuration = new ConfigurationByteArray();

        // parsing the Configuration-Files
        StringTokenizer stringTokenizer = new StringTokenizer(this.hibernateConfigXmlKeys, ",");
        while (stringTokenizer.hasMoreTokens()) {
            final String hibernateConfigXmlKey = stringTokenizer.nextToken();
            if (StringUtils.isNotEmpty(hibernateConfigXmlKey)) {
                addConfigurationFile(configuration, hibernateConfigXmlKey);
            }
        }

        // parsing the Mapping-Files.
        stringTokenizer = new StringTokenizer(this.hibernateMappingXmlKeys, ",");
        while (stringTokenizer.hasMoreTokens()) {
            final String hibernateMappingXmlKey = stringTokenizer.nextToken();
            if (StringUtils.isNotEmpty(hibernateMappingXmlKey)) {
                addMappingFile(configuration, hibernateMappingXmlKey);
            }
        }
        configuration.buildMappings();

        return configuration;
    }

    /**
     * Add a Configuration-File to the Configuration.
     * 
     * @param configuration
     * @param settings4jKey
     */
    private void addConfigurationFile(final ConfigurationByteArray configuration, final String settings4jKey) {
        byte[] hibernateConfigXml = getContentResolver().getContent(settings4jKey);
        if (hibernateConfigXml == null) {
            hibernateConfigXml = DEFAULT_CONTENT_RESOLVER.getContent(settings4jKey);
        }

        if (hibernateConfigXml == null) {
            LOG.warn("Cannot find Hibernate-Configuration from Key: " + settings4jKey);
        } else {
            configuration.configure(hibernateConfigXml, settings4jKey);
        }
    }

    /**
     * Add a Mapping-File to the Configuration.
     * 
     * @param configuration
     * @param settings4jKey
     */
    private void addMappingFile(final ConfigurationByteArray configuration, final String settings4jKey) {
        byte[] hibernateMappingXml = getContentResolver().getContent(settings4jKey);
        if (hibernateMappingXml == null) {
            hibernateMappingXml = DEFAULT_CONTENT_RESOLVER.getContent(settings4jKey);
        }

        if (hibernateMappingXml == null) {
            LOG.warn("Cannot find Hibernate-Mapping from Key: " + settings4jKey);
        } else {
            configuration.addInputStream(new ByteArrayInputStream(hibernateMappingXml));
        }
    }
}
