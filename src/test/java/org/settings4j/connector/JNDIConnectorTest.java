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

    private String charset = "UTF-8";
    private File testDir;
    
    protected void setUp() throws Exception {
        super.setUp();
        testDir = (new File("test/ConnectorTest/".toLowerCase()));
        FileUtils.forceMkdir(testDir);
    }
    
    protected void tearDown() throws Exception {
        FileUtils.deleteDirectory(new File("test"));
        System.getProperties().remove(Context.INITIAL_CONTEXT_FACTORY);
        System.getProperties().remove(Context.PROVIDER_URL);
        System.getProperties().remove(Context.URL_PKG_PREFIXES);
        super.tearDown();
    }


    public void testJNDIConnectorWithoutJNDI() throws Exception {
        //Clear JNDI Configs
        System.getProperties().remove(Context.INITIAL_CONTEXT_FACTORY);
        System.getProperties().remove(Context.PROVIDER_URL);
        System.getProperties().remove(Context.URL_PKG_PREFIXES);
        
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
        
        // No Exception if JNDIContext not available
        saveStatus = connector.setObject("helloWorldPath", testDir.getAbsolutePath() + "/test.txt");
        resultString = connector.getString("helloWorldPath");
        assertNull(resultString);
        
        // No Exception if JNDIContext not available
        saveStatus = connector.setObject("helloWorldPath", testDir.getAbsolutePath() + "/test.txt");
        assertEquals(Constants.SETTING_NOT_POSSIBLE, saveStatus);
        
        // No Exception if JNDIContext not available
        saveStatus = connector.setObject("helloWorldPath", testDir.getAbsolutePath() + "/test.txt");
        assertEquals(Constants.SETTING_NOT_POSSIBLE, saveStatus);
        
        
        
        
        
        // add contentResolver
        ContentResolver contentResolver = new UnionContentResolver();
        FSContentResolver fsContentResolver = new FSContentResolver();
        fsContentResolver.setRootFolderPath(testDir.getAbsolutePath());
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
        //Set JNDI Tomcat Configs
        setTomcatJNDIContextProperties();
        
        final String testTextPath = testDir.getAbsolutePath() + "/test.txt";
        
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
        saveStatus = connector.setObject("helloWorldPath", "Hello World".getBytes(charset));
        assertEquals(Constants.SETTING_SUCCESS, saveStatus);
        // The String-Value cannot read a byte[]
        resultString = connector.getString("helloWorldPath");
        assertNull(resultString);
        // The Content-Value should be the same
        resultContent = connector.getContent("helloWorldPath");
        assertNotNull(resultContent);
        assertEquals("Hello World", new String(resultContent, charset));
        
        
        
        // No Exception if JNDIContext not available
        saveStatus = connector.setObject("helloWorldPath", testTextPath);
        assertEquals(Constants.SETTING_SUCCESS, saveStatus);
        resultString = connector.getString("helloWorldPath");
        assertEquals(testTextPath, resultString);
        resultObject = connector.getObject("helloWorldPath");
        assertEquals(testTextPath, resultObject);

        // if no ObjectResolver is available. the value will be stored directly into the JNDI-Context
        ContentResolver contentResolver = new UnionContentResolver();
        FSContentResolver fsContentResolver = new FSContentResolver();
        fsContentResolver.setRootFolderPath(testDir.getAbsolutePath()); //should also work with temp-folder
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
        assertEquals("Hello World FileContent", new String(resultContent, charset));
        
        
    }

    public static void setTomcatJNDIContextProperties() {
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
        System.setProperty(Context.PROVIDER_URL, "localhost:1099");
        System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");

    }
}
