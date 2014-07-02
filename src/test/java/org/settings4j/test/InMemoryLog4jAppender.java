/* ***************************************************************************
 * Copyright (c) 2012 Brabenetz Harald, Austria.
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

package org.settings4j.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;


/**
 * UnitTest Helper for Log4j Configuration tests.
 * 
 * @author brabenetz
 */
public class InMemoryLog4jAppender extends AppenderSkeleton {

    private static final List<String> LINES = Collections.synchronizedList(new ArrayList<String>());

    /**
     * Returns the number of lines in this list. If this list contains more than <tt>Integer.MAX_VALUE</tt> elements,
     * returns <tt>Integer.MAX_VALUE</tt>.
     * 
     * @return the number of lines in this list.
     */
    public static int linesSize() {
        return LINES.size();
    }

    /**
     * Removes all of the lines from this list (optional operation). This list will be empty after this call returns
     * (unless it throws an exception).
     * 
     */
    public static void linesClear() {
        LINES.clear();
    }

    /**
     * Returns the line at the specified position in this list.
     * 
     * @param index index of line to return.
     * @return the line at the specified position in this list.
     * @throws IndexOutOfBoundsException if the index is out of range (index &lt; 0 || index &gt;= size()).
     */
    public static Object linesGet(final int index) throws IndexOutOfBoundsException {
        return LINES.get(index);
    }

    /** {@inheritDoc} */
    public void close() {
        LINES.clear();
    }

    /** {@inheritDoc} */
    public boolean requiresLayout() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    protected void append(final LoggingEvent event) {
        final String message = layout.format(event);
        LINES.add(message);
        System.out.print("[InMemoryLog4jAppender] " + message);
    }

}
