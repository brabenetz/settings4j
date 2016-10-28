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
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.settings4j.Connector;
import org.settings4j.ContentResolver;
import org.settings4j.contentresolver.ClasspathContentResolver;
import org.settings4j.contentresolver.FSContentResolver;
import org.settings4j.contentresolver.UnionContentResolver;

public class SystemPropertyConnectorTest {

    /** General Logger for this Class. */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SystemPropertyConnectorTest.class);

    private File testDir;

    @Before
    public void setUp() throws Exception {
        this.testDir = (new File("test/ConnectorTest/".toLowerCase())).getAbsoluteFile();
        FileUtils.forceMkdir(this.testDir);
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(new File("test"));
    }

    @Test
    public void testSystemPropertyConnector() throws Exception {
        final String rootFolderPath = "test/ConnectorTest/fs/".toLowerCase();

        final Connector connector = new SystemPropertyConnector();
        final ContentResolver contentResolver = new UnionContentResolver();
        final FSContentResolver fsContentResolver = new FSContentResolver();
        fsContentResolver.setRootFolderPath(rootFolderPath);
        contentResolver.addContentResolver(fsContentResolver);
        contentResolver.addContentResolver(new ClasspathContentResolver());
        connector.setContentResolver(contentResolver);

        String resultString;
        byte[] resultContent;
        Object resultObject;

        resultString = connector.getString("helloWorldPath");
        assertThat(resultString, is(nullValue()));

        resultContent = connector.getContent("helloWorldPath");
        assertThat(resultContent, is(nullValue()));

        resultObject = connector.getObject("helloWorldPath");
        assertThat(resultObject, is(nullValue()));

        System.setProperty("helloWorldPath", "org/settings4j/connector/HelloWorld2.txt");

        resultString = connector.getString("helloWorldPath");
        assertThat(resultString, is(notNullValue()));
        assertThat(resultString, is("org/settings4j/connector/HelloWorld2.txt"));

        resultContent = connector.getContent("helloWorldPath");
        assertThat(resultContent, is(notNullValue()));
        assertThat(new String(resultContent, "UTF-8"), is("Hello World 2"));

        // No Object Resolver configured
        resultObject = connector.getObject("helloWorldPath");
        assertThat(resultObject, is(nullValue()));

        System.setProperty("helloWorldPath", "file:org/settings4j/connector/HelloWorld2.txt");

        resultString = connector.getString("helloWorldPath");
        assertThat(resultString, is(notNullValue()));
        assertThat(resultString, is("file:org/settings4j/connector/HelloWorld2.txt"));

        resultContent = connector.getContent("helloWorldPath");
        assertThat(resultContent, is(nullValue()));

        resultObject = connector.getObject("helloWorldPath");
        assertThat(resultObject, is(nullValue()));

        System.setProperty("helloWorldPath", "classpath:org/settings4j/connector/HelloWorld2.txt");

        resultString = connector.getString("helloWorldPath");
        assertThat(resultString, is(notNullValue()));
        assertThat(resultString, is("classpath:org/settings4j/connector/HelloWorld2.txt"));

        resultContent = connector.getContent("helloWorldPath");
        assertThat(resultContent, is(notNullValue()));
        assertThat(new String(resultContent, "UTF-8"), is("Hello World 2"));

        resultObject = connector.getObject("helloWorldPath");
        assertThat(resultObject, is(nullValue()));


        final InputStream helloWorldIS = new ByteArrayInputStream("Hello World 2 - Test".getBytes("UTF-8"));
        FileUtils.forceMkdir(new File(rootFolderPath + "/org/settings4j/connector"));
        final String helloWorldPath = rootFolderPath + "/org/settings4j/connector/HelloWorld2.txt";
        final FileOutputStream fileOutputStream = new FileOutputStream(new File(helloWorldPath));
        IOUtils.copy(helloWorldIS, fileOutputStream);
        IOUtils.closeQuietly(helloWorldIS);
        IOUtils.closeQuietly(fileOutputStream);
        LOG.info("helloWorld2Path: {}", helloWorldPath);


        System.setProperty("helloWorldPath", "file:org/settings4j/connector/HelloWorld2.txt");

        resultString = connector.getString("helloWorldPath");
        assertThat(resultString, is(notNullValue()));
        assertThat(resultString, is("file:org/settings4j/connector/HelloWorld2.txt"));

        resultContent = connector.getContent("helloWorldPath");
        assertThat(resultContent, is(notNullValue()));
        assertThat(new String(resultContent, "UTF-8"), is("Hello World 2 - Test"));

        resultObject = connector.getObject("helloWorldPath");
        assertThat(resultObject, is(nullValue()));

        // the FS-ContentResolver is the first one => so it must be readed from the FileSystem.
        System.setProperty("helloWorldPath", "org/settings4j/connector/HelloWorld2.txt");

        resultString = connector.getString("helloWorldPath");
        assertThat(resultString, is(notNullValue()));
        assertThat(resultString, is("org/settings4j/connector/HelloWorld2.txt"));

        resultContent = connector.getContent("helloWorldPath");

        resultContent = connector.getContent("helloWorldPath");
        assertThat(resultContent, is(notNullValue()));
        assertThat(new String(resultContent, "UTF-8"), is("Hello World 2 - Test"));

        resultObject = connector.getObject("helloWorldPath");
        assertThat(resultObject, is(nullValue()));

        // from classpath
        System.setProperty("helloWorldPath", "classpath:org/settings4j/connector/HelloWorld2.txt");

        resultString = connector.getString("helloWorldPath");
        assertThat(resultString, is(notNullValue()));
        assertThat(resultString, is("classpath:org/settings4j/connector/HelloWorld2.txt"));

        resultContent = connector.getContent("helloWorldPath");
        assertThat(resultContent, is(notNullValue()));
        assertThat(new String(resultContent, "UTF-8"), is("Hello World 2"));

        resultObject = connector.getObject("helloWorldPath");
        assertThat(resultObject, is(nullValue()));

    }
}
