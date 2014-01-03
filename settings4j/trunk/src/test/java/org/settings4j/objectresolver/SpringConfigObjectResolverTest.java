/* ***************************************************************************
 * Copyright (c) 2008 Brabenetz Harald, Austria.
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

package org.settings4j.objectresolver;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.settings4j.ContentResolver;
import org.settings4j.contentresolver.ClasspathContentResolver;

public class SpringConfigObjectResolverTest extends TestCase {

    private File testDir;

    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();
        FileUtils.deleteDirectory(new File("test"));
        this.testDir = (new File("test/JavaXMLBeans/".toLowerCase())).getAbsoluteFile();
        FileUtils.forceMkdir(this.testDir);
    }

    /** {@inheritDoc} */
    protected void tearDown() throws Exception {
        FileUtils.deleteDirectory(new File("test"));
        super.tearDown();
    }

    public void testSpringContext1() throws Exception {
        final SpringConfigObjectResolver objectResolver = new SpringConfigObjectResolver();

        // Classpath is readonly => the XML-Spring-Config and Properties
        final ContentResolver contentResolver = new ClasspathContentResolver();

        final String key = "org/settings4j/objectresolver/testSpring1";

        final byte[] springFileContent = contentResolver.getContent(key);
        assertNotNull(springFileContent);

        final DataSource hsqlDS = (DataSource) objectResolver.getObject(key, contentResolver);
        assertNotNull(hsqlDS);

        // test DataSource
        Connection conn = null;
        try {
            conn = hsqlDS.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("create Table test ( name VARCHAR )");
            pstmt.execute();
            pstmt.close();

            pstmt = conn.prepareStatement("insert into test(name) values (?)");
            pstmt.setString(1, "Hello World");
            pstmt.execute();
            pstmt.close();

            pstmt = conn.prepareStatement("select * from test");
            final ResultSet rs = pstmt.executeQuery(); // NOPMD must have Result - An exception would break the test.
            rs.next();
            final String result = rs.getString(1);
            rs.close();
            pstmt.close();

            pstmt = conn.prepareStatement("drop Table test");
            pstmt.execute();
            pstmt.close();

            assertEquals("Hello World", result);

        } finally {
            if (conn != null) {
                conn.close();
            }
        }

    }
}
