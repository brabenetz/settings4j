/*
 * #%L
 * settings4j
 * ===============================================================
 * Copyright (C) 2008 - 2016 Brabenetz Harald, Austria
 * ===============================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.settings4j.helper.spring;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Test;
import org.settings4j.contentresolver.ClasspathContentResolver;
import org.settings4j.objectresolver.SpringConfigObjectResolver;


/**
 * TestSuite for {@link Settings4jFactoryBean}.
 *
 * @author brabenetz
 *
 */
public class Settings4jFactoryBeanTest {

    /**
     * Test simple UseCase and read a String Object.
     * <p>
     * See /src/test/resources/org/settings4j/helper/spring/Settings4jFactoryBeanHappyPath
     * </p>
     */
    @Test
    public void testHappyPath() {
        // Example system-Config
        System.setProperty("Spring.HappyPathTest", "Hallo World");

        // load the example Spring-Config
        final Object result = getObjectFromSpringConfig("org/settings4j/helper/spring/Settings4jFactoryBeanHappyPath");

        // validate Result
        assertThat(result, is((Object) "Hallo World"));
    }

    /**
     * Test complex UseCase with "defaultObject".
     * <p>
     * See /src/test/resources/org/settings4j/helper/spring/Settings4jFactoryBeanHappyPathComplex
     * </p>
     */
    @Test
    public void testHappyPathComplex() {
        // Example system-Config
        // System.setProperty("Spring.HappyPathComplexTest", "Hallo World");
        Object result;
        DummySessionFactory sessionFactory;

        // load the example Spring-Config
        result = getObjectFromSpringConfig("org/settings4j/helper/spring/Settings4jFactoryBeanHappyPathComplex");

        // validate Result
        assertThat(result.getClass().getName(), is(DummySessionFactory.class.getName()));
        sessionFactory = (DummySessionFactory) result;
        assertThat(sessionFactory.getHibernateProperties().get("testProperty1"), is((Object) "test Property Value 1"));
        assertThat(sessionFactory.getHibernateProperties().get("testProperty2"), is((Object) "test Property Value 2"));
        assertThat(sessionFactory.getHibernateProperties().get("testProperty3"), is(nullValue()));

        // Now set Custom Proeprty:
        System.setProperty("Spring.HappyPathComplexTest",
            "classpath:org/settings4j/helper/spring/CustomHibernate.properties");

        // load the same example Spring-Config (but now a custom config is set)
        result = getObjectFromSpringConfig("org/settings4j/helper/spring/Settings4jFactoryBeanHappyPathComplex");

        // validate custom Result
        assertThat(result.getClass().getName(), is(DummySessionFactory.class.getName()));
        sessionFactory = (DummySessionFactory) result;
        assertThat(sessionFactory.getHibernateProperties().get("testProperty1"), is((Object) "test Property Value 1 Custom"));
        assertThat(sessionFactory.getHibernateProperties().get("testProperty2"), is((Object) "test Property Value 2"));
        assertThat(sessionFactory.getHibernateProperties().get("testProperty3"), is((Object) "test Property Value 3 Custom"));

    }

    private Object getObjectFromSpringConfig(final String key) {
        final SpringConfigObjectResolver springConfigObjectResolver = new SpringConfigObjectResolver();
        final Object result = springConfigObjectResolver.getObject(key, new ClasspathContentResolver());
        return result;
    }
}
