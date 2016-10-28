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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.io.File;
import java.nio.charset.UnsupportedCharsetException;

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
import org.settings4j.contentresolver.FSContentResolver;

@RunWith(MockitoJUnitRunner.class)
public class FSConnectorTest {

    private static final String TEST_VALUE_STRING = "Test-Value";

    private static final byte[] TEST_VALUE_CONTENT = "Test-Value".getBytes();

    private static final Object TEST_VALUE_OBJECT = "Test-Value";

    private static final String TEST_KEY = "TEST";

    @InjectMocks
    private FSConnector fsConnector;

    @Mock
    private FSContentResolver fsContentResolver;

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
        assertThat(this.fsConnector.getContent(TEST_KEY), is(nullValue()));

        // second Test
        Mockito.when(this.fsContentResolver.getContent(TEST_KEY)).thenReturn(TEST_VALUE_CONTENT);
        assertThat(this.fsConnector.getContent(TEST_KEY), is(TEST_VALUE_CONTENT));

    }

    @Test
    public void testGetObject() throws Exception {
        // first Test without ObjectResolver
        assertThat(this.fsConnector.getObject(TEST_KEY), is(nullValue()));

        // second Test with Object Resolver but returns null
        final ObjectResolver objResolver = Mockito.mock(ObjectResolver.class);
        this.fsConnector.setObjectResolver(objResolver);
        assertThat(this.fsConnector.getObject(TEST_KEY), is(nullValue()));

        // third Test
        Mockito.when(objResolver.getObject(Matchers.eq(TEST_KEY), Matchers.any(ContentResolver.class))).thenReturn(TEST_VALUE_OBJECT);
        assertThat(this.fsConnector.getObject(TEST_KEY), is(TEST_VALUE_OBJECT));

    }

    @Test
    public void testGetString() throws Exception {
        // first Test without ObjectResolver
        assertThat(this.fsConnector.getString(TEST_KEY), is(nullValue()));

        // second Test with Object Resolver but returns null
        Mockito.when(this.fsContentResolver.getContent(TEST_KEY)).thenReturn(TEST_VALUE_CONTENT);
        assertThat(this.fsConnector.getString(TEST_KEY), is(TEST_VALUE_STRING));

        // third Test
        assertThat(this.fsConnector.getString(TEST_KEY), is(TEST_VALUE_STRING));

    }

    @Test
    public void testSetContent() throws Exception {
        // run Test
        this.fsConnector.setContent(TEST_KEY, TEST_VALUE_CONTENT);

        // validate result
        Mockito.verify(this.fsContentResolver).setContent(TEST_KEY, TEST_VALUE_CONTENT);

    }

    @Test
    public void testSetString() throws Exception {
        // run Test
        this.fsConnector.setString(TEST_KEY, TEST_VALUE_STRING);

        // validate result
        Mockito.verify(this.fsContentResolver).setContent(TEST_KEY, TEST_VALUE_STRING.getBytes("UTF-8"));

    }

    @Test
    public void testSetContentResolver() throws Exception {
        final ContentResolver contentResolver = new ClasspathContentResolver();
        assertThat(this.fsConnector.getContentResolver(), is(nullValue()));

        // run Test
        this.fsConnector.setContentResolver(contentResolver);

        // validate result
        assertThat(this.fsConnector.getContentResolver(), is(contentResolver));
        assertThat(this.fsConnector.getContentResolver() == contentResolver, is(true));

    }
    @Test
    public void testSetRootFolderPath() throws Exception {
        // prepare and validate pre-requirements
        assertThat(this.testDir.exists(), is(false));

        // run test
        this.fsConnector.setRootFolderPath(this.testDir.getCanonicalPath());

        // validate
        Mockito.verify(this.fsContentResolver).setRootFolderPath(this.testDir.getCanonicalPath());

    }

    @Test
    public void testGetRootFolder() throws Exception {
        // run test
        this.fsConnector.getRootFolder();

        // validate
        Mockito.verify(this.fsContentResolver).getRootFolder();

    }

    @Test
    public void testSetCharset() throws Exception {
        // run test
        this.fsConnector.setCharset("ISO-8859-1");

        // validate
        assertThat(this.fsConnector.getCharset(), is("ISO-8859-1"));

    }

    @Test
    public void testSetCharsetShouldThrowIllegalCharsetNameException() throws Exception {
        this.thrown.expect(UnsupportedCharsetException.class);
        this.thrown.expectMessage(containsString("xyz"));
        // run test
        this.fsConnector.setCharset("xyz");
    }
}
