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
package org.settings4j;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.settings4j.connector.ClasspathConnector;
import org.settings4j.connector.JNDIConnector;
import org.settings4j.connector.SystemPropertyConnector;

public class ConnectorPositionsTest {

    @Test
    public void testBeforeNameSuccess() {
        final List<Connector> connectors = createDummyConnectors();

        final int position = ConnectorPositions.before("sysCon2").getPosition(connectors);
        assertThat(position, is(2));

        // validate if list.add works with this value as expected:
        connectors.add(position, setName(new SystemPropertyConnector(), "myConnector"));
        int i = 0;
        assertThat(connectors.get(i++).getName(), is("sysCon1"));
        assertThat(connectors.get(i++).getName(), is("cpCon1"));
        assertThat(connectors.get(i++).getName(), is("myConnector")); // before "sysCon2"
        assertThat(connectors.get(i++).getName(), is("sysCon2"));
        assertThat(connectors.get(i++).getName(), is("cpCon2"));

    }

    @Test
    public void testBeforeNameFail() {
        final List<Connector> connectors = createDummyConnectors();

        assertThat(ConnectorPositions.before("xyz").getPosition(connectors), is(ConnectorPosition.UNKNOWN_POSITION));
    }

    @Test
    public void testAfterNameSuccess() {
        final List<Connector> connectors = createDummyConnectors();

        final int position = ConnectorPositions.after("sysCon2").getPosition(connectors);
        assertThat(position, is(3));

        // validate if list.add works with this value as expected:
        connectors.add(position, setName(new SystemPropertyConnector(), "myConnector"));
        int i = 0;
        assertThat(connectors.get(i++).getName(), is("sysCon1"));
        assertThat(connectors.get(i++).getName(), is("cpCon1"));
        assertThat(connectors.get(i++).getName(), is("sysCon2"));
        assertThat(connectors.get(i++).getName(), is("myConnector")); // after "sysCon2"
        assertThat(connectors.get(i++).getName(), is("cpCon2"));

    }

    @Test
    public void testAfterNameFail() {
        final List<Connector> connectors = createDummyConnectors();

        assertThat(ConnectorPositions.after("xyz").getPosition(connectors), is(ConnectorPosition.UNKNOWN_POSITION));
    }

    @Test
    public void testAfterFirstClassSuccess() {
        final List<Connector> connectors = createDummyConnectors();

        final int position = ConnectorPositions.afterFirst(ClasspathConnector.class).getPosition(connectors);
        assertThat(position, is(2));

        // validate if list.add works with this value as expected:
        connectors.add(position, setName(new SystemPropertyConnector(), "myConnector"));
        int i = 0;
        assertThat(connectors.get(i++).getName(), is("sysCon1"));
        assertThat(connectors.get(i++).getName(), is("cpCon1"));
        assertThat(connectors.get(i++).getName(), is("myConnector")); // after "cpCon1"
        assertThat(connectors.get(i++).getName(), is("sysCon2"));
        assertThat(connectors.get(i++).getName(), is("cpCon2"));
    }

    @Test
    public void testAfterFirstClassFail() {
        final List<Connector> connectors = createDummyConnectors();

        final int position = ConnectorPositions.afterFirst(JNDIConnector.class).getPosition(connectors);
        assertThat(position, is(ConnectorPosition.UNKNOWN_POSITION));
    }

    @Test
    public void testAfterLastClassSuccess() {
        final List<Connector> connectors = createDummyConnectors();

        final int position = ConnectorPositions.afterLast(SystemPropertyConnector.class).getPosition(connectors);
        assertThat(position, is(3));

        // validate if list.add works with this value as expected:
        connectors.add(position, setName(new SystemPropertyConnector(), "myConnector"));
        int i = 0;
        assertThat(connectors.get(i++).getName(), is("sysCon1"));
        assertThat(connectors.get(i++).getName(), is("cpCon1"));
        assertThat(connectors.get(i++).getName(), is("sysCon2"));
        assertThat(connectors.get(i++).getName(), is("myConnector")); // after "sysCon2"
        assertThat(connectors.get(i++).getName(), is("cpCon2"));
    }

    @Test
    public void testAfterLastClassFail() {
        final List<Connector> connectors = createDummyConnectors();

        final int position = ConnectorPositions.afterLast(JNDIConnector.class).getPosition(connectors);
        assertThat(position, is(ConnectorPosition.UNKNOWN_POSITION));
    }


    @Test
    public void testBeforeFirstClassSuccess() {
        final List<Connector> connectors = createDummyConnectors();

        final int position = ConnectorPositions.beforeFirst(SystemPropertyConnector.class).getPosition(connectors);
        assertThat(position, is(0));

        // validate if list.add works with this value as expected:
        connectors.add(position, setName(new SystemPropertyConnector(), "myConnector"));
        int i = 0;
        assertThat(connectors.get(i++).getName(), is("myConnector")); // Before "sysCon1"
        assertThat(connectors.get(i++).getName(), is("sysCon1"));
        assertThat(connectors.get(i++).getName(), is("cpCon1"));
        assertThat(connectors.get(i++).getName(), is("sysCon2"));
        assertThat(connectors.get(i++).getName(), is("cpCon2"));
    }

    @Test
    public void testBeforeFirstClassFail() {
        final List<Connector> connectors = createDummyConnectors();

        final int position = ConnectorPositions.beforeFirst(JNDIConnector.class).getPosition(connectors);
        assertThat(position, is(ConnectorPosition.UNKNOWN_POSITION));
    }

