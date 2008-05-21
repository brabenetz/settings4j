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

package org.settings4j.objectresolver;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;

public class JavaXMLBeansObjectResolver extends AbstractObjectResolver{

    protected Object contentToObject(String key, Properties properties, byte[] content) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content);
        XMLDecoder encoder = new XMLDecoder(byteArrayInputStream);
        return encoder.readObject();
    }

    protected String getObjectReolverKey() {
        return "JavaXMLBeans";
    }

    protected byte[] objectToContent(String key, Properties properties, Object value) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        XMLEncoder encoder = new XMLEncoder(byteArrayOutputStream);
        encoder.writeObject(value);
        encoder.flush();
        encoder.close();
        return byteArrayOutputStream.toByteArray();
    }

}
