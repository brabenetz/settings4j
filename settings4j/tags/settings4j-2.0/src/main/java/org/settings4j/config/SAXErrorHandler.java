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
package org.settings4j.config;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 * Error Handler for parsing the settings4j.xml.
 * 
 * @author Harald.Brabenetz
 *
 */
public class SAXErrorHandler implements ErrorHandler {

    /** General Logger for this Class. */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SAXErrorHandler.class);

    /** {@inheritDoc} */
    public void error(final SAXParseException ex) {
        emitMessage("Continuable parsing error ", ex);
    }

    /** {@inheritDoc} */
    public void fatalError(final SAXParseException ex) {
        emitMessage("Fatal parsing error ", ex);
    }

    /** {@inheritDoc} */
    public void warning(final SAXParseException ex) {
        emitMessage("Parsing warning ", ex);
    }

    private static void emitMessage(final String msg, final SAXParseException ex) {
        LOG.warn(msg + ex.getLineNumber() + " and column " + ex.getColumnNumber() + ". " + ex.getMessage(), //
            ex.getException());
    }
}