    @Test
    public void testBeforeLastClassSuccess() {
        final List<Connector> connectors = createDummyConnectors();

        final int position = ConnectorPositions.beforeLast(SystemPropertyConnector.class).getPosition(connectors);
        assertThat(position, is(2));

        // validate if list.add works with this value as expected:
        connectors.add(position, setName(new SystemPropertyConnector(), "myConnector"));
        int i = 0;
        assertThat(connectors.get(i++).getName(), is("sysCon1"));
        assertThat(connectors.get(i++).getName(), is("cpCon1"));
        assertThat(connectors.get(i++).getName(), is("myConnector")); // Before "sysCon2"
        assertThat(connectors.get(i++).getName(), is("sysCon2"));
        assertThat(connectors.get(i++).getName(), is("cpCon2"));
    }

    @Test
    public void testBeforeLastClassFail() {
        final List<Connector> connectors = createDummyConnectors();

        final int position = ConnectorPositions.beforeLast(JNDIConnector.class).getPosition(connectors);
        assertThat(position, is(ConnectorPosition.UNKNOWN_POSITION));
    }


    @Test
    public void testFirstValidSuccessFirst() {
        final List<Connector> connectors = createDummyConnectors();

        final int position = ConnectorPositions.firstValid(//
            ConnectorPositions.after("cpCon1"), //
            ConnectorPositions.beforeFirst(ClasspathConnector.class)//
            ).getPosition(connectors);
        assertThat(position, is(2));

        // validate if list.add works with this value as expected:
        connectors.add(position, setName(new SystemPropertyConnector(), "myConnector"));
        int i = 0;
        assertThat(connectors.get(i++).getName(), is("sysCon1"));
        assertThat(connectors.get(i++).getName(), is("cpCon1"));
        assertThat(connectors.get(i++).getName(), is("myConnector")); // After "cpCon1"
        assertThat(connectors.get(i++).getName(), is("sysCon2"));
        assertThat(connectors.get(i++).getName(), is("cpCon2"));
    }
    
    @Test
    public void testFirstValidSuccessSecond() {
        final List<Connector> connectors = createDummyConnectors();

        final int position = ConnectorPositions.firstValid(//
            ConnectorPositions.after("xyz"), //
            ConnectorPositions.beforeFirst(ClasspathConnector.class)//
            ).getPosition(connectors);
        assertThat(position, is(1));

        // validate if list.add works with this value as expected:
        connectors.add(position, setName(new SystemPropertyConnector(), "myConnector"));
        int i = 0;
        assertThat(connectors.get(i++).getName(), is("sysCon1"));
        assertThat(connectors.get(i++).getName(), is("myConnector")); // Before "cpCon1"
        assertThat(connectors.get(i++).getName(), is("cpCon1"));
        assertThat(connectors.get(i++).getName(), is("sysCon2"));
        assertThat(connectors.get(i++).getName(), is("cpCon2"));
    }


    @Test
    public void testFirstValidFail() {
        final List<Connector> connectors = createDummyConnectors();

        final int position = ConnectorPositions.firstValid(//
            ConnectorPositions.after("xyz"), //
            ConnectorPositions.beforeFirst(JNDIConnector.class)//
            ).getPosition(connectors);
        assertThat(position, is(ConnectorPosition.UNKNOWN_POSITION));
    }


    @Test
    public void testAtLast() {
        final List<Connector> connectors = createDummyConnectors();

        final int position = ConnectorPositions.atLast().getPosition(connectors);
        assertThat(position, is(connectors.size()));

        // validate if list.add works with this value as expected:
        connectors.add(position, setName(new SystemPropertyConnector(), "myConnector"));
        int i = 0;
        assertThat(connectors.get(i++).getName(), is("sysCon1"));
        assertThat(connectors.get(i++).getName(), is("cpCon1"));
        assertThat(connectors.get(i++).getName(), is("sysCon2"));
        assertThat(connectors.get(i++).getName(), is("cpCon2"));
        assertThat(connectors.get(i++).getName(), is("myConnector")); // at last
    }

    @Test
    public void testAtFirst() {
        final List<Connector> connectors = createDummyConnectors();

        final int position = ConnectorPositions.atFirst().getPosition(connectors);
        assertThat(position, is(0));

        // validate if list.add works with this value as expected:
        connectors.add(position, setName(new SystemPropertyConnector(), "myConnector"));
        int i = 0;
        assertThat(connectors.get(i++).getName(), is("myConnector")); // at first
        assertThat(connectors.get(i++).getName(), is("sysCon1"));
        assertThat(connectors.get(i++).getName(), is("cpCon1"));
        assertThat(connectors.get(i++).getName(), is("sysCon2"));
        assertThat(connectors.get(i++).getName(), is("cpCon2"));
    }

    private List<Connector> createDummyConnectors() {
        final List<Connector> connectors = new ArrayList<Connector>();
        connectors.add(setName(new SystemPropertyConnector(), "sysCon1"));
        connectors.add(setName(new ClasspathConnector(), "cpCon1"));
        connectors.add(setName(new SystemPropertyConnector(), "sysCon2"));
        connectors.add(setName(new ClasspathConnector(), "cpCon2"));
        return connectors;
    }

    private Connector setName(final Connector connector, final String name) {
        connector.setName(name);
        return connector;
    }
}
