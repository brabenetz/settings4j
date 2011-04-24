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
 * <h2>Simple SpringBean Example:</h2>
 * The following Example can be configured with the settings4j-Key "env/MyVariable".<br />
 * e.g.: System.setProperty("env/MyVariable", "Hallo World");<br />
 * OR with JNDI-Context OR in a Classpath-File under "classpath:env/MyVariable"
 * Or with a custom {@link org.settings4j.Connector}-Implementation.
 * <pre>
 *  &lt;bean id="MyConfigurableValue" class="org.settings4j.helper.spring.Settings4jFactoryBean"&gt;
 *      &lt;property name="key"&gt;&lt;value&gt;<b>env/MyVariable</b>&lt;/value&gt;&lt;/property&gt;
 *  &lt;/bean&gt;
 * </pre>
 * 
 * 
 * <h2>More complex SpringBean Example:</h2>
 * 
 * This Example shows how a Hibernate SessionFactory can optional customized with additional HibernateProperties. 
 * 
 * <pre>
 *    &lt;bean id="hibernateProperties"
 *        class="org.springframework.beans.factory.config.PropertiesFactoryBean"&gt;
 *        &lt;property name="locations"&gt;
 *            &lt;list&gt;
 *                &lt;value&gt;classpath:org/settings4j/helper/spring/DefaultHibernate.properties&lt;/value&gt;
 *                &lt;bean class="org.settings4j.helper.spring.Settings4jFactoryBean"&gt;
 *                    &lt;property name="key"&gt;&lt;value&gt;<b>env/MyCustomProprtiesLocationKey</b>&lt;/value&gt;&lt;/property&gt;
 *                    &lt;property name="defaultObject"&gt;
 *                        &lt;!-- This could also be an empty Property-File But it must Exist. --&gt;
 *                        &lt;!-- PropertiesFactoryBean cannot handle invalid Paths. --&gt;
 *                        &lt;value&gt;classpath:org/settings4j/helper/spring/DefaultHibernate.properties&lt;/value&gt;
 *                    &lt;/property&gt;
 *                &lt;/bean&gt;
 *            &lt;/list&gt;
 *        &lt;/property&gt;
 *    &lt;/bean&gt;
 *    
 *    &lt;bean id="mySessionFactory" class="org.springframework.orm.hibernate3.LocalSessionFactoryBean"&gt;
 *        &lt;property name="dataSource" ref="db2Datasource2" /&gt;
 *        &lt;property name="hibernateProperties" ref="hibernateProperties" /&gt;
 *        ....
 *    &lt;/bean&gt;
 * </pre>
 * 
 * 
 * @author Harald.Brabenetz
 *
 */
public class Settings4jFactoryBean implements FactoryBean, InitializingBean {

    private String key;
    private boolean singleton = true;
    private Object singletonObject;
    private Object defaultObject;
    private Class expectedType;

    public String getKey() {
        return key;
    }
    
    /**
     * @param key - the Setting4j-Key for Your Configuration-Value.
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @param singleton - If <code>true</code>, the Value will be read during SpringBean-Initialization. Default is <code>true</code>
     */
    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }
    
    /**
     * Specify the type that the configured object is supposed
     * to be assignable to, if any.
     * 
     * @param expectedType
     */
    public void setExpectedType(Class expectedType) {
        this.expectedType = expectedType;
    }

    /**
     * Return the type that the configured object is supposed
     * to be assignable to, if any.
     * 
     * @return the type that the configured object is supposed
     * to be assignable to.
     */
    public Class getExpectedType() {
        return this.expectedType;
    }

    
    public Object getDefaultObject() {
        return defaultObject;
    }

    
    public void setDefaultObject(Object defaultObject) {
        this.defaultObject = defaultObject;
    }

    /** {@inheritDoc} */
    public Object getObject() {
        if (singleton) {
            return singletonObject;
        }
        return getSettings4jObject();
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
            singletonObject = getSettings4jObject();
        }
    }

    private Object getSettings4jObject() {
        Object result = Settings4j.getObject(key);
        if (result == null) {
            result = Settings4j.getString(key);
        }

        if (result == null) {
            result = defaultObject;
        }
        return result;
    }

}
