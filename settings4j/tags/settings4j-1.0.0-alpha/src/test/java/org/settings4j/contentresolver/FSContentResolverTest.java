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

package org.settings4j.contentresolver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import junit.framework.TestCase;

public class FSContentResolverTest extends TestCase {

    /** General Logger for this Class */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(FSContentResolverTest.class);
    
    private static final String BASE_DIR = FSContentResolverTest.class.getPackage().getName().replace('.', '/');

    File testDir;
    
    protected void setUp() throws Exception {
        super.setUp();
        //testDir = (new File("test/" + System.currentTimeMillis() + "-" + (int)(Math.random()*1000) + "/ContentResolverTest/".toLowerCase())).getAbsoluteFile();
        testDir = (new File("test/ContentResolverTest/".toLowerCase())).getAbsoluteFile();
        FileUtils.forceMkdir(testDir);
    }
    
    protected void tearDown() throws Exception {
        FileUtils.deleteDirectory(new File("test"));
        super.tearDown();
    }

    public void testReadHelloWorldTxt() throws Exception {
        // Copy the required Test-file to the Temp-Path
        InputStream helloWorldIS = this.getClass().getClassLoader().getResourceAsStream(BASE_DIR + "/HelloWorld.txt");
        FileUtils.forceMkdir(new File(testDir.getAbsolutePath() + "/org/settings4j/contentresolver"));
        String helloWorldPath = testDir.getAbsolutePath() + "/org/settings4j/contentresolver/HelloWorld.txt";
        FileOutputStream fileOutputStream = new FileOutputStream(new File(helloWorldPath));
        IOUtils.copy(helloWorldIS, fileOutputStream);
        IOUtils.closeQuietly(helloWorldIS);
        IOUtils.closeQuietly(fileOutputStream);
        LOG.info("helloWorldPath: " + helloWorldPath);
        
        FSContentResolver contentResolver = new FSContentResolver();
        contentResolver.setRootFolderPath(testDir.getAbsolutePath());
        
        byte[] content = contentResolver.getContent("org/settings4j/contentresolver/HelloWorld.txt");
        assertNotNull(content);
        assertEquals("Hello World", new String(content, "UTF-8"));
        
        content = contentResolver.getContent("file:org/settings4j/contentresolver/HelloWorld.txt");
        assertNotNull(content);
        assertEquals("Hello World", new String(content, "UTF-8"));
        
        content = contentResolver.getContent("file:/org/settings4j/contentresolver/HelloWorld.txt");
        assertNotNull(content);
        assertEquals("Hello World", new String(content, "UTF-8"));
        

        content = contentResolver.getContent("file:laksjdhalksdhfa");
        assertNull(content);

        content = contentResolver.getContent("/org/settings4j/contentresolver/HelloWorld.txt");
        assertNotNull(content);
        assertEquals("Hello World", new String(content, "UTF-8"));
    }
    
    public void testWriteHelloWorldTxt() throws Exception {
        
        FileUtils.deleteDirectory(new File(testDir.getCanonicalPath() + "/org"));

        InputStream helloWorldIS = this.getClass().getClassLoader().getResourceAsStream(BASE_DIR + "/HelloWorld.txt");
        byte[] value = IOUtils.toByteArray(helloWorldIS);

        FSContentResolver contentResolver = new FSContentResolver();
        contentResolver.setRootFolderPath(testDir.getAbsolutePath());

        byte[] content = contentResolver.getContent("org/settings4j/contentresolver/HelloWorld.txt");
        assertNull(content);
        
        content = contentResolver.getContent("file:org/settings4j/contentresolver/HelloWorld.txt");
        assertNull(content);
        
        // store value;
        contentResolver.setContent("file:org/settings4j/contentresolver/HelloWorld.txt", value);
        
        
        content = contentResolver.getContent("org/settings4j/contentresolver/HelloWorld.txt");
        assertNotNull(content);
        assertEquals("Hello World", new String(content, "UTF-8"));
        
        content = contentResolver.getContent("file:org/settings4j/contentresolver/HelloWorld.txt");
        assertNotNull(content);
        assertEquals("Hello World", new String(content, "UTF-8"));
        

        content = contentResolver.getContent("file:laksjdhalksdhfa");
        assertNull(content);

        content = contentResolver.getContent("/org/settings4j/contentresolver/HelloWorld.txt");
        assertNotNull(content);
        assertEquals("Hello World", new String(content, "UTF-8"));
    }
}
