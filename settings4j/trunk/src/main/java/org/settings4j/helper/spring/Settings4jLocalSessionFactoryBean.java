/* ***************************************************************************
 * Copyright (c) 2010 BearingPoint INFONOVA GmbH, Austria.
 *
 * This software is the confidential and proprietary information of
 * BearingPoint INFONOVA GmbH, Austria. You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with INFONOVA.
 *
 * BEARINGPOINT INFONOVA MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
 * A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BEARINGPOINT INFONOVA SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *****************************************************************************/

package org.settings4j.helper.spring;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.settings4j.Settings4j;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;


/**
 * A {@link LocalSessionFactoryBean} where you configure a Settings4j-KEY for an additional hibernate config location.
 * <p>
 * TODO brabenetz Apr 11, 2011 : create Description, Examples and UnitTests.
 * 
 * @author <a href="mailto:harald.brabenetz@infonova.com">Harald Brabenetz (hbrabenetz)</a>
 *
 */
public class Settings4jLocalSessionFactoryBean extends LocalSessionFactoryBean implements ResourceLoaderAware {
    
    /** General Logger for this Class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(Settings4jLocalSessionFactoryBean.class);

    private String hibernatePropertiesKey;

    private ResourceLoader resourceLoader;
    
    public String getHibernatePropertiesKey() {
        return hibernatePropertiesKey;
    }

    /**
     * Set additional Hibernate Properties.
     * 
     * @param hibernatePropertiesKey - the settings4j-Key for the location where the
     *      hibernate properties are placed.
     */
    public void setHibernatePropertiesKey(String hibernatePropertiesKey) {
        this.hibernatePropertiesKey = hibernatePropertiesKey;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    protected SessionFactory buildSessionFactory() throws Exception {
        // overwrite with custom Hibernate Configurations:
        setCustomHibernateProperties();
        return super.buildSessionFactory();
    }

    private void setCustomHibernateProperties() {
        
        // overwrite with custom Hibernate Configurations:
        Properties hibernateProperties = getHibernateProperties();
        
        Properties additionalProperties = getAdditionalProperties();
        
        hibernateProperties.putAll(additionalProperties);
        setHibernateProperties(hibernateProperties);
    }

    private Properties getAdditionalProperties() {
        Properties additionalProperties = new Properties();

        String hibernatePropertiesLocation = Settings4j.getString(hibernatePropertiesKey);
        if (StringUtils.isEmpty(hibernatePropertiesLocation)) {
            LOG.info("No additional config location for Key: " + hibernatePropertiesKey);
            return additionalProperties;
        }
        
        Resource additionalPropertiesRes = resourceLoader.getResource(hibernatePropertiesLocation);
        if (additionalPropertiesRes != null && additionalPropertiesRes.exists()) {
            InputStream is = null;
            try {
                is = additionalPropertiesRes.getInputStream();
                additionalProperties.load(is);
            } catch (IOException e) {
                LOG.warn("Error getting additional Config from Location '" //
                    + hibernatePropertiesLocation + "'! " //
                    + "Additional Config will be ignored. " + e.getMessage(), e);
                return additionalProperties;
            } finally {
                IOUtils.closeQuietly(is);
            }
        } else {
            LOG.info("The configured additional Hibernate Properties cannot be found: " + hibernatePropertiesLocation);
        }


        return additionalProperties;
    }
}
