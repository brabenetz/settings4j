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
        
        objectResolver.setObject("org/settings4j/objectResolver/test1", contentResolver, testData);
        
        Map result = (Map) objectResolver.getObject("org/settings4j/objectResolver/test1", contentResolver);
        assertEquals("blablablablablabla", result.get("irgendwas"));
        Object liste = result.get("liste");
        assertNotNull(liste);
        assertTrue(liste instanceof List);
        assertEquals(4, ((List)liste).size());
        
        Map result2 = (Map) objectResolver.getObject("org/settings4j/objectResolver/test1", contentResolver);
        // no caching by default => difference Objects but same content.
        assertTrue(result != result2);
        assertEquals(result2.get("irgendwas"), result.get("irgendwas"));
        
        
    }
    
    public void test2Caching(){
        JavaXMLBeansObjectResolver objectResolver = new JavaXMLBeansObjectResolver();
        
        ContentResolver contentResolver = new ClasspathContentResolver();
        
        
        Map result = (Map) objectResolver.getObject("org/settings4j/objectResolver/test2", contentResolver);
        assertEquals("blablablaNEUblablabla", result.get("irgendwasNeues"));
        Object liste = result.get("liste");
        assertNotNull(liste);
        assertTrue(liste instanceof List);
        assertEquals(1, ((List)liste).size());
        assertEquals("testValue1", ((List)liste).get(0));
        
        Map result2 = (Map) objectResolver.getObject("org/settings4j/objectResolver/test2", contentResolver);
        // this Object is cached! The two Objects must be the same.
        assertTrue(result == result2);
        assertEquals(result2.get("irgendwasNeues"), result.get("irgendwasNeues"));
        
        
        
    }
}
