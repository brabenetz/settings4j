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
package org.settings4j.connector;

import java.io.UnsupportedEncodingException;

import org.settings4j.ContentResolver;
import org.settings4j.contentresolver.FSContentResolver;
import org.settings4j.contentresolver.UnionContentResolver;

/**
 * The FileSystem implementation of an {@link org.settings4j.Connector}.
 * <p>
 * 
 * @author Harald.Brabenetz
 *
 */
public class FSConnector extends AbstractConnector {

    /** General Logger for this Class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(FSConnector.class);

    private final FSContentResolver fsContentResolver = new FSContentResolver();
    private ContentResolver unionContentResolver = new UnionContentResolver(this.fsContentResolver);
    private String charset = "UTF-8";

    /** {@inheritDoc} */
    public byte[] getContent(final String key) {
        return this.fsContentResolver.getContent(key);
    }

    /** {@inheritDoc} */
    public Object getObject(final String key) {
        if (getObjectResolver() != null) {
            return getObjectResolver().getObject(key, this.unionContentResolver);
        }
        // else
        return null;
    }

    /** {@inheritDoc} */
    public String getString(final String key) {
        try {
            final byte[] content = getContent(key);
            if (content != null) {
                return new String(this.fsContentResolver.getContent(key), this.charset);
            }
            // else
            return null;

        } catch (final UnsupportedEncodingException e) {
            // should never occure with "UTF-8"
            LOG.error("Charset not found: " + this.charset, e);
            return null;
        }
    }

    public String getCharset() {
        return this.charset;
    }

    public void setCharset(final String charset) {
        this.charset = charset;
    }

    /**
     * Delegate the rootFolderPath to the {@link FSContentResolver#setRootFolderPath(String)}.
     * 
     * @param rootFolderPath The root Folder Path where the settings could be stored.
     */
    public void setRootFolderPath(final String rootFolderPath) {
        this.fsContentResolver.setRootFolderPath(rootFolderPath);
    }

    /** {@inheritDoc} */
    public void setContentResolver(final ContentResolver contentResolver) {
        this.unionContentResolver = new UnionContentResolver(this.fsContentResolver);
        this.unionContentResolver.addContentResolver(contentResolver);
    }

}
