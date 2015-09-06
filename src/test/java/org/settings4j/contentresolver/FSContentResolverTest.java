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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class FSContentResolverTest {

    /** General Logger for this Class. */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(FSContentResolverTest.class);

    private static final String BASE_DIR = FSContentResolverTest.class.getPackage().getName().replace('.', '/');

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
    public void testInitialRootFolder() throws Exception {
        final FSContentResolver contentResolver = new FSContentResolver();

        assertThat(contentResolver.getRootFolder(), is(notNullValue()));
        assertThat(contentResolver.getRootFolder().getCanonicalPath(), is(new File(".").getCanonicalPath()));
    }

    @Test
    public void testUnsupportedContentResolver() throws Exception {
        this.thrown.expect(UnsupportedOperationException.class);
        this.thrown.expectMessage(containsString("FSContentResolver cannot add other ContentResolvers"));
        final FSContentResolver contentResolver = new FSContentResolver();
        contentResolver.addContentResolver(new FSContentResolver());
    }

    @Test
    public void testInvalidRootFolder() throws Exception {
        String invalidFolderPAth = "./pom.xml/test";
        this.thrown.expect(IllegalArgumentException.class);
        this.thrown.expectMessage(containsString("Unable to create RootFolder:"));
        this.thrown.expectMessage(containsString(invalidFolderPAth));
        final FSContentResolver contentResolver = new FSContentResolver();
        contentResolver.setRootFolderPath(invalidFolderPAth);
    }

    @Test
    public void testReadHelloWorldTxt() throws Exception {
        // Copy the required Test-file to the Temp-Path
        FileUtils.forceMkdir(new File(this.testDir.getAbsolutePath() + "/org/settings4j/contentresolver"));
        final byte[] value = readFileContent(BASE_DIR + "/HelloWorld.txt");

        final String helloWorldPath = this.testDir.getAbsolutePath() + "/org/settings4j/contentresolver/HelloWorld.txt";
        LOG.info("helloWorldPath: {}", helloWorldPath);
        FileUtils.writeByteArrayToFile(new File(helloWorldPath), value);

        final FSContentResolver contentResolver = new FSContentResolver();
        contentResolver.setRootFolderPath(this.testDir.getAbsolutePath());

        // run and validate getContent()
        assertFileContent(contentResolver, "org/settings4j/contentresolver/HelloWorld.txt", "Hello World");
        assertFileContent(contentResolver, "/org/settings4j/contentresolver/HelloWorld.txt", "Hello World");
        assertFileContent(contentResolver, "file:org/settings4j/contentresolver/HelloWorld.txt", "Hello World");
        assertFileContent(contentResolver, "file:/org/settings4j/contentresolver/HelloWorld.txt", "Hello World");
        assertFileContent(contentResolver, helloWorldPath, "Hello World");
        assertFileContent(contentResolver, "file:laksjdhalksdhfa", null);
    }

    @Test
    public void testWriteHelloWorldTxt1() throws Exception {
        testWriteHelloWorldTxt("org/settings4j/contentresolver/HelloWorld.txt");
    }

    @Test
    public void testWriteHelloWorldTxt2() throws Exception {
        this.thrown.expect(IllegalArgumentException.class);
        this.thrown.expectMessage(containsString("can only store content relative to"));
        this.thrown.expectMessage(containsString(this.testDir.getAbsolutePath()));
        this.thrown.expectMessage(containsString("/org/settings4j"));
        testWriteHelloWorldTxt("/org/settings4j/contentresolver/HelloWorld.txt");
    }

    @Test
    public void testWriteHelloWorldTxt3() throws Exception {
        testWriteHelloWorldTxt("file:org/settings4j/contentresolver/HelloWorld.txt");
    }

    @Test
    public void testWriteHelloWorldTxt4() throws Exception {
        this.thrown.expect(IllegalArgumentException.class);
        this.thrown.expectMessage(containsString("can only store content relative to"));
        this.thrown.expectMessage(containsString(this.testDir.getAbsolutePath()));
        this.thrown.expectMessage(containsString("file:/org/settings4j"));
        testWriteHelloWorldTxt("file:/org/settings4j/contentresolver/HelloWorld.txt");
    }

    @Test
    public void testWriteHelloWorldTxt5() throws Exception {
        this.thrown.expect(IllegalArgumentException.class);
        this.thrown.expectMessage(containsString("can only store content relative to"));
        this.thrown.expectMessage(containsString(this.testDir.getAbsolutePath()));
        this.thrown.expectMessage(containsString("file:C:/org"));
        testWriteHelloWorldTxt("file:C:/org/settings4j/contentresolver/HelloWorld.txt");
    }

    public void testWriteHelloWorldTxt(final String key) throws Exception {

        final byte[] value = readFileContent(BASE_DIR + "/HelloWorld.txt");

        final FSContentResolver contentResolver = new FSContentResolver();
        contentResolver.setRootFolderPath(this.testDir.getAbsolutePath());

        checkPrerequirements(contentResolver);

        // store value;
        contentResolver.setContent(key, value);

        // run and validate getContent()
        assertFileContent(contentResolver, "org/settings4j/contentresolver/HelloWorld.txt", "Hello World");
        assertFileContent(contentResolver, "/org/settings4j/contentresolver/HelloWorld.txt", "Hello World");
        assertFileContent(contentResolver, "file:org/settings4j/contentresolver/HelloWorld.txt", "Hello World");
        assertFileContent(contentResolver, "file:/org/settings4j/contentresolver/HelloWorld.txt", "Hello World");
        assertFileContent(contentResolver, "file:laksjdhalksdhfa", null);
    }

    private void checkPrerequirements(final FSContentResolver contentResolver) {
        byte[] content = contentResolver.getContent("org/settings4j/contentresolver/HelloWorld.txt");
        assertThat(content, is(nullValue()));

        content = contentResolver.getContent("file:org/settings4j/contentresolver/HelloWorld.txt");
        assertThat(content, is(nullValue()));
    }

    private void assertFileContent(final FSContentResolver contentResolver, final String key, final String fileContentString)
        throws UnsupportedEncodingException {
        byte[] content = contentResolver.getContent(key);
        if (fileContentString == null) {
            assertThat(content, is(nullValue()));
        } else {
            assertThat(content, is(notNullValue()));
            assertThat(new String(content, "UTF-8"), is(fileContentString));
        }
    }

    private byte[] readFileContent(final String filename) throws IOException {
        final InputStream helloWorldIS = this.getClass().getClassLoader().getResourceAsStream(filename);
        final byte[] value = IOUtils.toByteArray(helloWorldIS);
        helloWorldIS.close();
        return value;
    }
}
