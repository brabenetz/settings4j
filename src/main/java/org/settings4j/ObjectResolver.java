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

import org.settings4j.objectresolver.JavaXMLBeansObjectResolver;
import org.settings4j.objectresolver.ReadOnlyObjectResolverWrapper;
import org.settings4j.objectresolver.UnionObjectResolver;

/**
 * The ObjectResolver is a Helper to resolce byte[] Content to a Java Object.<br />
 * <br />
 * <pre>
 * Example:
 * <div style="border-width:1px;border-style:solid;">
 * &lt;connector name="<span style="color:green;">ClasspathConnector</span>" class="org.settings4j.connector.ClasspathConnector"&gt;
 *     &lt;objectResolver-ref ref="<span style="color:red;">JavaXMLBeansObjectResolver</span>" /&gt;
 * &lt;/connector&gt;
 * <span style="color:blue;">&lt;root&gt;</span>
 *     &lt;connector-ref ref="<span style="color:green;">ClasspathConnector</span>" /&gt;
 * <span style="color:blue;">&lt;/root&gt;</span>
 * &lt;objectResolver name="<span style="color:red;">JavaXMLBeansObjectResolver</span>" class="org.settings4j.objectresolver.JavaXMLBeansObjectResolver" /&gt;
 * </div></pre>
 * 
 * 
 * You can now store a XMLEncoded (Serialized) Java-Objects into your Classpath. see {@link JavaXMLBeansObjectResolver} for more details.<br />
 * <br />
 * Most ObjectResolver-implementations needs a property-File for more informations, how the XML-File should be resolved.<br />
 * By convention the propertyfile will be searched by the given contentResolver under key + ".properties"
 * 
 * 
 * @author hbrabenetz
 *
 */
public interface ObjectResolver {
    
    /**
     * Reads the byte[] content from the ContentResolver and creates an Object.
     * 
     * The normal usecase of an implementation of this ObjectResolver Interface:<br />
     * 
     * <ol>
     * <li>Read the Propertyfile from {@link ContentResolver}.getContent(key + ".properties")</li>
     * <li>Read the Value of "objectResolverKey" from propertyfile</li>
     * <li>The "objectResolverKey" defines which ObjectResolver-Implementation should solve the byte[] content to a Java-Object</li>
     * <li>Convert the byte[] from {@link ContentResolver#getContent(String key)} to an Object.</li>
     * <li>Maybe additional values are consumed from the propertyfile</li>
     * </ol>
     * 
     * 
     * @param key The Key of the byte[] who should be converted to an Object.
     * @param contentResolver The contentResolver, from where the content could be read.
     * @return the Object, or null if this Object-Resolver can not convert the byte[] Content to an Object.
     */
    public Object getObject(String key, ContentResolver contentResolver);
    
    /**
     * Serialize and store the byte[] with the ContentResolver under the given key.
     * 
     * The normal usecase of an implementation of this ObjectResolver Interface:<br />
     * 
     * <ol>
     * <li>Read the Propertyfile from {@link ContentResolver}.getContent(key + ".properties")</li>
     * <li>Read the Value of "objectResolverKey" from propertyfile</li>
     * <li>The "objectResolverKey" defines which ObjectResolver-Implementation should store the Java-Object to an Byte[]</li>
     * <li>Convert the Object to a byte[] and store it with {@link ContentResolver#setContent(String, byte[])} .</li>
     * <li>Maybe additional values are consumed from the propertyfile</li>
     * </ol>
     * 
     * @param key The Key of the byte[] the serialized Object should be stored.
     * @param contentResolver The ContentResolver is used to store the byte[] content.
     * @param value The new Object who should be serialized.
     * @return {@link Constants#SETTING_SUCCESS} or {@link Constants#SETTING_NOT_POSSIBLE}
     */
    public int setObject(String key, ContentResolver contentResolver, Object value);

    /**
     * Some Implementations of a {@link ObjectResolver} are delegating the functionality
     * to other ObjectResolvers.<br />
     * Examples are: {@link ReadOnlyObjectResolverWrapper} or {@link UnionObjectResolver}
     * 
     * <pre>
     * --------------------------------------
     * &lt;objectResolver name="DefaultObjectResolver" class="org.settings4j.objectresolver.UnionObjectResolver"&gt;
     *     &lt;objectResolver-ref ref="JavaXMLBeansObjectResolver" /&gt;
     *     &lt;objectResolver-ref ref="SpingConfigObjectResolver" /&gt;
     * &lt;/objectResolver&gt;
     * --------------------------------------
     * </pre>
     * 
     * @param objectResolver the original objectResolver to delegate.
     */
    public void addObjectResolver(ObjectResolver objectResolver);

    
    /**
     * Notify the ObjectResolver-Implementation if a byte[] content is stored by some ContentResolver<br />
     * <br />
     * This is the right place to clear cached Objects for the given key.<br />
     * 
     * @param key The key from which the Content has changed.
     */
    public void notifyContentHasChanged(String key);
}
