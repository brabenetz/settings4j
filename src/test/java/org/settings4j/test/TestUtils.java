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
package org.settings4j.test;

import java.io.File;
import java.net.URL;

import org.settings4j.Settings4j;
import org.settings4j.Settings4jRepository;
import org.settings4j.config.DOMConfigurator;
import org.settings4j.contentresolver.ClasspathContentResolver;
import org.settings4j.settings.DefaultSettingsRepository;


/**
 * @author brabenetz
 */
public final class TestUtils {

    /** hide default constructor (utility pattern). */
    private TestUtils() {
        super();
    }

    /**
     * reload org/settings4j/config/defaultsettings4j.xml.
     */
    public static void reconfigureSettings4jWithDefaultConfig() {
        // settings4j will be configured with the default-fallback-config if no connector exists:
        // org/settings4j/config/defaultsettings4j.xml
        Settings4j.getSettingsRepository().getSettings().removeAllConnectors();
        Settings4j.getString("something"); // reconfigure Settings4j with default values
    }

    /**
     * Read and parse a Settings4j.xml Configuration from Classpath and return the SettingsRepository.
     * 
     * @param classpathUrl The Classpath url where the settings4j.xml are placed.
     * @return The SettingsRepository based on the given configuration found in the classpathUrl
     */
    public static Settings4jRepository getConfiguredSettingsRepository(final String classpathUrl) {

        final URL url = ClasspathContentResolver.getResource(classpathUrl);
        final Settings4jRepository settingsRepository = new DefaultSettingsRepository();
        DOMConfigurator.configure(url, settingsRepository);
        return settingsRepository;
    }

    /**
     * The TempFolder from System Property "java.io.tmpdir" and the subfolder "Settings4jUnittest".<br>
     * <br>
     * On my windows maschine this is:<br>
     * C:\Dokumente und Einstellungen\hbrabenetz\Lokale Einstellungen\Temp\Settings4jUnittest\<br>
     * 
     * @return to tempfolder for unittests
     */
    public static File getTmpFolder() {
        final String tmpdir = System.getProperty("java.io.tmpdir");
        final File tmpFolder = new File(tmpdir + "/Settings4jUnittest");
        return tmpFolder;
    }

    /**
     * The test Folder for Unittest.<br>
     * This is normaly a subfolder "target/test/Settings4jUnittest" of this current Project.
     * 
     * @return the subfolder "target/test/Settings4jUnittest" of the current Project.
     */
    public static File getTestFolder() {
        final File testFolder = new File("target/test/Settings4jUnittest");
        return testFolder;
    }

}
