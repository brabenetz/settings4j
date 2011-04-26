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
package org.settings4j.helper.spring;

import org.settings4j.Settings4j;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;


/**
 * BeanFactory which gets the Object from {@link Settings4j#getObject(String)}.
 * <p>
 * <h2>Simple SpringBean Example:</h2> The following Example can be configured with the settings4j-Key "env/MyVariable".
 * <br />
 * e.g.: System.setProperty("env/MyVariable", "Hallo World");<br />
 * OR with JNDI-Context OR in a Classpath-File under "classpath:env/MyVariable" Or with a custom
 * {@link org.settings4j.Connector}-Implementation.
 * 
 * <pre>
 *  &lt;bean id="MyConfigurableValue" class="org.settings4j.helper.spring.Settings4jFactoryBean"&gt;
 *      &lt;property name="key"&gt;&lt;value&gt;<b>env/MyVariable</b>&lt;/value&gt;&lt;/property&gt;
 *  &lt;/bean&gt;
 * </pre>
 * 
 * <h2>More complex SpringBean Example:</h2> This Example shows how a Hibernate SessionFactory can optional customized
 * with additional HibernateProperties.
 * 
 * <pre>
 *  &lt;bean id="hibernateProperties"
 *      class="org.springframework.beans.factory.config.PropertiesFactoryBean"&gt;
 *      &lt;property name="locations"&gt;
 *          &lt;list&gt;
 *              &lt;value&gt;classpath:org/settings4j/helper/spring/DefaultHibernate.properties&lt;/value&gt;
 *              &lt;bean class="org.settings4j.helper.spring.Settings4jFactoryBean"&gt;
 *                  &lt;property name="key"&gt;
 *                      &lt;value&gt;<b>env/MyCustomProprtiesLocationKey</b>&lt;/value&gt;
 *                  &lt;/property&gt;
 *                  &lt;property name="defaultObject"&gt;
 *                      &lt;!-- This could also be an empty Property-File But it must Exist. --&gt;
 *                      &lt;!-- PropertiesFactoryBean cannot handle invalid Paths. --&gt;
 *                      &lt;value&gt;classpath:org/settings4j/helper/spring/DefaultHibernate.properties&lt;/value&gt;
 *                  &lt;/property&gt;
 *              &lt;/bean&gt;
 *          &lt;/list&gt;
 *      &lt;/property&gt;
 *  &lt;/bean&gt;
 *  
 *  &lt;bean id="mySessionFactory" class="org.springframework.orm.hibernate3.LocalSessionFactoryBean"&gt;
 *      &lt;property name="dataSource" ref="db2Datasource2" /&gt;
 *      &lt;property name="hibernateProperties" ref="hibernateProperties" /&gt;
 *      ....
 *  &lt;/bean&gt;
 * </pre>
 * 
 * @author Harald.Brabenetz
 */
public class Settings4jFactoryBean implements FactoryBean, InitializingBean {

    private String key;
    private boolean singleton = true;
    private Object singletonObject;
    private Object defaultObject;
    private Class expectedType;

    public String getKey() {
        return this.key;
    }

    /**
     * @param key - the Setting4j-Key for Your Configuration-Value.
     */
    public void setKey(final String key) {
        this.key = key;
    }

    /**
     * @param singleton - If <code>true</code>, the Value will be read during SpringBean-Initialization. Default is
     *            <code>true</code>
     */
    public void setSingleton(final boolean singleton) {
        this.singleton = singleton;
    }

    /**
     * Specify the type that the configured object is supposed to be assignable to, if any.
     * 
     * @param expectedType the expected {@link Class}.
     */
    public void setExpectedType(final Class expectedType) {
        this.expectedType = expectedType;
    }

    /**
     * Return the type that the configured object is supposed to be assignable to, if any.
     * 
     * @return the type that the configured object is supposed to be assignable to.
     */
    public Class getExpectedType() {
        return this.expectedType;
    }


    public Object getDefaultObject() {
        return this.defaultObject;
    }


    public void setDefaultObject(final Object defaultObject) {
        this.defaultObject = defaultObject;
    }

    /** {@inheritDoc} */
    public Object getObject() {
        if (this.singleton) {
            return this.singletonObject;
        }
        return getSettings4jObject();
    }

    /** {@inheritDoc} */
    public Class getObjectType() {
        final Object obj = getObject();
        if (obj != null) {
            return obj.getClass();
        }
        return getExpectedType();
    }

    /** {@inheritDoc} */
    public boolean isSingleton() {
        return this.singleton;
    }

    /** {@inheritDoc} */
    public void afterPropertiesSet() throws Exception {
        if (this.singleton) {
            this.singletonObject = getSettings4jObject();
        }
    }

    private Object getSettings4jObject() {
        Object result = Settings4j.getObject(this.key);
        if (result == null) {
            result = Settings4j.getString(this.key);
        }

        if (result == null) {
            result = this.defaultObject;
        }
        return result;
    }

}
