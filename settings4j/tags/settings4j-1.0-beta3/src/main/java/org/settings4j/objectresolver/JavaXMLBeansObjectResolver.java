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

import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.io.ByteArrayInputStream;
import java.util.Properties;

import org.settings4j.ContentResolver;

/**
 * This ObjectResolver convert a byte[] to an Object with the {@link XMLDecoder}.
 * 
 * @author Harald.Brabenetz
 */
public class JavaXMLBeansObjectResolver extends AbstractObjectResolver {

    /** General Logger for this Class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(JavaXMLBeansObjectResolver.class);

    /** {@inheritDoc} */
    protected Object contentToObject(final String key, final Properties properties, final byte[] content,
            final ContentResolver contentResolver) {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content);
        final XMLDecoder encoder = new XMLDecoder(byteArrayInputStream);
        encoder.setExceptionListener(new LogDecoderExceptionListener(key));
        return encoder.readObject();
    }

    /**
     * Log out Exception during Encoding a byte[] to an Object<br />
     * <br />
     * Example:<br />
     * The {@link org.springframework.jdbc.datasource.AbstractDataSource} Object<br />
     * had Getter and Setter for "logWriter" who throws per default an {@link UnsupportedOperationException}.<br />
     * 
     * @author Harald.Brabenetz
     */
    private static class LogDecoderExceptionListener implements ExceptionListener {

        private final String key;

        public LogDecoderExceptionListener(final String key) {
            super();
            this.key = key;
        }

        /** {@inheritDoc} */
        public void exceptionThrown(final Exception e) {
            LOG.warn("Ignore error on decoding Object from key: " + this.key + "! " + e.getClass().getName() + ": "
                + e.getMessage() + "; Set Loglevel DEBUG for more informations.");
            if (LOG.isDebugEnabled()) {
                LOG.debug(e.getMessage(), e);
            }
        }
    }
}
