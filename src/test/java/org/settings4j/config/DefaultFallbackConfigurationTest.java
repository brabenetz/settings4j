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
package org.settings4j.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.settings4j.Settings4j;
import org.settings4j.test.TestUtils;

public class DefaultFallbackConfigurationTest {

    /** General Logger for this Class. */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
        .getLogger(DefaultFallbackConfigurationTest.class);

    @Before
    public void setUp() throws Exception {
        TestUtils.reconfigureSettings4jWithDefaultConfig();
        System.setProperty("testDefaultProperty", "HelloWorld");
    }

    @After
    public void tearDown() throws Exception {
        System.clearProperty("testDefaultProperty");
    }

    /**
     * Test parsing of defaultsettings4j.xml (FALLBACK-Configuration).
     */
    @Test
    public void testDefaultSettings4jConfig() {

        LOG.debug(//
            "#### AFTER THE FOLLOWING LINE SETTINGS4J WILL BE " //
            + "CONFIGURED THE FIRST TIME WITH THE FALLBACK-FILE #####");
        assertThat(Settings4j.getString("testDefaultProperty"), is("HelloWorld"));
        LOG.debug("#### FINISH CONFIGURATION #####");

        // check if there is no Exception thrown:
        assertThat(Settings4j.getString("xyz"), is(nullValue()));
        assertThat(Settings4j.getContent("xyz"), is(nullValue()));
        assertThat(Settings4j.getObject("xyz"), is(nullValue()));

        // the Default settings Configuration is readonly
        final int expectedConnectors = 5;
        assertThat(Settings4j.getConnectors().size(), is(expectedConnectors));
    }
}
