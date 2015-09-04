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
/* ***************************************************************************
 * Copyright (c) 2011 Brabenetz Harald, Austria.
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
package org.settings4j.connector;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import junit.framework.TestCase;

import org.junit.Test;

/**
 * Test Suite for {@link PreferencesConnector}. Checkstyle:OFF .*
 * 
 * @author brabenetz
 */
public class EnvironmentConnectorTest extends TestCase {

    @Test
    public void testReadSuccess() {
        final EnvironmentConnector connector = new EnvironmentConnector();

        assertThat(connector.getString("PATH"), containsString("bin"));
    }

    @Test
    public void testReadFail() {
        final EnvironmentConnector connector = new EnvironmentConnector();

        assertThat(connector.getString("testReadFailDoesntExist"), is(nullValue()));
    }
}
