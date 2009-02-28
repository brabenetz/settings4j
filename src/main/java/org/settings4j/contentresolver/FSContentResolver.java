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
import org.settings4j.Constants;
import org.settings4j.ContentResolver;

public class FSContentResolver implements ContentResolver {
    /** General Logger for this Class */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
        .getLog(FSContentResolver.class);


    /** Pseudo URL prefix for loading from the class path: "classpath:" */
    public static final String FILE_URL_PREFIX = "file:";
    
    private File rootFolder;

    public void addContentResolver(ContentResolver contentResolver) {
        throw new UnsupportedOperationException("FSContentResolver cannot add other ContentResolvers");
    }

    public byte[] getContent(String key) {
    	
        if (key.startsWith(FILE_URL_PREFIX)){
            key = key.substring(FILE_URL_PREFIX.length());
        }
        
        File file = new File(key);
        if (file.exists()) {
            try {
                return FileUtils.readFileToByteArray(file);
            } catch (IOException e) {
                LOG.info(e.getMessage(), e);
            }
        } else {
            file = new File(getRootFolder(), key);
            if (file.exists()) {
                try {
                    return FileUtils.readFileToByteArray(file);
                } catch (IOException e) {
                    LOG.info(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    public int setContent(String key, byte[] value) {
    	
        if (key.startsWith(FILE_URL_PREFIX)){
            key = key.substring(FILE_URL_PREFIX.length());
        }

        int status = Constants.SETTING_NOT_POSSIBLE;
        File file;
        
        if (key.startsWith("/")){
        	// Unix-Root
            file = new File(key);
        }else if (key.indexOf(":")>=0){
            // Windows-Root
            file = new File(key);
        } else {
            file = new File(getRootFolder(), key);
        }
        if (LOG.isDebugEnabled()){
            LOG.debug("Store content in: " + file.getAbsolutePath());
        }
        try {
            FileUtils.writeByteArrayToFile(file, value);
            status = Constants.SETTING_SUCCESS;
        } catch (IOException e) {
            LOG.info(e.getMessage(), e);
        }
        return status;
    }

    public File getRootFolder() {
        if (rootFolder == null) {
            LOG.info("FSContentResolver.rootFolder == null");
            // get the default java temporary directory
            String tmpdir = ".";
            if (tmpdir != null) {
                LOG.info("The TEMP Folder will be used: " + tmpdir + "! ");
                rootFolder = new File(tmpdir);
            }
        }
        return rootFolder;
    }

    public void setRootFolderPath(String rootFolderPath) {
        File newRootFolder = new File(rootFolderPath);
        if (!newRootFolder.exists()) {
            try {
                FileUtils.forceMkdir(newRootFolder);
                this.rootFolder = newRootFolder;
                LOG.info("Set RootPath for FSConntentResolver: " + newRootFolder.getAbsolutePath());
            } catch (IOException e) {
                LOG.warn("cannot create rootFolder: " + rootFolderPath + "! ");
            }
        } else {
            this.rootFolder = newRootFolder;
        }
    }
}
