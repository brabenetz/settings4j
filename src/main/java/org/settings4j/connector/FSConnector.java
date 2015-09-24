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
package org.settings4j.connector;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;

import org.settings4j.ContentResolver;
import org.settings4j.contentresolver.FSContentResolver;
import org.settings4j.contentresolver.UnionContentResolver;

/**
 * The FileSystem implementation of an {@link org.settings4j.Connector}.
 *
 * @author Harald.Brabenetz
 */
public class FSConnector extends AbstractConnector {

    private final FSContentResolver fsContentResolver;
    private UnionContentResolver unionContentResolver;
    private String charset = "UTF-8";

    /** Default Constructor (e.g. use in settings4j.xml). */
    public FSConnector() {
        this(new FSContentResolver());
    }

    /**
     * protected Constructor for extensions and Unit-Tests.
     *
     * @param fsContentResolver
     *        {@link FSContentResolver}
     */
    protected FSConnector(final FSContentResolver fsContentResolver) {
        super();
        this.fsContentResolver = fsContentResolver;
        this.unionContentResolver = new UnionContentResolver(this.fsContentResolver);
    }

    @Override
    public byte[] getContent(final String key) {
        return this.fsContentResolver.getContent(key);
    }

    @Override
    public Object getObject(final String key) {
        if (getObjectResolver() != null) {
            return getObjectResolver().getObject(key, this.unionContentResolver);
        }
        // else
        return null;
    }

    @Override
    public String getString(final String key) {
        try {
            final byte[] content = getContent(key);
            if (content != null) {
                // TODO brabenetz 06. Sep. 2015 : With Settings4j-2.1 and JDK 6: use new String(byte[], Charset) instead. (and remove ExceptionHandling)
                return new String(this.fsContentResolver.getContent(key), this.charset);
            }
            // else
            return null;

        } catch (final UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(String.format("Charset not found: %s", this.charset), e);
        }
    }

    /**
     * @param key The Settings4j Key.
     * @param value The value to Store.
     * @throws IOException if an error occured.
     */
    public void setContent(final String key, final byte[] value) throws IOException {
        this.fsContentResolver.setContent(key, value);
    }

    /**
     * @param key The Settings4j Key.
     * @param value The value to Store.
     * @throws IOException if an error occured.
     */
    public void setString(final String key, final String value) throws IOException {
        try {
            // TODO brabenetz 06. Sep. 2015 : With Settings4j-2.1 and JDK 6: use String.getBytes(Charset) instead. (and remove ExceptionHandling)
            setContent(key, value.getBytes(this.charset));
        } catch (final UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(String.format("Charset not found: %s", this.charset), e);
        }
    }

    public String getCharset() {
        return this.charset;
    }

    /**
     * @param charset
     *        a valid {@link Charset}
     * @see Charset#isSupported(String)
     */
    public void setCharset(final String charset) {
        if (!Charset.isSupported(charset)) {
            throw new IllegalCharsetNameException(
                String.format("IllegalCharsetName: '%s'. See: http://docs.oracle.com/javase/8/docs/api/java/nio/charset/StandardCharsets.html", charset));
        }
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

    @Override
    public void setContentResolver(final ContentResolver contentResolver) {
        this.unionContentResolver = new UnionContentResolver(this.fsContentResolver);
        this.unionContentResolver.addContentResolver(contentResolver);
    }

    @Override
    protected ContentResolver getContentResolver() {
        if (hasCustomContentResolver()) {
            return this.unionContentResolver.getContentResolvers()[1];
        }
        return null;
    }

    private boolean hasCustomContentResolver() {
        return this.unionContentResolver.getContentResolvers().length > 1;
    }
    /**
     * return the root of this FileSystem ContenResolver.
     * <p>
     * if no one is set, the "." will be returned.
     * </p>
     *
     * @return the root of this FileSystem ContenResolver.
     */
    public File getRootFolder() {
        return this.fsContentResolver.getRootFolder();
    }
}
