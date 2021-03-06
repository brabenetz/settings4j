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
package org.settings4j.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.map.LazyMap;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test ExpressionLanguage Syntax.
 */
public class ExpressionLanguageUtilTest {

    /**
     * TestCase for {@link ExpressionLanguageUtil#evaluateExpressionLanguage(String, Map)} HappyPath.
     *
     * @throws Exception if an error occurs.
     */
    @Test
    public void testSimple() throws Exception {
        // the Map with all params
        Map<String, Object> context = new HashMap<String, Object>();

        String result;

        // First test without Params. Result: "Hallo "
        result = ExpressionLanguageUtil.evaluateExpressionLanguage("Hallo ${name}", context);
        assertThat(result, is("Hallo "));

        // Second test with Param (key="name", value="Harry"). Result: "Hallo Harry"
        context.put("name", "Harry");
        result = ExpressionLanguageUtil.evaluateExpressionLanguage("Hallo ${name}", context);
        assertThat(result, is("Hallo Harry"));

    }

    /**
     * TestCase for {@link ExpressionLanguageUtil#evaluateExpressionLanguage(String, Map)} Complex.
     *
     * @throws Exception if an error occurs.
     */
    @Test
    public void testComplex() throws Exception {
        // the Map with all params
        Map<String, Object> context = new HashMap<String, Object>();

        String result;

        /*
         * A complex Test to parse a Object and access the getter
         */

        StringBuffer complexTest = new StringBuffer();
        complexTest.append("Username: ${user.vorname} ${user.nachname}\n");
        complexTest.append("Adresse: ${user.adresse}\n");
        complexTest.append("EMail: ${user.email}\n");

        // store the User-Object into the Context-Map
        context.put("user", new ExampleUser());

        result = ExpressionLanguageUtil.evaluateExpressionLanguage(complexTest.toString(), context);

        StringBuffer complexTestResult = new StringBuffer();
        complexTestResult.append("Username: Franz Mustermann\n");
        complexTestResult.append("Adresse: Musterstrasse 08/15\n");
        complexTestResult.append("EMail: franz.mustermann@musterserver.net\n");

        assertThat(result, is(complexTestResult.toString()));

    }

    /**
     * TestCase for {@link LazyMap#lazyMap(Map, org.apache.commons.collections4.Factory)}
     * and {@link MatchPatternTransformer}.
     *
     * @throws Exception if an error occurs.
     */
    @Test
    public void testLazyMapMatchPattern() throws Exception {

        // the Map with all params
        Map<String, Object> context = new HashMap<String, Object>();


        /*
         * A complex Test to parse a LazyMap and access the dynamic generated values
         */
        context.put("test", LazyMap.lazyMap(new HashMap<String, Boolean>(), new MatchPatternTransformer("testString")));

        Assert.assertTrue(((Boolean) ExpressionLanguageUtil.evaluateExpressionLanguage(//
            "${test['testString']}", context, Boolean.class)).booleanValue());
        Assert.assertFalse(((Boolean) ExpressionLanguageUtil.evaluateExpressionLanguage(//
            "${test['teststring']}", context, Boolean.class)).booleanValue());
        Assert.assertFalse(((Boolean) ExpressionLanguageUtil.evaluateExpressionLanguage(//
            "${test['tesString']}", context, Boolean.class)).booleanValue());
        Assert.assertTrue(((Boolean) ExpressionLanguageUtil.evaluateExpressionLanguage(//
            "${test['.*String']}", context, Boolean.class)).booleanValue());
        Assert.assertFalse(((Boolean) ExpressionLanguageUtil.evaluateExpressionLanguage(//
            "${test['.*string']}", context, Boolean.class)).booleanValue());
        Assert.assertTrue(((Boolean) ExpressionLanguageUtil.evaluateExpressionLanguage(//
            "${test['test.*']}", context, Boolean.class)).booleanValue());
        Assert.assertFalse(((Boolean) ExpressionLanguageUtil.evaluateExpressionLanguage(//
            "${test['askjlf.*']}", context, Boolean.class)).booleanValue());
        Assert.assertFalse(((Boolean) ExpressionLanguageUtil.evaluateExpressionLanguage(//
            "${test['invalid Expresion: \\\\']}", context, Boolean.class)).booleanValue());


    }

    /**
     * Example Object for the testComplex()-Function.
     *
     * @author Harald.Brabenetz
     *
     */
    public static class ExampleUser {
        public String getAdresse() {
            return "Musterstrasse 08/15";
        }
        public String getEmail() {
            return "franz.mustermann@musterserver.net";
        }
        public String getNachname() {
            return "Mustermann";
        }
        public String getVorname() {
            return "Franz";
        }
    }
}
