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

import java.io.File;

import javax.naming.Context;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.settings4j.Constants;
import org.settings4j.ContentResolver;
import org.settings4j.contentresolver.ClasspathContentResolver;
import org.settings4j.contentresolver.FSContentResolver;
import org.settings4j.contentresolver.UnionContentResolver;

public class JNDIConnectorTest extends TestCase {

    /** General Logger for this Class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(JNDIConnectorTest.class);

    private final String charset = "UTF-8";
    private File testDir;

    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();
        this.testDir = (new File("test/ConnectorTest/".toLowerCase()));
        FileUtils.forceMkdir(this.testDir);
        removeJNDIContextProperties();
    }

    /** {@inheritDoc} */
    protected void tearDown() throws Exception {
        FileUtils.deleteDirectory(new File("test"));
        removeJNDIContextProperties();
        super.tearDown();
    }


    public void testJNDIConnectorWithoutJNDI() throws Exception {
        LOG.info("START: testJNDIConnectorWithoutJNDI");

        JNDIConnector connector;
        int saveStatus;
        String resultString;
        byte[] resultContent;
        Object resultObject;

        connector = new JNDIConnector();

        // test
        assertNull(connector.getIsJNDIAvailable());
        assertFalse(connector.isJNDIAvailable());
        assertNotNull(connector.getIsJNDIAvailable());
        assertFalse(connector.isJNDIAvailable());

        resultString = connector.getString("helloWorldPath");
        assertNull(resultString);

        resultContent = connector.getContent("helloWorldPath");
        assertNull(resultContent);

        resultObject = connector.getObject("helloWorldPath");
        assertNull(resultObject);

        // No Exception if JNDIContext not available
        saveStatus = connector.setObject("helloWorldPath", this.testDir.getAbsolutePath() + "/test.txt");
        resultString = connector.getString("helloWorldPath");
        assertNull(resultString);

        // No Exception if JNDIContext not available
        saveStatus = connector.setObject("helloWorldPath", this.testDir.getAbsolutePath() + "/test.txt");
        assertEquals(Constants.SETTING_NOT_POSSIBLE, saveStatus);

        // No Exception if JNDIContext not available
        saveStatus = connector.setObject("helloWorldPath", this.testDir.getAbsolutePath() + "/test.txt");
        assertEquals(Constants.SETTING_NOT_POSSIBLE, saveStatus);


        // add contentResolver
        final ContentResolver contentResolver = new UnionContentResolver();
        final FSContentResolver fsContentResolver = new FSContentResolver();
        fsContentResolver.setRootFolderPath(this.testDir.getAbsolutePath());
        contentResolver.addContentResolver(new ClasspathContentResolver());
        contentResolver.addContentResolver(fsContentResolver);
        connector.setContentResolver(contentResolver);


        resultString = connector.getString("helloWorldPath");
        assertNull(resultString);

        resultContent = connector.getContent("helloWorldPath");
        assertNull(resultContent);

        resultObject = connector.getObject("helloWorldPath");
        assertNull(resultObject);


    }

