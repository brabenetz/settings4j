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

import java.util.Properties;


/**
 * Summy SessionFactory to demonstrate an example who a {@link Properties} Object
 * can be read by Settings4j an set by the SpringFramework. 
 * 
 * @author <a href="mailto:harald.brabenetz@infonova.com">Harald Brabenetz (hbrabenetz)</a>
 *
 */
public class DummySessionFactory {
    private Properties hibernateProperties;

    public Properties getHibernateProperties() {
        return hibernateProperties;
    }

    public void setHibernateProperties(Properties hibernateProperties) {
        this.hibernateProperties = hibernateProperties;
    }
    
}
