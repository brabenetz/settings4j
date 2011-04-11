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

import org.settings4j.Settings4j;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;


/**
 * BeanFactory which gets the Object from {@link Settings4j#getObject(String)}.
 * <p>
 * TODO brabenetz Apr 11, 2011 : create Description, Examples and UnitTests.
 * 
 * 
 * @author <a href="mailto:harald.brabenetz@infonova.com">Harald Brabenetz (hbrabenetz)</a>
 *
 */
public class Settings4jFactoryBean implements FactoryBean, InitializingBean {

    private String key;
    private boolean singleton = true;
    private Object singletonObject;
    private Class expectedType;

    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }
    
    /**
     * Specify the type that the located JNDI object is supposed
     * to be assignable to, if any.
     * 
     * @param expectedType
     */
    public void setExpectedType(Class expectedType) {
        this.expectedType = expectedType;
    }

    /**
     * Return the type that the located JNDI object is supposed
     * to be assignable to, if any.
     * 
     * @return the type that the located JNDI object is supposed
     * to be assignable to.
     */
    public Class getExpectedType() {
        return this.expectedType;
    }

    /** {@inheritDoc} */
    public Object getObject() {
        if (singleton) {
            return singletonObject;
        }
        return Settings4j.getObject(key);
    }

    /** {@inheritDoc} */
    public Class getObjectType() {
        Object obj = getObject();
        if (obj != null) {
            return obj.getClass();
        }
        return getExpectedType();
    }

    /** {@inheritDoc} */
    public boolean isSingleton() {
        return singleton;
    }

    /** {@inheritDoc} */
    public void afterPropertiesSet() throws Exception {
        if (singleton) {
            singletonObject = Settings4j.getObject(key);
        }
    }

}
