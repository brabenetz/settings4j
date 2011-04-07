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

import org.settings4j.connector.PropertyFileConnector;
import org.settings4j.connector.SystemPropertyConnector;
import org.settings4j.contentresolver.UnionContentResolver;

/**
 * a ContentResolver is a Helper for read/write byte[] content for a given key.
 * 
 * <pre>
 * Example configuration in settings4j.xml:
 * --------------------------------------
 * &lt;contentResolver name="ClasspathContentResolver" class="org.settings4j.contentresolver.ClasspathContentResolver"&gt;
 * &lt;/contentResolver&gt;
 * --------------------------------------
 * </pre>
 * 
 * This is usefull for {@link SystemPropertyConnector} or {@link PropertyFileConnector}.
 * If you define a ContentResolver in this Connectors, you can rever to a File of the FileSystem or Classpath.
 * 
 * <pre>
 * Example Connector usage in settings4j.xml:
 * --------------------------------------
 * &lt;connector name="SystemPropertyConnector" class="org.settings4j.connector.SystemPropertyConnector" &gt;
 *     &lt;contentResolver-ref ref="ClasspathContentResolver" /&gt;
 * &lt;/connector&gt;
 * --------------------------------------
 * 
 * Example usage in java-code:
 * 
 * --------------------------------------
 * // alternativ start myapp with -Dxyz=com/mycompany/myapp/xyz-config.xml
 * System.setProperty("xyz", "com/mycompany/myapp/xyz-config.xml"); //refer to the ClasspathContentResolver
 * 
 * // somewhere in myapp:
 * byte[] xyzConfig = Settings.getContent("xyz"); // get Classpath-URL from the SystemPropertyConnector
 * --------------------------------------
 * 
 * </pre>
 * 
 * @author hbrabenetz
 *
 */
public interface ContentResolver {
    
    /**
     * Reads the Content for the given Key or null if nothing where found.
     * 
     * @param key The key
     * @return The byte[] Content or null if nothing where found.
     */
    byte[] getContent(String key);
    
    /**
     * Some Implementations of a {@link ContentResolver} are delegating the functionality
     * to other ContentResolvers.<br />
     * Examples are: {@link UnionContentResolver}
     * 
     * <pre>
     * --------------------------------------
     * &lt;contentResolver name="DefaultContentResolver" class="org.settings4j.contentresolver.UnionContentResolver"&gt;
     *     &lt;contentResolver-ref ref="FSContentResolver" /&gt;
     *     &lt;contentResolver-ref ref="ClasspathContentResolver" /&gt;
     * &lt;/contentResolver&gt;
     * --------------------------------------
     * </pre>
     * 
     * @param contentResolver the original contentResolver to delegate.
     */
    void addContentResolver(ContentResolver contentResolver);
}
