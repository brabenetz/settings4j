/* ***************************************************************************
 * Copyright (c) 2008 Brabenetz Harald, Austria.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 *****************************************************************************/

package org.settings4j.connector;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.settings4j.Connector;
import org.settings4j.ContentResolver;
import org.settings4j.contentresolver.ClasspathContentResolver;
import org.settings4j.contentresolver.FSContentResolver;
import org.settings4j.contentresolver.UnionContentResolver;

public class SystemPropertyConnectorTest extends TestCase {

    /** General Logger for this Class. */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SystemPropertyConnectorTest.class);

    private File testDir;

    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();
        this.testDir = (new File("test/ConnectorTest/".toLowerCase())).getAbsoluteFile();
        FileUtils.forceMkdir(this.testDir);
    }

    /** {@inheritDoc} */
    protected void tearDown() throws Exception {
        FileUtils.deleteDirectory(new File("test"));
        super.tearDown();
    }


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
        assertNull(resultString);

        resultContent = connector.getContent("helloWorldPath");
        assertNull(resultContent);

        resultObject = connector.getObject("helloWorldPath");
        assertNull(resultObject);

        System.setProperty("helloWorldPath", "org/settings4j/connector/HelloWorld2.txt");

        resultString = connector.getString("helloWorldPath");
        assertNotNull(resultString);
        assertEquals("org/settings4j/connector/HelloWorld2.txt", resultString);

        resultContent = connector.getContent("helloWorldPath");
        assertNotNull(resultContent);
        assertEquals("Hello World 2", new String(resultContent, "UTF-8"));

        // No Object Resolver configured
        resultObject = connector.getObject("helloWorldPath");
        assertNull(resultObject);

        System.setProperty("helloWorldPath", "file:org/settings4j/connector/HelloWorld2.txt");

        resultString = connector.getString("helloWorldPath");
        assertNotNull(resultString);
        assertEquals("file:org/settings4j/connector/HelloWorld2.txt", resultString);

        resultContent = connector.getContent("helloWorldPath");
        assertNull(resultObject);

        resultObject = connector.getObject("helloWorldPath");
        assertNull(resultObject);

        System.setProperty("helloWorldPath", "classpath:org/settings4j/connector/HelloWorld2.txt");

        resultString = connector.getString("helloWorldPath");
        assertNotNull(resultString);
        assertEquals("classpath:org/settings4j/connector/HelloWorld2.txt", resultString);

        resultContent = connector.getContent("helloWorldPath");
        assertNotNull(resultContent);
        assertEquals("Hello World 2", new String(resultContent, "UTF-8"));

        resultObject = connector.getObject("helloWorldPath");
        assertNull(resultObject);


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
        assertNotNull(resultString);
        assertEquals("file:org/settings4j/connector/HelloWorld2.txt", resultString);

        resultContent = connector.getContent("helloWorldPath");
        assertNotNull(resultContent);
        assertEquals("Hello World 2 - Test", new String(resultContent, "UTF-8"));

        resultObject = connector.getObject("helloWorldPath");
        assertNull(resultObject);

        // the FS-ContentResolver is the first one => so it must be readed from the FileSystem.
        System.setProperty("helloWorldPath", "org/settings4j/connector/HelloWorld2.txt");

        resultString = connector.getString("helloWorldPath");
        assertNotNull(resultString);
        assertEquals("org/settings4j/connector/HelloWorld2.txt", resultString);

        resultContent = connector.getContent("helloWorldPath");

        resultContent = connector.getContent("helloWorldPath");
        assertNotNull(resultContent);
        assertEquals("Hello World 2 - Test", new String(resultContent, "UTF-8"));

        resultObject = connector.getObject("helloWorldPath");
        assertNull(resultObject);

        // from classpath
        System.setProperty("helloWorldPath", "classpath:org/settings4j/connector/HelloWorld2.txt");

        resultString = connector.getString("helloWorldPath");
        assertNotNull(resultString);
        assertEquals("classpath:org/settings4j/connector/HelloWorld2.txt", resultString);

        resultContent = connector.getContent("helloWorldPath");
        assertNotNull(resultContent);
        assertEquals("Hello World 2", new String(resultContent, "UTF-8"));

        resultObject = connector.getObject("helloWorldPath");
        assertNull(resultObject);

    }
}
