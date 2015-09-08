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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.io.File;
import java.nio.charset.IllegalCharsetNameException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.settings4j.ContentResolver;
import org.settings4j.ObjectResolver;
import org.settings4j.contentresolver.ClasspathContentResolver;

@RunWith(MockitoJUnitRunner.class)
public class ClasspathConnectorTest {

    private static final String TEST_VALUE_STRING = "Test-Value";

    private static final byte[] TEST_VALUE_CONTENT = "Test-Value".getBytes();

    private static final Object TEST_VALUE_OBJECT = "Test-Value";

    private static final String TEST_KEY = "TEST";

    @InjectMocks
    private ClasspathConnector cpConnector;

    @Mock
    private ClasspathContentResolver classpathContentResolver;

    private File testDir;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        this.testDir = new File("target/test/" + getClass().getSimpleName() + "/".toLowerCase()).getAbsoluteFile();
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(this.testDir);
    }

    @Test
    public void testGetContent() throws Exception {
        // first Test
        assertThat(this.cpConnector.getContent(TEST_KEY), is(nullValue()));

        // second Test
        Mockito.when(this.classpathContentResolver.getContent(TEST_KEY)).thenReturn(TEST_VALUE_CONTENT);
        assertThat(this.cpConnector.getContent(TEST_KEY), is(TEST_VALUE_CONTENT));

    }

    @Test
    public void testGetObject() throws Exception {
        // first Test without ObjectResolver
        assertThat(this.cpConnector.getObject(TEST_KEY), is(nullValue()));

        // second Test with Object Resolver but returns null
        ObjectResolver objResolver = Mockito.mock(ObjectResolver.class);
        this.cpConnector.setObjectResolver(objResolver);
        assertThat(this.cpConnector.getObject(TEST_KEY), is(nullValue()));

        // third Test
        Mockito.when(objResolver.getObject(Matchers.eq(TEST_KEY), Matchers.any(ContentResolver.class))).thenReturn(TEST_VALUE_OBJECT);
        assertThat(this.cpConnector.getObject(TEST_KEY), is(TEST_VALUE_OBJECT));

    }

    @Test
    public void testGetString() throws Exception {
        // first Test without ObjectResolver
        assertThat(this.cpConnector.getString(TEST_KEY), is(nullValue()));

        // second Test with Object Resolver but returns null
        Mockito.when(this.classpathContentResolver.getContent(TEST_KEY)).thenReturn(TEST_VALUE_CONTENT);
        assertThat(this.cpConnector.getString(TEST_KEY), is(TEST_VALUE_STRING));

        // third Test
        assertThat(this.cpConnector.getString(TEST_KEY), is(TEST_VALUE_STRING));

    }

    @Test
    public void testSetContentResolver() throws Exception {
        ContentResolver contentResolver = new ClasspathContentResolver();
        assertThat(this.cpConnector.getContentResolver(), is(nullValue()));

        // run Test
        this.cpConnector.setContentResolver(contentResolver);

        // validate result
        assertThat(this.cpConnector.getContentResolver(), is(contentResolver));
        assertThat(this.cpConnector.getContentResolver() == contentResolver, is(true));

    }

    @Test
    public void testSetCharset() throws Exception {
        // run test
        this.cpConnector.setCharset("ISO-8859-1");

        // validate
        assertThat(this.cpConnector.getCharset(), is("ISO-8859-1"));

    }

    @Test
    public void testSetCharsetShouldThrowIllegalCharsetNameException() throws Exception {
        this.thrown.expect(IllegalCharsetNameException.class);
        this.thrown.expectMessage(containsString("'xyz'"));
        // run test
        this.cpConnector.setCharset("xyz");
    }
}
