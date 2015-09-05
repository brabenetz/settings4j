/*
 * #%L
 * settings4j
 * ===============================================================
 * Copyright (C) 2008 - 2015 Brabenetz Harald, Austria
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
package org.settings4j.settings;

import org.junit.Assert;
import org.junit.Test;

/**
 * TestCases for {@link DefaultFilter}.
 */
public class DefaultFilterTest {

    @Test
    public void testDefaultFilterInclude() {
        final DefaultFilter defaultFilter = new DefaultFilter();
        defaultFilter.addInclude("org/settings4j/config/testConfigFilter1\\.txt");

        Assert.assertTrue(defaultFilter.isValid("org/settings4j/config/testConfigFilter1.txt"));
        Assert.assertFalse(defaultFilter.isValid("xyz"));
    }

    @Test
    public void testDefaultFilterExclude() {
        final DefaultFilter defaultFilter = new DefaultFilter();
        defaultFilter.addExclude("org/settings4j/config/testConfigFilter1\\.txt");

        Assert.assertFalse(defaultFilter.isValid("org/settings4j/config/testConfigFilter1.txt"));
        Assert.assertTrue(defaultFilter.isValid("xyz"));
    }
}
