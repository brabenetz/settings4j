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
package org.settings4j.connector;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Test;

/**
 * Test Suite for {@link PreferencesConnector}.
 *
 * @author brabenetz
 */
public class EnvironmentConnectorTest {

    @Test
    public void testGetString() {
        final EnvironmentConnector connector = new EnvironmentConnector();

        assertThat(connector.getString("PATH"), containsString("bin"));
    }

    @Test
    public void testGetStringUpperCase() {
        final EnvironmentConnector connector = new EnvironmentConnector();

        assertThat(connector.getString("java-home"), notNullValue());
        assertThat(connector.getString("java.home"), notNullValue());
        assertThat(connector.getString("java/home"), notNullValue());
    }

    @Test
    public void testGetStringFail() {
        final EnvironmentConnector connector = new EnvironmentConnector();

        assertThat(connector.getString("testReadFailDoesntExist"), is(nullValue()));
    }

}
