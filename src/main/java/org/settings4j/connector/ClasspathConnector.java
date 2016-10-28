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
package org.settings4j.connector;

import java.nio.charset.Charset;

import org.apache.commons.io.Charsets;
import org.settings4j.ContentResolver;
import org.settings4j.contentresolver.ClasspathContentResolver;
import org.settings4j.contentresolver.UnionContentResolver;

/**
 * Connector implementation to read Settings from the Classpath.
 * <p>
 * Default Charset "UTF-8" is used to read the content from Classpath.
 * </p>
 *
 * @author Harald.Brabenetz
 */
public class ClasspathConnector extends AbstractConnector {

    private final ClasspathContentResolver classpathContentResolver;
    private UnionContentResolver unionContentResolver;
    private Charset charset = Charsets.UTF_8;

    /** Default Constructor (e.g. use in settings4j.xml). */
    public ClasspathConnector() {
        this(new ClasspathContentResolver());
    }

    /**
     * protected Constructor for extensions and Unit-Tests.
     *
     * @param classpathContentResolver
     *        {@link ClasspathContentResolver}
     */
    protected ClasspathConnector(final ClasspathContentResolver classpathContentResolver) {
        super();
        this.classpathContentResolver = classpathContentResolver;
        this.unionContentResolver = new UnionContentResolver(this.classpathContentResolver);
    }

    @Override
    public byte[] getContent(final String key) {
        return this.classpathContentResolver.getContent(key);
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
        final byte[] content = getContent(key);
        if (content != null) {
            return new String(this.classpathContentResolver.getContent(key), this.charset);
        }
        return null;
    }

    public String getCharset() {
        return this.charset.name();
    }

    /**
     * @param charset
     *        a valid {@link Charset}
     * @see Charset#isSupported(String)
     */
    public void setCharset(final String charset) {
        this.charset = Charset.forName(charset);
    }

    @Override
    public void setContentResolver(final ContentResolver contentResolver) {
        this.unionContentResolver = new UnionContentResolver(this.classpathContentResolver);
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
}
