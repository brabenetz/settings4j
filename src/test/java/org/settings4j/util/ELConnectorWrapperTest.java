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
package org.settings4j.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Map;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;
import org.settings4j.connector.ClasspathConnector;
import org.settings4j.objectresolver.JavaXMLBeansObjectResolver;
import org.settings4j.objectresolver.SpringConfigObjectResolver;
import org.settings4j.objectresolver.UnionObjectResolver;

/**
 * TestCases for {@link ELConnectorWrapper}.
 *
 * @author Harald.Brabenetz
 */
public class ELConnectorWrapperTest {

    /**
     * TestCase for {@link ELConnectorWrapper#getObject()}.
     */
    @Test
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
        assertThat(result, is(notNullValue()));
        Assert.assertTrue(result instanceof DataSource);

        // JavaXMLBeansObjectResolver
        result = connectorWrapper.getObject().get("org/settings4j/objectresolver/test1");
        assertThat(result, is(notNullValue()));
        Assert.assertTrue(result instanceof Map);

        // with classpath prefix
        result = connectorWrapper.getObject().get("classpath:org/settings4j/objectresolver/testSpring1");
        assertThat(result, is(notNullValue()));
        Assert.assertTrue(result instanceof DataSource);



    }
}