    public void testJNDIConnectorWithJNDI() throws Exception {
        LOG.info("START: testJNDIConnectorWithJNDI");
        // Set JNDI Tomcat Configs
        setTomcatJNDIContextProperties();

        final String testTextPath = this.testDir.getAbsolutePath() + "/test.txt";

        JNDIConnector connector;
        int saveStatus;
        String resultString;
        byte[] resultContent;
        Object resultObject;

        connector = new JNDIConnector();

        resultString = connector.getString("helloWorldPath");
        assertNull(resultString);

        resultContent = connector.getContent("helloWorldPath");
        assertNull(resultContent);

        resultObject = connector.getObject("helloWorldPath");
        assertNull(resultObject);

        // set the PATH into the JNDI-Context
        saveStatus = connector.setObject("helloWorldPath", testTextPath);
        assertEquals(Constants.SETTING_SUCCESS, saveStatus);
        resultString = connector.getString("helloWorldPath");
        assertEquals(testTextPath, resultString);

        // if no ContentResolver is available. the value will be stored directly into the JNDI-Context
        saveStatus = connector.setObject("helloWorldPath", "Hello World".getBytes(this.charset));
        assertEquals(Constants.SETTING_SUCCESS, saveStatus);
        // The String-Value cannot read a byte[]
        resultString = connector.getString("helloWorldPath");
        assertNull(resultString);
        // The Content-Value should be the same
        resultContent = connector.getContent("helloWorldPath");
        assertNotNull(resultContent);
        assertEquals("Hello World", new String(resultContent, this.charset));


        // No Exception if JNDIContext not available
        saveStatus = connector.setObject("helloWorldPath", testTextPath);
        assertEquals(Constants.SETTING_SUCCESS, saveStatus);
        resultString = connector.getString("helloWorldPath");
        assertEquals(testTextPath, resultString);
        resultObject = connector.getObject("helloWorldPath");
        assertEquals(testTextPath, resultObject);

        // if no ObjectResolver is available. the value will be stored directly into the JNDI-Context
        final ContentResolver contentResolver = new UnionContentResolver();
        final FSContentResolver fsContentResolver = new FSContentResolver();
        fsContentResolver.setRootFolderPath(this.testDir.getAbsolutePath()); // should also work with temp-folder
        contentResolver.addContentResolver(fsContentResolver);
        contentResolver.addContentResolver(new ClasspathContentResolver());
        connector.setContentResolver(contentResolver);


        // set the PATH into the JNDI-Context
        saveStatus = connector.setObject("helloWorldPath", testTextPath);
        assertEquals(Constants.SETTING_SUCCESS, saveStatus);
        resultString = connector.getString("helloWorldPath");
        assertEquals(testTextPath, resultString);

        // Store the content into the file on the file system.
        // And save the Path in the JNDI Context.
        FileUtils.writeStringToFile(new File(testTextPath), "Hello World FileContent");
        saveStatus = connector.setObject("helloWorldPath", testTextPath);

        assertEquals(Constants.SETTING_SUCCESS, saveStatus);
        // The String-Value should be the text-File Path.
        resultString = connector.getString("helloWorldPath");
        assertEquals(testTextPath, resultString);
        // The Content-Value should be content of the file.
        resultContent = connector.getContent("helloWorldPath");
        assertNotNull(resultContent);
        assertEquals("Hello World FileContent", new String(resultContent, this.charset));


    }

    public void testJNDIConnectorIsAvailable() throws Exception {
        LOG.info("START: testJNDIConnectorIsAvailable");
        JNDIConnector connector;

        connector = new JNDIConnector();

        // JNDIContext not available
        assertNull(connector.getIsJNDIAvailable());
        assertFalse(connector.isJNDIAvailable()); // will set the boolean Flag
        assertNotNull(connector.getIsJNDIAvailable());
        assertFalse(connector.isJNDIAvailable());

        // Set JNDI Tomcat Configs (Enable JNDI Context)
        setTomcatJNDIContextProperties();

        // JNDI-Connector is disabled:
        connector.setObject("irgendwas", "Something to create parent Context");
        assertNull(connector.getObject("irgendwas"));

        // reset boolean Flag
        connector.setIsJNDIAvailable(null);

        // JNDIContext is now available
        assertNull(connector.getIsJNDIAvailable());
        assertTrue(connector.isJNDIAvailable()); // will set the boolean Flag again
        assertNotNull(connector.getIsJNDIAvailable());
        assertTrue(connector.isJNDIAvailable());


        // test setObject and getObject.
        assertNull(connector.getObject("irgendwas"));
        connector.setObject("irgendwas", "Something to test");
        assertEquals("Something to test", connector.getObject("irgendwas"));

    }

    public static void setTomcatJNDIContextProperties() {
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
        System.setProperty(Context.PROVIDER_URL, "localhost:1099");
        System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");

    }

    private void removeJNDIContextProperties() {
        System.getProperties().remove(Context.INITIAL_CONTEXT_FACTORY);
        System.getProperties().remove(Context.PROVIDER_URL);
        System.getProperties().remove(Context.URL_PKG_PREFIXES);
    }
}
