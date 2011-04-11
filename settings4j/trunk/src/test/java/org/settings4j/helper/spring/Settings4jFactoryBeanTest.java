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

import org.settings4j.Settings4jRepository;
import org.settings4j.UtilTesting;
import org.settings4j.contentresolver.ClasspathContentResolver;
import org.settings4j.objectresolver.SpringConfigObjectResolver;

import junit.framework.TestCase;


public class Settings4jFactoryBeanTest extends TestCase {

    public void testHappyPath() {
        // Example system-Config
        System.setProperty("Spring.HappyPathTest", "Hallo World");
        
        // load the example Spring-Config
        String key = "org/settings4j/helper/spring/Settings4jFactoryBeanHappyPath";
        SpringConfigObjectResolver springConfigObjectResolver = new SpringConfigObjectResolver();
        Object result = springConfigObjectResolver.getObject(key, new ClasspathContentResolver());
        
        assertEquals("Hallo World", result);
    }
}
