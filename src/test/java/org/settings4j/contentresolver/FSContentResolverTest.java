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
package org.settings4j.contentresolver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FSContentResolverTest {

    /** General Logger for this Class. */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(FSContentResolverTest.class);

    private static final String BASE_DIR = FSContentResolverTest.class.getPackage().getName().replace('.', '/');

    private File testDir;

    @Before
    public void setUp() throws Exception {
        this.testDir = (new File("test/ContentResolverTest/".toLowerCase())).getAbsoluteFile();
        FileUtils.forceMkdir(this.testDir);
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(new File("test"));
    }

    @Test
    public void testReadHelloWorldTxt() throws Exception {
        // Copy the required Test-file to the Temp-Path
        final InputStream helloWorldIS = this.getClass().getClassLoader()
            .getResourceAsStream(BASE_DIR + "/HelloWorld.txt");
        FileUtils.forceMkdir(new File(this.testDir.getAbsolutePath() + "/org/settings4j/contentresolver"));
        final String helloWorldPath = this.testDir.getAbsolutePath() + "/org/settings4j/contentresolver/HelloWorld.txt";
        final FileOutputStream fileOutputStream = new FileOutputStream(new File(helloWorldPath));
        IOUtils.copy(helloWorldIS, fileOutputStream);
        IOUtils.closeQuietly(helloWorldIS);
        IOUtils.closeQuietly(fileOutputStream);
        LOG.info("helloWorldPath: {}", helloWorldPath);

        final FSContentResolver contentResolver = new FSContentResolver();
        contentResolver.setRootFolderPath(this.testDir.getAbsolutePath());

        byte[] content = contentResolver.getContent("org/settings4j/contentresolver/HelloWorld.txt");
        assertThat(content, is(notNullValue()));
        assertThat(new String(content, "UTF-8"), is("Hello World"));

        content = contentResolver.getContent("file:org/settings4j/contentresolver/HelloWorld.txt");
        assertThat(content, is(notNullValue()));
        assertThat(new String(content, "UTF-8"), is("Hello World"));

        content = contentResolver.getContent("file:/org/settings4j/contentresolver/HelloWorld.txt");
        assertThat(content, is(notNullValue()));
        assertThat(new String(content, "UTF-8"), is("Hello World"));


        content = contentResolver.getContent("file:laksjdhalksdhfa");
        assertThat(content, is(nullValue()));

        content = contentResolver.getContent("/org/settings4j/contentresolver/HelloWorld.txt");
        assertThat(content, is(notNullValue()));
        assertThat(new String(content, "UTF-8"), is("Hello World"));
    }

    @Test
    public void testWriteHelloWorldTxt() throws Exception {

        FileUtils.deleteDirectory(new File(this.testDir.getCanonicalPath() + "/org"));

        final InputStream helloWorldIS = this.getClass().getClassLoader()
            .getResourceAsStream(BASE_DIR + "/HelloWorld.txt");
        final byte[] value = IOUtils.toByteArray(helloWorldIS);
        helloWorldIS.close();

        final FSContentResolver contentResolver = new FSContentResolver();
        contentResolver.setRootFolderPath(this.testDir.getAbsolutePath());

        byte[] content = contentResolver.getContent("org/settings4j/contentresolver/HelloWorld.txt");
        assertThat(content, is(nullValue()));

        content = contentResolver.getContent("file:org/settings4j/contentresolver/HelloWorld.txt");
        assertThat(content, is(nullValue()));

        // store value;
        FileUtils.writeByteArrayToFile(new File(this.testDir.getAbsolutePath()
            + "/org/settings4j/contentresolver/HelloWorld.txt"), value);


        content = contentResolver.getContent("org/settings4j/contentresolver/HelloWorld.txt");
        assertThat(content, is(notNullValue()));
        assertThat(new String(content, "UTF-8"), is("Hello World"));

        content = contentResolver.getContent("file:org/settings4j/contentresolver/HelloWorld.txt");
        assertThat(content, is(notNullValue()));
        assertThat(new String(content, "UTF-8"), is("Hello World"));


        content = contentResolver.getContent("file:laksjdhalksdhfa");
        assertThat(content, is(nullValue()));

        content = contentResolver.getContent("/org/settings4j/contentresolver/HelloWorld.txt");
        assertThat(content, is(notNullValue()));
        assertThat(new String(content, "UTF-8"), is("Hello World"));
    }
}
