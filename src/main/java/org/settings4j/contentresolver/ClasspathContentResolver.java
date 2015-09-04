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
package org.settings4j.contentresolver;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.settings4j.ContentResolver;

/**
 * {@link ContentResolver} implementation to read content from the Classpath.
 * <p>
 * Uses the default ClassLoader: typically the thread context ClassLoader see {@link #getClassLoader()}.
 * </p>
 * <p>
 * Optional Path Prefix is "classpath:".
 * </p>
 *
 * @author Harald.Brabenetz
 */
public class ClasspathContentResolver implements ContentResolver {

    /** General Logger for this Class. */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ClasspathContentResolver.class);

    /** Pseudo URL prefix for loading from the class path: "classpath:". */
    public static final String CLASSPATH_URL_PREFIX = "classpath:";


    /** {@inheritDoc} */
    public void addContentResolver(final ContentResolver contentResolver) {
        throw new UnsupportedOperationException("ClasspathContentResolver cannot add other ContentResolvers");
    }

    /** {@inheritDoc} */
    public byte[] getContent(final String key) {
        final String normalizedKey = normalizeKey(key);

        InputStream is = null;
        try {
            is = getClassLoader().getResourceAsStream(normalizedKey);
            if (is != null) {
                return IOUtils.toByteArray(is);
            }
            // else
            return null;
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * Method to get onlx the URL for the given Key.
     *
     * @param key the key (could have a 'classpath:' prefix or not)
     * @return The {@link URL}, see {@link ClassLoader#getResource(String)}.
     */
    public static URL getResource(final String key) {
        final String normalizedKey = normalizeKey(key);

        return getClassLoader().getResource(normalizedKey);
    }


    /**
     * Return the default ClassLoader to use: typically the thread context ClassLoader, if available; the ClassLoader that loaded the ClasspathContentResolver
     * class will be used as fallback.
     * <p>
     * Call this method if you intend to use the thread context ClassLoader in a scenario where you absolutely need a non-null ClassLoader reference: for
     * example, for class path resource loading (but not necessarily for <code>Class.forName</code>, which accepts a <code>null</code> ClassLoader reference as
     * well).
     * </p>
     *
     * @return the default ClassLoader (never <code>null</code>)
     * @see java.lang.Thread#getContextClassLoader()
     */
    // SuppressWarnings PMD.UseProperClassLoader: ProperClassLoader is only a fall back solution if Thread ContextClassloder not exit.
    @SuppressWarnings("PMD.UseProperClassLoader")
    public static ClassLoader getClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (final Throwable ex) {
            LOG.debug("Cannot access thread context ClassLoader - falling back to system class loader", ex);
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = ClasspathContentResolver.class.getClassLoader();
        }
        return cl;
    }

    private static String normalizeKey(final String key) {
        String normalizedKey = key;
        if (normalizedKey.startsWith(CLASSPATH_URL_PREFIX)) {
            normalizedKey = normalizedKey.substring(CLASSPATH_URL_PREFIX.length());
        }
        if (normalizedKey.startsWith("/")) {
            normalizedKey = normalizedKey.substring(1);
        }
        return normalizedKey;
    }
}
