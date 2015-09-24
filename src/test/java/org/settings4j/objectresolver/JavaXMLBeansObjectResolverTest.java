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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.beans.ExceptionListener;
import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.settings4j.ContentResolver;
import org.settings4j.contentresolver.ClasspathContentResolver;
import org.settings4j.contentresolver.FSContentResolver;
import org.settings4j.contentresolver.UnionContentResolver;

public class JavaXMLBeansObjectResolverTest {

    /** General Logger for this Class. */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(JavaXMLBeansObjectResolverTest.class);

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
    public void test1() throws Exception {
        final JavaXMLBeansObjectResolver objectResolver = new JavaXMLBeansObjectResolver();

        final ContentResolver contentResolver = createUnionContentResolver();

        final Map<String, Object> testData = new HashMap<String, Object>();
        final List<String>  testList = new ArrayList<String>();
        final String testValue1 = "testValue1";
        final String testValue2 = "testValue2";
        final String testValue3 = "testValue3";
        final String testValue4 = "testValue4";
        testList.add(testValue1);
        testList.add(testValue2);
        testList.add(testValue3);
        testList.add(testValue4);
        testData.put("irgendwas", "blablablablablabla");
        testData.put("liste", testList);

        final String key = "org/settings4j/objectresolver/test1";

        setObject(key, testData);

        @SuppressWarnings("unchecked")
        final Map<String, Object> result = (Map<String, Object>) objectResolver.getObject("org/settings4j/objectresolver/test1", contentResolver);
        assertThat(result.get("irgendwas"), is((Object) "blablablablablabla"));
        final Object liste = result.get("liste");
        assertThat(liste, is(notNullValue()));
        Assert.assertTrue(liste instanceof List);
        assertThat(((List<?>) liste), hasSize(4));

        @SuppressWarnings("unchecked")
        final Map<String, Object> result2 = (Map<String, Object>) objectResolver.getObject("org/settings4j/objectresolver/test1", contentResolver);
        // no caching by default => difference Objects but same content.
        Assert.assertTrue(result != result2);
        assertThat(result.get("irgendwas"), is(result2.get("irgendwas")));


    }

    /**
     * cached by propertyfile "org/settings4j/objectresolver/test2.properties".
     *
     * @throws Exception if an error occurs.
     */
    @Test
    public void test2Caching() throws Exception {
        final String key = "org/settings4j/objectresolver/test2";

        final JavaXMLBeansObjectResolver objectResolver = new JavaXMLBeansObjectResolver();

        cachingTest(key, objectResolver);

    }

    /**
     * cached by {@link AbstractObjectResolver#setCached(boolean)}.
     *
     * @throws Exception if an error occurs.
     */
    @Test
    public void test3Caching() throws Exception {
        String key = "org/settings4j/objectresolver/test3";

        final JavaXMLBeansObjectResolver objectResolver = new JavaXMLBeansObjectResolver();
        objectResolver.setCached(true);

        final ContentResolver contentResolver = createUnionContentResolver();

        cachingTest(key, objectResolver);

        // The propertyfile "test4.properties" declare explicitly cached=false
        key = "org/settings4j/objectresolver/test4";
        @SuppressWarnings("unchecked")
        final Map<String, String> result = (Map<String, String>) objectResolver.getObject(key, contentResolver);
        assertThat(result.get("irgendwasNeues"), is("blablablaNEU4blablabla"));
        final Map<?, ?> result2 = (Map<?, ?>) objectResolver.getObject(key, contentResolver);
        // this Object is explicit NOT cached! The two Objects must be different.
        Assert.assertTrue(result != result2);


    }

