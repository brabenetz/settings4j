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

import org.settings4j.ContentResolver;

import junit.framework.TestCase;

public class ClasspathContentResolverTest extends TestCase {
    public void testReadHelloWorldTxt() throws Exception {
        ContentResolver contentResolver = new ClasspathContentResolver();
        byte[] content = contentResolver.getContent("org/settings4j/contentresolver/HelloWorld.txt");
        assertNotNull(content);
        assertEquals("Hello World", new String(content, "UTF-8"));
        
        content = contentResolver.getContent("classpath:org/settings4j/contentresolver/HelloWorld.txt");
        assertNotNull(content);
        assertEquals("Hello World", new String(content, "UTF-8"));
        
        content = contentResolver.getContent("classpath:/org/settings4j/contentresolver/HelloWorld.txt");
        assertNotNull(content);
        assertEquals("Hello World", new String(content, "UTF-8"));
        

        content = contentResolver.getContent("classpath:laksjdhalksdhfa");
        assertNull(content);

        content = contentResolver.getContent("/org/settings4j/contentresolver/HelloWorld.txt");
        assertNotNull(content);
        assertEquals("Hello World", new String(content, "UTF-8"));
    }
}
