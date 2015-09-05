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
package org.settings4j.connector;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.settings4j.Connector;
import org.settings4j.Filter;
import org.settings4j.contentresolver.ClasspathContentResolver;
import org.settings4j.objectresolver.JavaXMLBeansObjectResolver;

@RunWith(MockitoJUnitRunner.class)
public class FilteredConnectorWrapperTest {

    private static final String TEST_VALUE_STRING = "Test-Value";

    private static final byte[] TEST_VALUE_CONTENT = "Test-Value".getBytes();

    private static final Object TEST_VALUE_OBJECT = "Test-Value";

    private static final String TEST_KEY = "TEST";

    @InjectMocks
    private FilteredConnectorWrapper filteredConnectorWrapper;

    @Mock
    private Connector targetConnector;

    @Mock
    private Filter filter;


    @Test
    public void testGetString() {
        // prepare
        Mockito.when(this.filter.isValid(TEST_KEY)).thenReturn(true);
        Mockito.when(this.targetConnector.getString(TEST_KEY)).thenReturn(TEST_VALUE_STRING);

        // test
        assertThat(this.filteredConnectorWrapper.getString(TEST_KEY), is(TEST_VALUE_STRING));

        // validate
        Mockito.verify(this.targetConnector).getString(TEST_KEY);
    }

    @Test
    public void testGetStringFiltered() {
        // prepare
        Mockito.when(this.filter.isValid(TEST_KEY)).thenReturn(false);

        // test
        assertThat(this.filteredConnectorWrapper.getString(TEST_KEY), is(nullValue()));

        // validate
        Mockito.verifyZeroInteractions(this.targetConnector);
    }

    @Test
    public void testGetContent() {
        // prepare
        Mockito.when(this.filter.isValid(TEST_KEY)).thenReturn(true);
        Mockito.when(this.targetConnector.getContent(TEST_KEY)).thenReturn(TEST_VALUE_CONTENT);

        // test
        assertThat(this.filteredConnectorWrapper.getContent(TEST_KEY), is(TEST_VALUE_CONTENT));

        // validate
        Mockito.verify(this.targetConnector).getContent(TEST_KEY);
    }

    @Test
    public void testGetContentFiltered() {
        // prepare
        Mockito.when(this.filter.isValid(TEST_KEY)).thenReturn(false);
        Mockito.when(this.targetConnector.getContent(TEST_KEY)).thenReturn(TEST_VALUE_CONTENT);

        // test
        assertThat(this.filteredConnectorWrapper.getContent(TEST_KEY), is(nullValue()));

        // validate
        Mockito.verifyZeroInteractions(this.targetConnector);
    }

    @Test
    public void testGetObject() {
        // prepare
        Mockito.when(this.filter.isValid(TEST_KEY)).thenReturn(true);
        Mockito.when(this.targetConnector.getObject(TEST_KEY)).thenReturn(TEST_VALUE_OBJECT);

        // test
        assertThat(this.filteredConnectorWrapper.getObject(TEST_KEY), is(TEST_VALUE_OBJECT));

        // validate
        Mockito.verify(this.targetConnector).getObject(TEST_KEY);
    }

    @Test
    public void testGetObjectFiltered() {
        // prepare
        Mockito.when(this.filter.isValid(TEST_KEY)).thenReturn(false);
        Mockito.when(this.targetConnector.getObject(TEST_KEY)).thenReturn(TEST_VALUE_OBJECT);

        // test
        assertThat(this.filteredConnectorWrapper.getObject(TEST_KEY), is(nullValue()));

        // validate
        Mockito.verifyZeroInteractions(this.targetConnector);
    }

    @Test
    public void testAddConnector() {
        // prepare
        SystemPropertyConnector connector = new SystemPropertyConnector();

        // test
        this.filteredConnectorWrapper.addConnector(connector);

        // validate
        Mockito.verify(this.targetConnector).addConnector(connector);
    }

    @Test
    public void testSetContentResolver() {
        // prepare
        ClasspathContentResolver contentResolver = new ClasspathContentResolver();

        // test
        this.filteredConnectorWrapper.setContentResolver(contentResolver);

        // validate
        Mockito.verify(this.targetConnector).setContentResolver(contentResolver);
    }

    @Test
    public void testSetObjectResolver() {
        // prepare
        JavaXMLBeansObjectResolver objectResolver = new JavaXMLBeansObjectResolver();

        // test
        this.filteredConnectorWrapper.setObjectResolver(objectResolver);

        // validate
        Mockito.verify(this.targetConnector).setObjectResolver(objectResolver);
    }

    @Test
    public void testInit() {

        // test
        this.filteredConnectorWrapper.init();

        // validate
        Mockito.verify(this.targetConnector).init();
    }

    @Test
    public void testGetName() {
        // prepare
        Mockito.when(this.targetConnector.getName()).thenReturn("testName");

        // test
        assertThat(this.filteredConnectorWrapper.getName(), is("testName"));

        // validate
        Mockito.verify(this.targetConnector).getName();
    }

    @Test
    public void testSetName() {

        // test
        this.filteredConnectorWrapper.setName("test");

        // validate
        Mockito.verify(this.targetConnector).setName("test");
    }
}