    private void cachingTest(final String key, final JavaXMLBeansObjectResolver objectResolver) throws Exception {
        final ContentResolver contentResolver = createUnionContentResolver();

        // read from classpath
        @SuppressWarnings("unchecked")
        final Map<String, Object> result = (Map<String, Object>) objectResolver.getObject(key, contentResolver);
        assertThat(result.get("irgendwasNeues"), is((Object) "blablablaNEUblablabla"));
        @SuppressWarnings("unchecked")
        final List<String> liste = (List<String>) result.get("liste");
        assertThat(liste, is(notNullValue()));
        assertThat(liste, hasSize(1));
        assertThat(liste.get(0), is("testValue1"));

        @SuppressWarnings("unchecked")
        final
        Map<String, Object> result2 = (Map<String, Object>) objectResolver.getObject(key, contentResolver);
        // this Object is cached! The two Objects must be the same.
        Assert.assertTrue(result == result2);
        assertThat(result.get("irgendwasNeues"), is(result2.get("irgendwasNeues")));

        final Map<String, Object> testData = new HashMap<String, Object>();
        final List<String> testList = new ArrayList<String>();
        final String testValue1 = "testValue1";
        final String testValue2 = "testValue2";
        final String testValue3 = "testValue3";
        final String testValue4 = "testValue4";
        testList.add(testValue1);
        testList.add(testValue2);
        testList.add(testValue3);
        testList.add(testValue4);
        testData.put("irgendwas", "blablablablablabla");
        testData.put("liste", testList);

        // copy properties for a Temp-Key. This is required for parsing (read/write) of Objects
        byte[] content = contentResolver.getContent(key + ".properties");
        setContent(key + "temp.properties", content);

        // save Object to a Temp-Key
        setObject(key + "temp", testData);

        // Copy the writen Object without Objectreolver from Temp-Key to real Key
        content = contentResolver.getContent(key + "temp");
        setContent(key, content);


        // this Object is cached, and the ObjectResolver doesn't know that the content was changed from contentResolver!
        // The OLD Object must be returned.
        @SuppressWarnings("unchecked")
        final
        Map<String, Object> result3 = (Map<String, Object>) objectResolver.getObject(key, contentResolver);
        Assert.assertTrue(result3 == result);
        assertThat(result.get("irgendwasNeues"), is(result3.get("irgendwasNeues")));

    }

    private ContentResolver createUnionContentResolver() {
        final ContentResolver contentResolver = new UnionContentResolver(createFsContentResolver());
        contentResolver.addContentResolver(new ClasspathContentResolver());
        return contentResolver;
    }

    private FSContentResolver createFsContentResolver() {
        final FSContentResolver fsContentResolver = new FSContentResolver();
        fsContentResolver.setRootFolderPath(this.testDir.getAbsolutePath());
        return fsContentResolver;
    }


    private void setObject(final String key, final Map<?, ?> testData) throws IOException {
        final byte[] content = objectToContent(testData);
        setContent(key, content);
    }

    private void setContent(final String key, final byte[] content) throws IOException {
        final File file = new File(this.testDir, key);
        FileUtils.writeByteArrayToFile(file, content);
    }

    private byte[] objectToContent(final Object value) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        final XMLEncoder encoder = new XMLEncoder(byteArrayOutputStream);
        encoder.setExceptionListener(new LogEncoderExceptionListener(value));

        LOG.debug("START Writing Object {} with XMLEncoder", value.getClass().getName());
        encoder.writeObject(value);
        LOG.debug("FINISH Writing Object {} with XMLEncoder", value.getClass().getName());

        encoder.flush();
        encoder.close();
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Log out Exception during Encoding a byte[] to an Object<br>
     * <br>
     * Example:<br>
     * The {@link org.springframework.jdbc.datasource.AbstractDataSource} Object<br>
     * hast Getter and Setter for "logWriter" who throws per default an {@link UnsupportedOperationException}.<br>
     *
     * @author hbrabenetz
     */
    private class LogEncoderExceptionListener implements ExceptionListener {

        private final Object obj;

        public LogEncoderExceptionListener(final Object obj) {
            super();
            this.obj = obj;
        }

        @Override
        public void exceptionThrown(final Exception e) {
            LOG.warn("Ignore error on encoding Object from type: " + this.obj.getClass().getName() + "! "
                + e.getClass().getName() + ": '" + e.getMessage() + "'. Set Loglevel DEBUG for more informations.");
            LOG.debug(e.getMessage(), e);
        }
    }
}
