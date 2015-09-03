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
package org.settings4j.contentresolver;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.settings4j.ContentResolver;

/**
 * {@link ContentResolver} implementation to read content from the File System.
 * <p>
 * The optional Path Prefix is "file:".
 * </p>
 * <p>
 * Absolute Windows paths contains a <b>":"</b>.<br>
 * Absolute Unix paths starts with <b>"/"</b>.<br>
 * Other Paths are relative and uses the {@link #getRootFolder()} as root. Which is per default <code>new File(".")</code>.<br>
 * </p>
 *
 * @author Harald.Brabenetz
 */
public class FSContentResolver implements ContentResolver {

    /** General Logger for this Class. */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(FSContentResolver.class);

    /** Pseudo URL prefix for loading from the class path: "classpath:". */
    public static final String FILE_URL_PREFIX = "file:";

    private File rootFolder;

    /** {@inheritDoc} */
    public void addContentResolver(final ContentResolver contentResolver) {
        throw new UnsupportedOperationException("FSContentResolver cannot add other ContentResolvers");
    }

    /** {@inheritDoc} */
    public byte[] getContent(final String key) {
        String normalizedKey = key;
        if (normalizedKey.startsWith(FILE_URL_PREFIX)) {
            normalizedKey = normalizedKey.substring(FILE_URL_PREFIX.length());
        }

        File file = new File(normalizedKey);
        if (file.exists()) {
            try {
                return FileUtils.readFileToByteArray(file);
            } catch (final IOException e) {
                LOG.info(e.getMessage(), e);
            }
        } else {
            file = new File(getRootFolder(), normalizedKey);
            if (file.exists()) {
                try {
                    return FileUtils.readFileToByteArray(file);
                } catch (final IOException e) {
                    LOG.info(e.getMessage(), e);
                }
            }
        }
        return null;
    }


    /**
     * @param key The Settings4j Key.
     * @param value The value to Store.
     * @throws IOException if an error occured.
     */
    public void setContent(final String key, final byte[] value) throws IOException {

        final String normalizedKey;
        if (key.startsWith(FILE_URL_PREFIX)) {
            normalizedKey = key.substring(FILE_URL_PREFIX.length());
        } else {
            normalizedKey = key;
        }

        File file;

        if (normalizedKey.startsWith("/")) {
            // Unix-Root
            file = new File(normalizedKey);
        } else if (normalizedKey.indexOf(':') >= 0) {
            // Windows-Root
            file = new File(normalizedKey);
        } else {
            file = new File(getRootFolder(), normalizedKey);
        }
        LOG.debug("Store content in: {}", file.getAbsolutePath());

        FileUtils.writeByteArrayToFile(file, value);
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
        if (this.rootFolder == null) {
            this.rootFolder = new File(".");
            LOG.info("FSContentResolver.rootFolder is null. The RootPath Folder will be used: {}", this.rootFolder.getAbsolutePath());
        }
        return this.rootFolder;
    }

    /**
     * @param rootFolderPath the root folder Path.
     */
    public void setRootFolderPath(final String rootFolderPath) {
        final File newRootFolder = new File(rootFolderPath);
        if (!newRootFolder.exists()) {
            try {
                FileUtils.forceMkdir(newRootFolder);
                this.rootFolder = newRootFolder;
                LOG.info("Set RootPath for FSConntentResolver: {}", newRootFolder.getAbsolutePath());
            } catch (final IOException e) {
                LOG.warn("cannot create rootFolder: {}!", rootFolderPath, e);
            }
        } else {
            this.rootFolder = newRootFolder;
        }
    }
}
