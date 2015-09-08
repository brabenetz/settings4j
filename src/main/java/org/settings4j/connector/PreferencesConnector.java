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

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.lang3.Validate;

/**
 * Connector which uses the {@link Preferences} feature of Java.
 * <p>
 * Locations:
 * </p>
 * <ul>
 * <li>In W98/Me/NT/W2K/XP/W2003/Vista/W7-32/W7-64 this information is stored in the fragile, hard-to-back-up registry in HKEY_LOCAL_MACHINE\JavaSoft\Prefs for
 * system Preferences and HKEY_CURRENT_USER\JavaSoft\Prefs for user Preferences in a very fluffy format. Every capital letter is preceded with a / and any
 * fields containing accented letters are encoded in Base64.</li>
 * <li>In Windows, user Preferences show up at HKEY_CURRENT_USER\Software\JavaSoft\Prefs\com\mindprod\replicator and HKEY_USERS\
 * usernamexxx\Software\JavaSoft\Prefs\com\mindprod\replicator where the package name is com.mindprod.replicator.</li>
 * <li>In Windows, system Preferences show up at HKEY_LOCAL_MACHINE\Software\JavaSoft\Prefs\com\mindprod\replicator, where the package name is
 * com.mindprod.replicator</li>
 * <li>In Linux, preferences are stored in ordinary xml files. System Preferences are stored in etc/.java.</li>
 * <li>In Linux, user preferences are stored in ~/.java. The file for user preferences may have a goofy base64-encoded name something like this:<br>
 * /home/username/.java/.userPrefs/ com/mindprod/replicator/_!':!bw "t!#4!b@"p!'4!~!"w!()!bw"k!#4!cg"l!(!!b!"p!'}@"0!'8!cg==</li>
 * </ul>
 *
 * @author Harald.Brabenetz
 */
public class PreferencesConnector extends AbstractPropertyConnector {

    private final Preferences systemPrefs;

    private final Preferences userPrefs;

    /**
     * Default Constructor initialize the User and System {@link Preferences}.
     */
    public PreferencesConnector() {
        this(Preferences.systemRoot(), Preferences.userRoot());
    }

    /**
     * @param systemPrefs
     *        {@link Preferences#systemRoot()}
     * @param userPrefs
     *        {@link Preferences#userRoot()}
     */
    protected PreferencesConnector(final Preferences systemPrefs, final Preferences userPrefs) {
        super();
        this.systemPrefs = systemPrefs;
        this.userPrefs = userPrefs;
    }

    /** {@inheritDoc} */
    @Override
    protected String getProperty(final String keyPath, final String defaultValue) {
        Validate.notNull(keyPath);
        final String normalizedKey = normalizeKey(keyPath);
        final String path = getPath(normalizedKey);
        final String key = getKey(normalizedKey);
        String value = getPreferenceValue(path, key, defaultValue, this.userPrefs);
        if (value == null) {
            value = getPreferenceValue(path, key, defaultValue, this.systemPrefs);
        }
        return value;
    }



    private String getPath(final String keyPath) {
        String path = null;
        final int endOfPath = keyPath.lastIndexOf('/');
        if (endOfPath != -1) {
            path = keyPath.substring(0, endOfPath);
        }
        return path;
    }

    /**
     * Resolve the given path and key against the given Preferences.
     *
     * @param path the preferences path (placeholder part before '/')
     * @param key the preferences key (placeholder part after '/')
     * @param defaultValue the default Value.
     * @param preferences the Preferences to resolve against
     * @return the value for the placeholder, or <code>null</code> if none found
     */
    protected String getPreferenceValue(final String path, final String key, final String defaultValue,
        final Preferences preferences) {
        if (path != null) {
            // Do not create the node if it does not exist...
            try {
                if (preferences.nodeExists(path)) {
                    return preferences.node(path).get(key, defaultValue);
                }
                return defaultValue;
            } catch (final BackingStoreException e) {
                throw new RuntimeException("Cannot access specified node path [" + path + "]", e);
            }
        }
        return preferences.get(key, defaultValue);
    }


    /**
     * Stores the given Value into the User-Preferences.
     *
     * @param keyPath - The full KeyPath
     * @param value - The new Value
     */
    public void setString(final String keyPath, final String value) {
        Validate.notNull(keyPath);
        final String normalizedKey = normalizeKey(keyPath);
        final String path = getPath(normalizedKey);
        final String key = getKey(normalizedKey);
        setPreferenceValue(path, key, value, this.userPrefs);
    }

    /**
     * Stores the given Value into the System-Preferences.
     *
     * @param keyPath - The full KeyPath
     * @param value - The new Value
     */
    public void setSystemString(final String keyPath, final String value) {
        Validate.notNull(keyPath);
        final String normalizedKey = normalizeKey(keyPath);
        final String path = getPath(normalizedKey);
        final String key = getKey(normalizedKey);
        setPreferenceValue(path, key, value, this.systemPrefs);
    }

    /**
     * Resolve the given path and key against the given Preferences.
     *
     * @param path the preferences path (placeholder part before '/')
     * @param key the preferences key (placeholder part after '/')
     * @param value the Value to store.
     * @param preferences the Preferences to resolve against
     */
    protected void setPreferenceValue(final String path, final String key, final String value,
        final Preferences preferences) {
        if (path != null) {
            preferences.node(path).put(key, value);
        } else {
            preferences.put(key, value);
        }
        try {
            preferences.flush();
        } catch (BackingStoreException e) {
            throw new RuntimeException("Cannot access specified node path [" + path + "]", e);
        }
    }

    private String getKey(final String keyPath) {
        String key = keyPath;
        final int endOfPath = keyPath.lastIndexOf('/');
        if (endOfPath != -1) {
            key = keyPath.substring(endOfPath + 1);
        }
        return key;
    }

    private String normalizeKey(final String key) {
        Validate.notNull(key);
        String normalizeKey = key;

        normalizeKey = normalizeKey.replace('\\', '/');

        if (normalizeKey.startsWith("/")) {
            normalizeKey = normalizeKey.substring(1);
        }
        return normalizeKey;
    }
}
