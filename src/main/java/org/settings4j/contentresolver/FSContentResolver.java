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
 * 
 * @author Harald.Brabenetz
 */
public class FSContentResolver implements ContentResolver {

    /** General Logger for this Class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(FSContentResolver.class);


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
     * return the root of this FileSystem ContenResolver.
     * <p>
     * if no one is set, the "." will be returned.
     * 
     * @return the root of this FileSystem ContenResolver.
     */
    public File getRootFolder() {
        if (this.rootFolder == null) {
            LOG.info("FSContentResolver.rootFolder == null");
            // get the current execution directory
            final String tmpdir = ".";
            LOG.info("The TEMP Folder will be used: " + tmpdir + "! ");
            this.rootFolder = new File(tmpdir);
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
                LOG.info("Set RootPath for FSConntentResolver: " + newRootFolder.getAbsolutePath());
            } catch (final IOException e) {
                LOG.warn("cannot create rootFolder: " + rootFolderPath + "! ");
            }
        } else {
            this.rootFolder = newRootFolder;
        }
    }
}
