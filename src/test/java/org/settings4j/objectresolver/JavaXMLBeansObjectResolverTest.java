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

package org.settings4j.objectresolver;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.settings4j.ContentResolver;
import org.settings4j.contentresolver.ClasspathContentResolver;
import org.settings4j.contentresolver.FSContentResolver;
import org.settings4j.contentresolver.UnionContentResolver;

import junit.framework.TestCase;

public class JavaXMLBeansObjectResolverTest extends TestCase {

    private File testDir;
    
    protected void setUp() throws Exception {
        super.setUp();
        FileUtils.deleteDirectory(new File("test"));
        testDir = (new File("test/JavaXMLBeans/".toLowerCase())).getAbsoluteFile();
        FileUtils.forceMkdir(testDir);
    }
    
    protected void tearDown() throws Exception {
        FileUtils.deleteDirectory(new File("test"));
        super.tearDown();
    }
    
    public void test1(){
        JavaXMLBeansObjectResolver objectResolver = new JavaXMLBeansObjectResolver();
        
        // FileSystem is Writeable => the XML-Object
        // Classpath is readonly => the XML-Object-Properties
        FSContentResolver fsContentResolver = new FSContentResolver();
        fsContentResolver.setRootFolderPath(testDir.getAbsolutePath());
        ContentResolver contentResolver = new UnionContentResolver(fsContentResolver);
        contentResolver.addContentResolver(new ClasspathContentResolver());
        
        Map testData =  new HashMap();
        List testList = new ArrayList();
        String testValue1 = "testValue1";
        String testValue2 = "testValue2";
        String testValue3 = "testValue3";
        String testValue4 = "testValue4";
        testList.add(testValue1);
        testList.add(testValue2);
        testList.add(testValue3);
        testList.add(testValue4);
        testData.put("irgendwas", "blablablablablabla");
        testData.put("liste", testList);
        
        objectResolver.setObject("org/settings4j/objectresolver/test1", contentResolver, testData);
        
        Map result = (Map) objectResolver.getObject("org/settings4j/objectresolver/test1", contentResolver);
        assertEquals("blablablablablabla", result.get("irgendwas"));
        Object liste = result.get("liste");
        assertNotNull(liste);
        assertTrue(liste instanceof List);
        assertEquals(4, ((List)liste).size());
        
        Map result2 = (Map) objectResolver.getObject("org/settings4j/objectresolver/test1", contentResolver);
        // no caching by default => difference Objects but same content.
        assertTrue(result != result2);
        assertEquals(result2.get("irgendwas"), result.get("irgendwas"));
        
        
    }
    
    /**
     * cached by propertyfile "org/settings4j/objectresolver/test2.properties"
     */
    public void test2Caching(){
        String key = "org/settings4j/objectresolver/test2";
        
        JavaXMLBeansObjectResolver objectResolver = new JavaXMLBeansObjectResolver();

        FSContentResolver fsContentResolver = new FSContentResolver();
        fsContentResolver.setRootFolderPath(testDir.getAbsolutePath());
        ContentResolver contentResolver = new UnionContentResolver(fsContentResolver);
        contentResolver.addContentResolver(new ClasspathContentResolver());
        
        cachingTest(key, objectResolver, contentResolver);
        
    }
    
    /**
     * cached by {@link AbstractObjectResolver#setCached(boolean)}
     */
    public void test3Caching(){
        String key = "org/settings4j/objectresolver/test3";
        
        JavaXMLBeansObjectResolver objectResolver = new JavaXMLBeansObjectResolver();
        objectResolver.setCached(true);
        
        FSContentResolver fsContentResolver = new FSContentResolver();
        fsContentResolver.setRootFolderPath(testDir.getAbsolutePath());
        ContentResolver contentResolver = new UnionContentResolver(fsContentResolver);
        contentResolver.addContentResolver(new ClasspathContentResolver());
        
        cachingTest(key, objectResolver, contentResolver);
        
        // The propertyfile "test4.properties" declare explicitly cached=false
        key = "org/settings4j/objectresolver/test4";
        Map result = (Map) objectResolver.getObject(key, contentResolver);
        assertEquals("blablablaNEU4blablabla", result.get("irgendwasNeues"));
        Map result2 = (Map) objectResolver.getObject(key, contentResolver);
        // this Object is explicit NOT cached! The two Objects must be different.
        assertTrue(result != result2);

    
    }

    private void cachingTest(String key, JavaXMLBeansObjectResolver objectResolver, ContentResolver contentResolver) {
        // read from classpath
        Map result = (Map) objectResolver.getObject(key, contentResolver);
        assertEquals("blablablaNEUblablabla", result.get("irgendwasNeues"));
        Object liste = result.get("liste");
        assertNotNull(liste);
        assertTrue(liste instanceof List);
        assertEquals(1, ((List)liste).size());
        assertEquals("testValue1", ((List)liste).get(0));
        
        Map result2 = (Map) objectResolver.getObject(key, contentResolver);
        // this Object is cached! The two Objects must be the same.
        assertTrue(result == result2);
        assertEquals(result2.get("irgendwasNeues"), result.get("irgendwasNeues"));
        
        Map testData =  new HashMap();
        List testList = new ArrayList();
        String testValue1 = "testValue1";
        String testValue2 = "testValue2";
        String testValue3 = "testValue3";
        String testValue4 = "testValue4";
        testList.add(testValue1);
        testList.add(testValue2);
        testList.add(testValue3);
        testList.add(testValue4);
        testData.put("irgendwas", "blablablablablabla");
        testData.put("liste", testList);

        // copy properties for a Temp-Key. This is required for parsing (read/write) of Objects
        byte[] content = contentResolver.getContent(key + ".properties");
        contentResolver.setContent(key + "temp.properties", content);
        
        // save Object to a Temp-Key
        objectResolver.setObject(key + "temp", contentResolver, testData);
        
        // Copy the writen Object without Objectreolver from Temp-Key to real Key
        content = contentResolver.getContent(key + "temp");
        contentResolver.setContent(key, content);
        
        
        // this Object is cached, and the ObjectResolver doesn't know that the content was changed from contentResolver!
        // The OLD Object must be returned.
        result2 = (Map) objectResolver.getObject(key, contentResolver);
        assertTrue(result2 == result);
        assertEquals(result2.get("irgendwasNeues"), result.get("irgendwasNeues"));
        
        // clear cache:
        // This is the work of ContentHasChangedNotifierConnectorWrapper in real live
        objectResolver.notifyContentHasChanged(key);
        
        // Now the right Object must be returned.
        result2 = (Map) objectResolver.getObject(key, contentResolver);
        assertTrue(result2 != result);
        assertNull(result2.get("irgendwasNeues"));
        assertEquals("blablablablablabla", result2.get("irgendwas"));
        liste = result2.get("liste");
        assertNotNull(liste);
        assertTrue(liste instanceof List);
        assertEquals(4, ((List)liste).size());
    }
}
