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
package org.settings4j.connector;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.settings4j.Connector;
import org.settings4j.contentresolver.ClasspathContentResolver;
import org.settings4j.objectresolver.JavaXMLBeansObjectResolver;

@RunWith(MockitoJUnitRunner.class)
public class CachedConnectorWrapperTest {

    private static final String TEST_VALUE_STRING = "Test-Value";

    private static final byte[] TEST_VALUE_CONTENT = "Test-Value".getBytes();

    private static final Object TEST_VALUE_OBJECT = "Test-Value";

    private static final String TEST_KEY = "TEST";

    @InjectMocks
    private CachedConnectorWrapper cachedConnectorWrapper;

    @Mock
    private Connector targetConnector;

    @Test
    public void testGetString() {
        // prepare
        Mockito.when(this.targetConnector.getString(TEST_KEY)).thenReturn(TEST_VALUE_STRING);

        // test
        assertThat(this.cachedConnectorWrapper.getString(TEST_KEY), is(TEST_VALUE_STRING));
        assertThat(this.cachedConnectorWrapper.getString(TEST_KEY), is(TEST_VALUE_STRING));

        // validate
        Mockito.verify(this.targetConnector).getString(TEST_KEY);
    }

    @Test
    public void testGetStringWithNull() {
        // prepare
        Mockito.when(this.targetConnector.getString(TEST_KEY)).thenReturn(null);

        // test
        assertThat(this.cachedConnectorWrapper.getString(TEST_KEY), is(nullValue()));
        assertThat(this.cachedConnectorWrapper.getString(TEST_KEY), is(nullValue()));

        // validate
        Mockito.verify(this.targetConnector, Mockito.times(1)).getString(TEST_KEY);
    }

    @Test
    public void testGetContent() {
        // prepare
        Mockito.when(this.targetConnector.getContent(TEST_KEY)).thenReturn(TEST_VALUE_CONTENT);

        // test
        assertThat(this.cachedConnectorWrapper.getContent(TEST_KEY), is(TEST_VALUE_CONTENT));
        assertThat(this.cachedConnectorWrapper.getContent(TEST_KEY), is(TEST_VALUE_CONTENT));

        // validate
        Mockito.verify(this.targetConnector).getContent(TEST_KEY);
    }

    @Test
    public void testGetContentWithNull() {
        // prepare
        Mockito.when(this.targetConnector.getContent(TEST_KEY)).thenReturn(null);

        // test
        assertThat(this.cachedConnectorWrapper.getContent(TEST_KEY), is(nullValue()));
        assertThat(this.cachedConnectorWrapper.getContent(TEST_KEY), is(nullValue()));

        // validate
        Mockito.verify(this.targetConnector, Mockito.times(1)).getContent(TEST_KEY);
    }

    @Test
    public void testGetObject() {
        // prepare
        Mockito.when(this.targetConnector.getObject(TEST_KEY)).thenReturn(TEST_VALUE_OBJECT);

        // test
        assertThat(this.cachedConnectorWrapper.getObject(TEST_KEY), is(TEST_VALUE_OBJECT));
        assertThat(this.cachedConnectorWrapper.getObject(TEST_KEY), is(TEST_VALUE_OBJECT));

        // validate
        Mockito.verify(this.targetConnector).getObject(TEST_KEY);
    }

    @Test
    public void testGetObjectWithNull() {
        // prepare
        Mockito.when(this.targetConnector.getObject(TEST_KEY)).thenReturn(null);

        // test
        assertThat(this.cachedConnectorWrapper.getObject(TEST_KEY), is(nullValue()));
        assertThat(this.cachedConnectorWrapper.getObject(TEST_KEY), is(nullValue()));

        // validate
        Mockito.verify(this.targetConnector, Mockito.times(1)).getObject(TEST_KEY);
    }

    @Test
    public void testClearCachedValue() {
        // prepare
        Mockito.when(this.targetConnector.getString(TEST_KEY)).thenReturn(TEST_VALUE_STRING);
        Mockito.when(this.targetConnector.getContent(TEST_KEY)).thenReturn(TEST_VALUE_CONTENT);
        Mockito.when(this.targetConnector.getObject(TEST_KEY)).thenReturn(TEST_VALUE_OBJECT);

        // test
        assertThat(this.cachedConnectorWrapper.getString(TEST_KEY), is(TEST_VALUE_STRING));
        assertThat(this.cachedConnectorWrapper.getContent(TEST_KEY), is(TEST_VALUE_CONTENT));
        assertThat(this.cachedConnectorWrapper.getObject(TEST_KEY), is(TEST_VALUE_OBJECT));
        this.cachedConnectorWrapper.clearCachedValue(TEST_KEY);
        assertThat(this.cachedConnectorWrapper.getString(TEST_KEY), is(TEST_VALUE_STRING));
        assertThat(this.cachedConnectorWrapper.getContent(TEST_KEY), is(TEST_VALUE_CONTENT));
        assertThat(this.cachedConnectorWrapper.getObject(TEST_KEY), is(TEST_VALUE_OBJECT));

        // validate
        Mockito.verify(this.targetConnector, Mockito.times(2)).getString(TEST_KEY);
        Mockito.verify(this.targetConnector, Mockito.times(2)).getContent(TEST_KEY);
        Mockito.verify(this.targetConnector, Mockito.times(2)).getObject(TEST_KEY);
    }

    @Test
    public void testAddConnector() {
        // prepare
        SystemPropertyConnector connector = new SystemPropertyConnector();

        // test
        this.cachedConnectorWrapper.addConnector(connector);

        // validate
        Mockito.verify(this.targetConnector).addConnector(connector);
    }

    @Test
    public void testSetContentResolver() {
        // prepare
        ClasspathContentResolver contentResolver = new ClasspathContentResolver();

        // test
        this.cachedConnectorWrapper.setContentResolver(contentResolver);

        // validate
        Mockito.verify(this.targetConnector).setContentResolver(contentResolver);
    }

    @Test
    public void testSetObjectResolver() {
        // prepare
        JavaXMLBeansObjectResolver objectResolver = new JavaXMLBeansObjectResolver();

        // test
        this.cachedConnectorWrapper.setObjectResolver(objectResolver);

        // validate
        Mockito.verify(this.targetConnector).setObjectResolver(objectResolver);
    }

    @Test
    public void testInit() {

        // test
        this.cachedConnectorWrapper.init();

        // validate
        Mockito.verify(this.targetConnector).init();
    }

    @Test
    public void testGetName() {
        // prepare
        Mockito.when(this.targetConnector.getName()).thenReturn("testName");

        // test
        assertThat(this.cachedConnectorWrapper.getName(), is("testName"));

        // validate
        Mockito.verify(this.targetConnector).getName();
    }

    @Test
    public void testSetName() {

        // test
        this.cachedConnectorWrapper.setName("test");

        // validate
        Mockito.verify(this.targetConnector).setName("test");
    }
}
