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

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.settings4j.Constants;
import org.settings4j.ContentResolver;
import org.settings4j.contentresolver.ClasspathContentResolver;
import org.settings4j.contentresolver.FSContentResolver;
import org.settings4j.contentresolver.UnionContentResolver;

import javax.naming.Context;
import javax.naming.InitialContext;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class JNDIConnectorTest {

    /** General Logger for this Class. */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(JNDIConnectorTest.class);

    private final String charset = "UTF-8";
    private File testDir;

    @Before
    public void setUp() throws Exception {
        this.testDir = (new File("test/ConnectorTest/".toLowerCase()));
        FileUtils.forceMkdir(this.testDir);
        removeJNDIContextProperties();
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(new File("test"));
        removeJNDIContextProperties();
    }

    /**
     * SmokeTest: tests if the {@link #setTomcatJNDIContextProperties()} works which is required for all other
     * TestCases.
     *
     * @throws Exception in case of an error
     */
    @Test
    public void testSmokeTest() throws Exception {
        LOG.info("START: testJNDIConnectorWithoutJNDI");
        // Set JNDI Tomcat Configs
        setTomcatJNDIContextProperties();
        final InitialContext initialContext = new InitialContext();
        Context tmpCtx = initialContext.createSubcontext("java:");
        tmpCtx = tmpCtx.createSubcontext("comp");
        tmpCtx = tmpCtx.createSubcontext("env");
        tmpCtx.bind("testKey", "testValue");

        // start getting the JNDI Resource
        final Context envCtx = (Context) new InitialContext().lookup("java:/comp/env");
        final Object testValue = envCtx.lookup("testKey");

        assertThat(testValue, is((Object) "testValue"));

    }

    @Test
    public void testJNDIConnectorWithoutJNDI() throws Exception {
        LOG.info("START: testJNDIConnectorWithoutJNDI");

        JNDIConnector connector;
        int saveStatus;
        String resultString;
        byte[] resultContent;
        Object resultObject;

        connector = new JNDIConnector();

        // test
        assertThat(connector.getIsJNDIAvailable(), is(nullValue()));
        Assert.assertFalse(connector.isJNDIAvailable());
        assertThat(connector.getIsJNDIAvailable(), is(notNullValue()));
        Assert.assertFalse(connector.isJNDIAvailable());

        resultString = connector.getString("helloWorldPath");
        assertThat(resultString, is(nullValue()));

        resultContent = connector.getContent("helloWorldPath");
        assertThat(resultContent, is(nullValue()));

        resultObject = connector.getObject("helloWorldPath");
        assertThat(resultObject, is(nullValue()));

        // No Exception if JNDIContext not available
        saveStatus = connector.setObject("helloWorldPath", this.testDir.getAbsolutePath() + "/test.txt");
        resultString = connector.getString("helloWorldPath");
        assertThat(resultString, is(nullValue()));

        // No Exception if JNDIContext not available
        saveStatus = connector.setObject("helloWorldPath", this.testDir.getAbsolutePath() + "/test.txt");
        assertThat(saveStatus, is(Constants.SETTING_NOT_POSSIBLE));

        // No Exception if JNDIContext not available
        saveStatus = connector.setObject("helloWorldPath", this.testDir.getAbsolutePath() + "/test.txt");
        assertThat(saveStatus, is(Constants.SETTING_NOT_POSSIBLE));


        // add contentResolver
        final ContentResolver contentResolver = new UnionContentResolver();
        final FSContentResolver fsContentResolver = new FSContentResolver();
        fsContentResolver.setRootFolderPath(this.testDir.getAbsolutePath());
        contentResolver.addContentResolver(new ClasspathContentResolver());
        contentResolver.addContentResolver(fsContentResolver);
        connector.setContentResolver(contentResolver);


        resultString = connector.getString("helloWorldPath");
        assertThat(resultString, is(nullValue()));

        resultContent = connector.getContent("helloWorldPath");
        assertThat(resultContent, is(nullValue()));

        resultObject = connector.getObject("helloWorldPath");
        assertThat(resultObject, is(nullValue()));


    }

    @Test
    public void testJNDIConnectorWithCustomJNDIProperties() throws Exception {
        LOG.info("START: testJNDIConnectorWithJNDI");
        // Remove default JNDI Tomcat Configs
        removeJNDIContextProperties();

        JNDIConnector connector = new JNDIConnector();
        connector.setInitialContextFactory("org.apache.naming.java.javaURLContextFactory");
        connector.setProviderUrl("localhost:1099");
        connector.setUrlPkgPrefixes("org.apache.naming");

        // the value should be found with or without prefix.
        connector.rebindToContext("java:comp/env/myTestKey1", "myTestValue1");


        // start Test and validation
        assertThat(connector.getString("myTestKey1"), is("myTestValue1"));
        assertThat(connector.getString("java:comp/env/myTestKey1"), is("myTestValue1"));
        assertThat(connector.getString("unknown"), is(nullValue()));


    }

    @Test
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
        assertThat(resultString, is(nullValue()));

        resultContent = connector.getContent("helloWorldPath");
        assertThat(resultContent, is(nullValue()));

        resultObject = connector.getObject("helloWorldPath");
        assertThat(resultObject, is(nullValue()));

        // set the PATH into the JNDI-Context
        saveStatus = connector.setObject("helloWorldPath", testTextPath);
        assertThat(saveStatus, is(Constants.SETTING_SUCCESS));
        resultString = connector.getString("helloWorldPath");
        assertThat(resultString, is(testTextPath));

        // if no ContentResolver is available. the value will be stored directly into the JNDI-Context
        saveStatus = connector.setObject("helloWorldPath", "Hello World".getBytes(this.charset));
        assertThat(saveStatus, is(Constants.SETTING_SUCCESS));
        // The String-Value cannot read a byte[]
        resultString = connector.getString("helloWorldPath");
        assertThat(resultString, is(nullValue()));
        // The Content-Value should be the same
        resultContent = connector.getContent("helloWorldPath");
        assertThat(resultContent, is(notNullValue()));
        assertThat(new String(resultContent, this.charset), is("Hello World"));


        // No Exception if JNDIContext not available
        saveStatus = connector.setObject("helloWorldPath", testTextPath);
        assertThat(saveStatus, is(Constants.SETTING_SUCCESS));
        resultString = connector.getString("helloWorldPath");
        assertThat(resultString, is(testTextPath));
        resultObject = connector.getObject("helloWorldPath");
        assertThat(resultObject, is((Object) testTextPath));

        // if no ObjectResolver is available. the value will be stored directly into the JNDI-Context
        final ContentResolver contentResolver = new UnionContentResolver();
        final FSContentResolver fsContentResolver = new FSContentResolver();
        fsContentResolver.setRootFolderPath(this.testDir.getAbsolutePath()); // should also work with temp-folder
        contentResolver.addContentResolver(fsContentResolver);
        contentResolver.addContentResolver(new ClasspathContentResolver());
        connector.setContentResolver(contentResolver);


        // set the PATH into the JNDI-Context
        saveStatus = connector.setObject("helloWorldPath", testTextPath);
        assertThat(saveStatus, is(Constants.SETTING_SUCCESS));
        resultString = connector.getString("helloWorldPath");
        assertThat(resultString, is(testTextPath));

        // Store the content into the file on the file system.
        // And save the Path in the JNDI Context.
        FileUtils.writeStringToFile(new File(testTextPath), "Hello World FileContent", Charsets.UTF_8);
        saveStatus = connector.setObject("helloWorldPath", testTextPath);

        assertThat(saveStatus, is(Constants.SETTING_SUCCESS));
        // The String-Value should be the text-File Path.
        resultString = connector.getString("helloWorldPath");
        assertThat(resultString, is(testTextPath));
        // The Content-Value should be content of the file.
        resultContent = connector.getContent("helloWorldPath");
        assertThat(resultContent, is(notNullValue()));
        assertThat(new String(resultContent, this.charset), is("Hello World FileContent"));


    }

    @Test
    public void testJNDIConnectorWithJNDIAndPathPrefix() throws Exception {
        LOG.info("START: testJNDIConnectorWithJNDIAndPathPrefix");
        // Set JNDI Tomcat Configs
        setTomcatJNDIContextProperties();

        JNDIConnector connector = new JNDIConnector();
        // the value shopould be found with or without prefix.
        connector.rebindToContext("java:comp/env/myTestKey1", "myTestValue1");
        connector.rebindToContext("myTestKey2", "myTestValue2");


        // start Test and validation
        assertThat(connector.getString("myTestKey1"), is("myTestValue1"));
        assertThat(connector.getString("myTestKey2"), is("myTestValue2"));
        assertThat(connector.getString("/myTestKey1"), is("myTestValue1"));
        assertThat(connector.getString("/myTestKey2"), is("myTestValue2"));
        assertThat(connector.getString("xyz"), is(nullValue()));

    }

    @Test
    public void testJNDIConnectorIsAvailable() throws Exception {
        LOG.info("START: testJNDIConnectorIsAvailable");
        JNDIConnector connector;

        connector = new JNDIConnector();

        // JNDIContext not available
        assertThat(connector.getIsJNDIAvailable(), is(nullValue()));
        Assert.assertFalse(connector.isJNDIAvailable()); // will set the boolean Flag
        assertThat(connector.getIsJNDIAvailable(), is(notNullValue()));
        Assert.assertFalse(connector.isJNDIAvailable());

        // Set JNDI Tomcat Configs (Enable JNDI Context)
        setTomcatJNDIContextProperties();

        // JNDI-Connector is disabled:
        connector.setObject("irgendwas", "Something to create parent Context");
        assertThat(connector.getObject("irgendwas"), is(nullValue()));

        // reset boolean Flag
        connector.setIsJNDIAvailable(null);

        // JNDIContext is now available
        assertThat(connector.getIsJNDIAvailable(), is(nullValue()));
        Assert.assertTrue(connector.isJNDIAvailable()); // will set the boolean Flag
        assertThat(connector.getIsJNDIAvailable(), is(notNullValue()));
        Assert.assertTrue(connector.isJNDIAvailable());


        // test setObject and getObject.
        assertThat(connector.getObject("irgendwas"), is(nullValue()));
        connector.setObject("irgendwas", "Something to test");
        assertThat(connector.getObject("irgendwas"), is((Object) "Something to test"));

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
