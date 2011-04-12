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

import junit.framework.TestCase;

import org.settings4j.contentresolver.ClasspathContentResolver;
import org.settings4j.objectresolver.SpringConfigObjectResolver;


public class Settings4jFactoryBeanTest extends TestCase {

    public void testHappyPath() {
        // Example system-Config
        System.setProperty("Spring.HappyPathTest", "Hallo World");
        
        // load the example Spring-Config
        Object result = getObjectFromSpringConfig("org/settings4j/helper/spring/Settings4jFactoryBeanHappyPath");
        
        // validate Result
        assertEquals("Hallo World", result);
    }
    
    public void testHappyPathComplex() {
        // Example system-Config
        //System.setProperty("Spring.HappyPathComplexTest", "Hallo World");
        Object result;
        DummySessionFactory sessionFactory;
        
        // load the example Spring-Config
        result = getObjectFromSpringConfig("org/settings4j/helper/spring/Settings4jFactoryBeanHappyPathComplex");

        // validate Result
        assertEquals(DummySessionFactory.class.getName(), result.getClass().getName());
        sessionFactory = (DummySessionFactory) result;
        assertEquals("test Property Value 1", sessionFactory.getHibernateProperties().get("testProperty1"));
        assertEquals("test Property Value 2", sessionFactory.getHibernateProperties().get("testProperty2"));
        assertNull(sessionFactory.getHibernateProperties().get("testProperty3"));
        
        // Now set Custom Proeprty:
        System.setProperty("Spring.HappyPathComplexTest", "classpath:org/settings4j/helper/spring/CustomHibernate.properties");

        // load the same example Spring-Config (but now a custom config is set)
        result = getObjectFromSpringConfig("org/settings4j/helper/spring/Settings4jFactoryBeanHappyPathComplex");

        // validate custom Result
        assertEquals(DummySessionFactory.class.getName(), result.getClass().getName());
        sessionFactory = (DummySessionFactory) result;
        assertEquals("test Property Value 1 Custom", sessionFactory.getHibernateProperties().get("testProperty1"));
        assertEquals("test Property Value 2", sessionFactory.getHibernateProperties().get("testProperty2"));
        assertEquals("test Property Value 3 Custom", sessionFactory.getHibernateProperties().get("testProperty3"));
        
    }

    private Object getObjectFromSpringConfig(String key) {
        SpringConfigObjectResolver springConfigObjectResolver = new SpringConfigObjectResolver();
        Object result = springConfigObjectResolver.getObject(key, new ClasspathContentResolver());
        return result;
    }
}
