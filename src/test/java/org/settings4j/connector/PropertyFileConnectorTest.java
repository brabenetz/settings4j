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
package org.settings4j.connector;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PropertyFileConnectorTest {

    @Test
    public void testReadPropertyFromFilePath() throws IOException {
        final PropertyFileConnector connector = new PropertyFileConnector();
        connector.setPropertyFromPath("file:./src/test/resources/org/settings4j/connector/propertyFile.properties");
        connector.setResolveRelativePaths(true);
        assertThat(connector.getString("xyz"), is("Value from Property-File"));
        assertThat(connector.getString("someRelativePath"),
            is(new File("./src/test/resources/org/settings4j/connector/test.xml").getCanonicalFile().toURL().toExternalForm()));
        assertThat(connector.getString("someRelativeSiblingPath"),
            is(new File("./src/test/resources/org/settings4j/test1/test.xml").getCanonicalFile().toURL().toExternalForm()));
        assertThat(connector.getString("someNormalWindowsPath"), is(new URL("file:C:/test.xml").toExternalForm()));
        assertThat(connector.getString("someNormalUnixPath"), is(new URL("file:/test.xml").toExternalForm()));
    }

    @Test
    public void testReadPropertyFromClasspath() throws IOException {
        final PropertyFileConnector connector = new PropertyFileConnector();
        connector.setResolveRelativePaths(true);
        connector.setPropertyFromPath("classpath:/org/settings4j/connector/propertyFile.properties");
        assertThat(connector.getString("xyz"), is("Value from Property-File"));
        assertThat(connector.getString("someRelativePath"),
            is(new File("./target/test-classes/org/settings4j/connector/test.xml").getCanonicalFile().toURL().toExternalForm()));
        assertThat(connector.getString("someRelativeSiblingPath"),
            is(new File("./target/test-classes/org/settings4j/test1/test.xml").getCanonicalFile().toURL().toExternalForm()));
        assertThat(connector.getString("someNormalWindowsPath"), is(new URL("file:C:/test.xml").toExternalForm()));
        assertThat(connector.getString("someNormalUnixPath"), is(new URL("file:/test.xml").toExternalForm()));
    }

    @Test
    public void testReadPropertyFromJarFile() {
        final PropertyFileConnector connector = new PropertyFileConnector();
        connector.setResolveRelativePaths(true);
        connector.setPropertyFromPath("classpath:/META-INF/maven/org.slf4j/slf4j-api/pom.properties");
        assertThat(connector.getString("groupId"), is("org.slf4j"));
        assertThat(connector.getPropertyFileFolderUrl().toExternalForm() + "pom.properties",
            is(Thread.currentThread().getContextClassLoader().getResource("META-INF/maven/org.slf4j/slf4j-api/pom.properties").toExternalForm()));
    }
}
