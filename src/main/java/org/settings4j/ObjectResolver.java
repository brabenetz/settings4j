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


/**
 * <p>The ObjectResolver is a Helper to resolve byte[] Content to a Java Object.
 * 
 * <pre>
 * Example Configuration in settings4j.xml:
 * <div style="border-width:1px;border-style:solid;">
 * &lt;connector name="<span style="color:green;">ClasspathConnector</span>"
 *          class="org.settings4j.connector.ClasspathConnector"&gt;
 *     &lt;objectResolver-ref ref="<span style="color:red;">JavaXMLBeansObjectResolver</span>" /&gt;
 * &lt;/connector&gt;
 * &lt;objectResolver name="<span style="color:red;">JavaXMLBeansObjectResolver</span>"
 *          class="org.settings4j.objectresolver.JavaXMLBeansObjectResolver" /&gt;
 * </div></pre>
 * 
 * 
 * <p>You can now store a XMLEncoded (Serialized) Java-Objects into your Classpath.<br />
 * see {@link org.settings4j.objectresolver.JavaXMLBeansObjectResolver} for more details.<br />
 * Or see {@link org.settings4j.objectresolver.SpringConfigObjectResolver} for more details on a Spring Configuration
 * File to generate an Object. Requires Springframework (tested with 2.5.6)<br />
 * 
 * @author Harald.Brabenetz
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
     * <li>The "objectResolverKey" defines which ObjectResolver-Implementation should
     *      solve the byte[] content to a Java-Object</li>
     * <li>Convert the byte[] from {@link ContentResolver#getContent(String key)} to an Object.</li>
     * <li>Maybe additional values are consumed from the propertyfile</li>
     * </ol>
     * 
     * 
     * @param key The Key of the byte[] who should be converted to an Object.
     * @param contentResolver The contentResolver, from where the content could be read.
     * @return the Object, or null if this Object-Resolver can not convert the byte[] Content to an Object.
     */
    Object getObject(String key, ContentResolver contentResolver);
    
    /**
     * Some Implementations of a {@link ObjectResolver} are delegating the functionality
     * to other ObjectResolvers.<br />
     * Examples are: {@link org.settings4j.objectresolver.UnionObjectResolver}
     * 
     * <pre>
     * --------------------------------------
     * &lt;objectResolver name="DefaultObjectResolver" class="org.settings4j.objectresolver.UnionObjectResolver"&gt;
     *     &lt;objectResolver-ref ref="JavaXMLBeansObjectResolver" /&gt;
     *     &lt;objectResolver-ref ref="SpringConfigObjectResolver" /&gt;
     * &lt;/objectResolver&gt;
     * --------------------------------------
     * </pre>
     * 
     * @param objectResolver the original objectResolver to delegate.
     */
    void addObjectResolver(ObjectResolver objectResolver);
}
