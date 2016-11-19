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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.settings4j.ContentResolver;
import org.settings4j.Filter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@RunWith(MockitoJUnitRunner.class)
public class FilteredContentResolverWrapperTest {

    private static final byte[] TEST_VALUE_CONTENT = "Test-Value".getBytes();

    private static final String TEST_KEY = "TEST";

    @InjectMocks
    private FilteredContentResolverWrapper filteredContentResolverWrapper;

    @Mock
    private ContentResolver targetContentResolver;

    @Mock
    private Filter filter;

    @Test
    public void testGetContent() {
        // prepare
        Mockito.when(this.filter.isValid(TEST_KEY)).thenReturn(true);
        Mockito.when(this.targetContentResolver.getContent(TEST_KEY)).thenReturn(TEST_VALUE_CONTENT);

        // test
        assertThat(this.filteredContentResolverWrapper.getContent(TEST_KEY), is(TEST_VALUE_CONTENT));

        // validate
        Mockito.verify(this.targetContentResolver).getContent(TEST_KEY);
    }

    @Test
    public void testGetContentFiltered() {
        // prepare
        Mockito.when(this.filter.isValid(TEST_KEY)).thenReturn(false);

        // test
        assertThat(this.filteredContentResolverWrapper.getContent(TEST_KEY), is(nullValue()));

        // validate
        Mockito.verifyZeroInteractions(this.targetContentResolver);
    }

    @Test
    public void testSetContentResolver() {
        // prepare
        ClasspathContentResolver contentResolver = new ClasspathContentResolver();

        // test
        this.filteredContentResolverWrapper.addContentResolver(contentResolver);

        // validate
        Mockito.verify(this.targetContentResolver).addContentResolver(contentResolver);
    }

}
