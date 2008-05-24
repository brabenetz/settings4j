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

package org.settings4j;

import java.io.File;
import java.net.URL;

import org.settings4j.config.DOMConfigurator;
import org.settings4j.contentresolver.ClasspathContentResolver;
import org.settings4j.settings.DefaultSettings;
import org.settings4j.settings.HierarchicalSettingsRepository;

public class UtilTesting {

    /** Hide constructor */
    private UtilTesting() {
        super();
    }
    
    public static SettingsRepository getConfiguredSettingsRepository(String classpathUrl){

        URL url = ClasspathContentResolver.getResource(classpathUrl);
        SettingsRepository settingsRepository = new HierarchicalSettingsRepository(new DefaultSettings("root"));
        DOMConfigurator.configure(url, settingsRepository);
        return settingsRepository;
    }
    
    public static File getTmpFolder(){
        String tmpdir = System.getProperty("java.io.tmpdir");
        File tmpFolder = new File(tmpdir + "Settings4jUnittest");
        return tmpFolder;
    }
    
    public static File getTestFolder(){
        File testFolder = new File("test/Settings4jUnittest");
        return testFolder;
    }

}
