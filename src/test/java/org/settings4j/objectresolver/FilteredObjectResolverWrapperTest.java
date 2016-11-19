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
package org.settings4j.objectresolver;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.settings4j.Filter;
import org.settings4j.ObjectResolver;
import org.settings4j.contentresolver.ClasspathContentResolver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@RunWith(MockitoJUnitRunner.class)
public class FilteredObjectResolverWrapperTest {

    private static final Object TEST_VALUE_OBJECT = "Test-Value";

    private static final String TEST_KEY = "TEST";

    @InjectMocks
    private FilteredObjectResolverWrapper filteredObjectResolverWrapper;

    @Mock
    private ObjectResolver targetObjectResolver;

    @Mock
    private Filter filter;

    @Test
    public void testGetObject() {
        // prepare
        ClasspathContentResolver contentResolver = new ClasspathContentResolver();
        Mockito.when(this.filter.isValid(TEST_KEY)).thenReturn(true);
        Mockito.when(this.targetObjectResolver.getObject(TEST_KEY, contentResolver)).thenReturn(TEST_VALUE_OBJECT);

        // test
        assertThat(this.filteredObjectResolverWrapper.getObject(TEST_KEY, contentResolver), is(TEST_VALUE_OBJECT));

        // validate
        Mockito.verify(this.targetObjectResolver).getObject(TEST_KEY, contentResolver);
    }

    @Test
    public void testGetObjectFiltered() {
        // prepare
        ClasspathContentResolver contentResolver = new ClasspathContentResolver();
        Mockito.when(this.filter.isValid(TEST_KEY)).thenReturn(false);

        // test
        assertThat(this.filteredObjectResolverWrapper.getObject(TEST_KEY, contentResolver), is(nullValue()));

        // validate
        Mockito.verifyZeroInteractions(this.targetObjectResolver);
    }

    @Test
    public void testSetObjectResolver() {
        // prepare
        JavaXMLBeansObjectResolver objectResolver = new JavaXMLBeansObjectResolver();

        // test
        this.filteredObjectResolverWrapper.addObjectResolver(objectResolver);

        // validate
        Mockito.verify(this.targetObjectResolver).addObjectResolver(objectResolver);
    }

}
