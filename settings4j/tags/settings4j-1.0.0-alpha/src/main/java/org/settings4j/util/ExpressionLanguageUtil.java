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

import java.util.Map;

import org.apache.taglibs.standard.lang.jstl.ELEvaluator;
import org.apache.taglibs.standard.lang.jstl.ELException;
import org.apache.taglibs.standard.lang.jstl.VariableResolver;


/**
 * A Simple Variable Resolver for ExpressionLanguage.
 * With the JSTLVariableResolver you need a PageContext-Object with HttpRequest etc....
 * This class is a Simple Variant of the JSTLVariableResolver who accept a Simple Map as PageContext.
 * Example:
 * 1. create a Map with Values(key="name", value="Herr Mustermann")
 * 2. parse the String "Hello, ${name}" with the given Map
 * 3. result: "Hello, Herr Mustermann".
 * 
 * @see org.apache.taglibs.standard.lang.jstl.JSTLVariableResolver
 * 
 * @author Harald.Brabenetz
 *
 */
public class ExpressionLanguageUtil {
    
    /**
     * The VariableResolver for a <code>java.util.Map</code> as Context-Object
     */
    private static final VariableResolver simpleVariableResolver = new VariableResolver(){

        /**
         * Resolves the specified variable within the given context.
         * Returns null if the variable is not found.
         **/
        public Object resolveVariable (String pName,
                     Object simpleMap)
          throws ELException
        {
          Map ctx = (Map) simpleMap;
          return ctx.get(pName);
        }
    };

    private static final ELEvaluator sEvaluator = new ELEvaluator(simpleVariableResolver);

    /**
     * Helper-Funktion to evaluate a Expression to a String
     * 
     * @param expression The Expresion to be evaluated
     * @param context The context where the expression has Access
     * @return The Result as String
     * @throws ELException Throw an Exception if the expression is not valid.
     */
    public static String evaluateExpressionLanguage(String expression, Map context) throws ELException {
        return (String)evaluateExpressionLanguage(expression, context, String.class);
    }
    
    /**
     * Helper-Funktion to evaluate a Expression to a given Class
     * 
     * @param expression The Expresion to be evaluated
     * @param context The context where the expression has Access
     * @param returnType the Class-Object who will be returned
     * @return The Result as the given return-Type
     * @throws ELException Throw an Exception if the expression is not valid.
     */
    public static Object evaluateExpressionLanguage(String expression, Map context, Class returnType) throws ELException {
        return sEvaluator.evaluate(expression, context, returnType, null, null);
    }
    
    
//
//    /**
//     * Simple Test And Example
//     * 
//     * @param args
//     */
//    public static void main(String[] args){
//        // Create the Evaluation-Object with a SimpleVariableResolver.
//        
//        // The String to parse
//        String simpleTest = "Hallo ${name}";
//        
//        StringBuilder complexTest = new StringBuilder();
//        complexTest.append("Username: ${user.vorname} ${user.nachname}\n");
//        complexTest.append("Adresse: ${user.adresse}\n");
//        complexTest.append("EMail: ${user.email}\n");
//        
//        
//        // the Map with all params 
//        Map<String, Object> context = new java.util.HashMap<String, Object>();
//        
//        try {
//            String result;
//            // First test without Params. Result: "Hallo "
//            result = evaluateExpressionLanguage(simpleTest, context);
//            System.out.println(result);
//            System.out.println();
//            
//            // Second test with Param (key="name", value="Harry"). Result: "Hallo Harry"
//            context.put("name", "Harry");
//            result = evaluateExpressionLanguage(simpleTest, context);
//            System.out.println(result);
//            System.out.println();
//            
//            
//            
//            
//            // Third Test with complex Object as Param
//            context.clear();
//            Object exampleUser = new ExampleUser();
//            // store the User-Object into the Context-Map
//            context.put("user", exampleUser);
//
//            result = evaluateExpressionLanguage(complexTest.toString(), context);
//            System.out.println(result);
//            
//            
//        } catch (ELException e) {
//            e.printStackTrace();
//        }
//        
//    }
//    
//    /**
//     * Example Object for the main()-Function
//     * 
//     * @author Harald.Brabenetz
//     *
//     */
//    public static class ExampleUser {
//        public String getAdresse() {
//            return "Musterstrasse 08/15";
//        }
//        public String getEmail() {
//            return "franz.mustermann@musterserver.net";
//        }
//        public String getNachname() {
//            return "Mustermann";
//        }
//        public String getVorname() {
//            return "Franz";
//        }
//    }
}
