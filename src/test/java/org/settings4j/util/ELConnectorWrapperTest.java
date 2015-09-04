/*
 * #%L
 * settings4j
 * ===============================================================
 * Copyright (C) 2008 - 2015 Brabenetz Harald, Austria
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
package org.settings4j.util;

import java.util.Map;

import javax.sql.DataSource;

import org.settings4j.connector.ClasspathConnector;
import org.settings4j.objectresolver.JavaXMLBeansObjectResolver;
import org.settings4j.objectresolver.SpringConfigObjectResolver;
import org.settings4j.objectresolver.UnionObjectResolver;

import junit.framework.TestCase;

/**
 * TestCases for {@link ELConnectorWrapper}.
 * <p>
 * Checkstyle:OFF MagicNumber
 * </p>
 *
 * @author Harald.Brabenetz
 */
public class ELConnectorWrapperTest extends TestCase {

    /**
     * TestCase for {@link ELConnectorWrapper#getObject()}.
     */
    public void testGetObject() {
        Object result;
        ClasspathConnector classpathConnector = new ClasspathConnector();
        //ContentResolver contentResolver = new ClasspathContentResolver();
        UnionObjectResolver unionObjectResolver = new UnionObjectResolver();
        unionObjectResolver.addObjectResolver(new SpringConfigObjectResolver());
        unionObjectResolver.addObjectResolver(new JavaXMLBeansObjectResolver());
        classpathConnector.setObjectResolver(unionObjectResolver);

        ELConnectorWrapper connectorWrapper = new ELConnectorWrapper(classpathConnector);

        // SpringConfigObjectResolver
        result = connectorWrapper.getObject().get("org/settings4j/objectresolver/testSpring1");
        assertNotNull(result);
        assertTrue(result instanceof DataSource);

        // JavaXMLBeansObjectResolver
        result = connectorWrapper.getObject().get("org/settings4j/objectresolver/test1");
        assertNotNull(result);
        assertTrue(result instanceof Map);

        // with classpath prefix
        result = connectorWrapper.getObject().get("classpath:org/settings4j/objectresolver/testSpring1");
        assertNotNull(result);
        assertTrue(result instanceof DataSource);



    }
}
