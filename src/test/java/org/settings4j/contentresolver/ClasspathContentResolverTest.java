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
package org.settings4j.contentresolver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Test;
import org.settings4j.ContentResolver;

public class ClasspathContentResolverTest {

    @Test
    public void testReadHelloWorldTxt() throws Exception {
        final ContentResolver contentResolver = new ClasspathContentResolver();
        byte[] content = contentResolver.getContent("org/settings4j/contentresolver/HelloWorld.txt");
        assertThat(content, is(notNullValue()));
        assertThat(new String(content, "UTF-8"), is("Hello World"));

        content = contentResolver.getContent("classpath:org/settings4j/contentresolver/HelloWorld.txt");
        assertThat(content, is(notNullValue()));
        assertThat(new String(content, "UTF-8"), is("Hello World"));

        content = contentResolver.getContent("classpath:/org/settings4j/contentresolver/HelloWorld.txt");
        assertThat(content, is(notNullValue()));
        assertThat(new String(content, "UTF-8"), is("Hello World"));


        content = contentResolver.getContent("classpath:laksjdhalksdhfa");
        assertThat(content, is(nullValue()));

        content = contentResolver.getContent("/org/settings4j/contentresolver/HelloWorld.txt");
        assertThat(content, is(notNullValue()));
        assertThat(new String(content, "UTF-8"), is("Hello World"));
    }
}
