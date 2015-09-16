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
package org.settings4j.objectresolver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.settings4j.ContentResolver;
import org.settings4j.contentresolver.ClasspathContentResolver;

public class SpringConfigObjectResolverTest {

    private File testDir;

    @Before
    public void setUp() throws Exception {
        FileUtils.deleteDirectory(new File("test"));
        this.testDir = (new File("test/JavaXMLBeans/".toLowerCase())).getAbsoluteFile();
        FileUtils.forceMkdir(this.testDir);
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(new File("test"));
    }

    @Test
    public void testSpringContext1() throws Exception {
        final SpringConfigObjectResolver objectResolver = new SpringConfigObjectResolver();

        // Classpath is readonly => the XML-Spring-Config and Properties
        final ContentResolver contentResolver = new ClasspathContentResolver();

        final String key = "org/settings4j/objectresolver/testSpring1";

        final byte[] springFileContent = contentResolver.getContent(key);
        assertThat(springFileContent, is(notNullValue()));

        final DataSource hsqlDS = (DataSource) objectResolver.getObject(key, contentResolver);
        assertThat(hsqlDS, is(notNullValue()));

        // test DataSource
        Connection conn = null;
        try {
            conn = hsqlDS.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("create Table test ( name VARCHAR(255) )");
            pstmt.execute();
            pstmt.close();

            pstmt = conn.prepareStatement("insert into test(name) values (?)");
            pstmt.setString(1, "Hello World");
            pstmt.execute();
            pstmt.close();

            pstmt = conn.prepareStatement("select * from test");
            final ResultSet rs = pstmt.executeQuery();
            rs.next();
            final String result = rs.getString(1);
            rs.close();
            pstmt.close();

            pstmt = conn.prepareStatement("drop Table test");
            pstmt.execute();
            pstmt.close();

            assertThat(result, is("Hello World"));

        } finally {
            if (conn != null) {
                conn.close();
            }
        }

    }
}
