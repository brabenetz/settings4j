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
package org.settings4j.util;

import java.util.Map;

import org.apache.taglibs.standard.lang.jstl.ELEvaluator;
import org.apache.taglibs.standard.lang.jstl.ELException;
import org.apache.taglibs.standard.lang.jstl.VariableResolver;


/**
 * A Simple Variable Resolver for ExpressionLanguage. With the JSTLVariableResolver you need a PageContext-Object with
 * HttpRequest etc.... This class is a Simple Variant of the JSTLVariableResolver who accept a Simple Map as
 * PageContext. Example: 1. create a Map with Values(key="name", value="Herr Mustermann") 2. parse the String
 * "Hello, ${name}" with the given Map 3. result: "Hello, Herr Mustermann".
 *
 * @see org.apache.taglibs.standard.lang.jstl.JSTLVariableResolver
 * @author Harald.Brabenetz
 */
public final class ExpressionLanguageUtil {
    /**
     * The VariableResolver for a <code>java.util.Map</code> as Context-Object.
     */
    private static final VariableResolver SIMPLE_VARIABLE_RESOLVER = new VariableResolver() {

        /**
         * Resolves the specified variable within the given context. Returns null if the variable is not found.
         **/
        @Override
        public Object resolveVariable(final String pName, final Object simpleMap) {
            final Map<?, ?> ctx = (Map<?, ?>) simpleMap;
            return ctx.get(pName);
        }
    };

    private static final ELEvaluator SIMPLE_EVALUATOR = new ELEvaluator(SIMPLE_VARIABLE_RESOLVER);

    /** Hide Constructor (Utility Pattern). */
    private ExpressionLanguageUtil() {
        super();
    }

    /**
     * Helper-Function to evaluate a Expression to a String.
     *
     * @param expression The Expression to be evaluated
     * @param context The context where the expression has Access
     * @return The Result as String
     * @throws ELException Throw an Exception if the expression is not valid.
     */
    public static String evaluateExpressionLanguage(final String expression, final Map<?, ?> context) throws ELException {
        return (String) evaluateExpressionLanguage(expression, context, String.class);
    }

    /**
     * Helper-Function to evaluate a Expression to a given Class.
     *
     * @param expression The Expression to be evaluated
     * @param context The context where the expression has Access
     * @param returnType the Class-Object who will be returned
     * @return The Result as the given return-Type
     * @throws ELException Throw an Exception if the expression is not valid.
     */
    public static Object evaluateExpressionLanguage(final String expression, final Map<?, ?> context, final Class<?> returnType)
        throws ELException {
        return SIMPLE_EVALUATOR.evaluate(expression, context, returnType, null, null);
    }

}
