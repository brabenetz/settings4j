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

package org.settings4j.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.map.LazyMap;

import junit.framework.TestCase;

/**
 * Test ExpressionLanguage Syntax
 */
public class TestExpressionLanguageUtil extends TestCase{
    
    public void testSimple() throws Exception {
        // the Map with all params 
        //Map<String, Object> context = new java.util.HashMap<String, Object>();
        Map context = new java.util.HashMap();

        String result;
        
        
        // First test without Params. Result: "Hallo "
        result = ExpressionLanguageUtil.evaluateExpressionLanguage("Hallo ${name}", context);
        assertEquals(result, "Hallo ");
        
        // Second test with Param (key="name", value="Harry"). Result: "Hallo Harry"
        context.put("name", "Harry");
        result = ExpressionLanguageUtil.evaluateExpressionLanguage("Hallo ${name}", context);
        assertEquals(result, "Hallo Harry");
        
    }
    
    public void testComplex() throws Exception {
        // the Map with all params 
        // Map<String, Object> context = new java.util.HashMap<String, Object>();
        Map context = new java.util.HashMap();

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
        
        assertEquals(result, complexTestResult.toString());
        
    }

    public void testLazyMapMatchPattern() throws Exception {
        
        // the Map with all params 
        // Map<String, Object> context = new java.util.HashMap<String, Object>();
        Map context = new java.util.HashMap();
        

        /*
         * A complex Test to parse a LazyMap and access the dynamic generated values
         */
        context.put("test", LazyMap.decorate(new HashMap(), new MatchPatternTransformer("testString")));
        
        assertEquals(((Boolean)ExpressionLanguageUtil.evaluateExpressionLanguage("${test['testString']}", context, Boolean.class)).booleanValue(),  true);
        assertEquals(((Boolean)ExpressionLanguageUtil.evaluateExpressionLanguage("${test['teststring']}", context, Boolean.class)).booleanValue(),  false);
        assertEquals(((Boolean)ExpressionLanguageUtil.evaluateExpressionLanguage("${test['tesString']}", context, Boolean.class)).booleanValue(),   false);
        assertEquals(((Boolean)ExpressionLanguageUtil.evaluateExpressionLanguage("${test['.*String']}", context, Boolean.class)).booleanValue(),    true);
        assertEquals(((Boolean)ExpressionLanguageUtil.evaluateExpressionLanguage("${test['.*string']}", context, Boolean.class)).booleanValue(),    false);
        assertEquals(((Boolean)ExpressionLanguageUtil.evaluateExpressionLanguage("${test['test.*']}", context, Boolean.class)).booleanValue(),      true);
        assertEquals(((Boolean)ExpressionLanguageUtil.evaluateExpressionLanguage("${test['askjlf.*']}", context, Boolean.class)).booleanValue(),    false);
        assertEquals(((Boolean)ExpressionLanguageUtil.evaluateExpressionLanguage("${test['invalid Expresion: \\\\']}", context, Boolean.class)).booleanValue(),    false);
        
        
    }
    
    /**
     * Example Object for the testComplex()-Function
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
