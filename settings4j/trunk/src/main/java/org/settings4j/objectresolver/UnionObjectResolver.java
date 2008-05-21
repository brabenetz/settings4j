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

import org.settings4j.ContentResolver;
import org.settings4j.ObjectResolver;

public class UnionObjectResolver implements ObjectResolver{

    private ObjectResolver[] objectResolvers = new ObjectResolver[0];

    public void addObjectResolver(ObjectResolver objectResolver) {
        
        ObjectResolver[] objectResolversNew = new ObjectResolver[objectResolvers.length+1];
        for (int i = 0; i < objectResolvers.length; i++) {
            objectResolversNew[i] = objectResolvers[i];
        }
        objectResolversNew[objectResolvers.length] = objectResolver;
        
        objectResolvers = objectResolversNew;
    }

    public void notifyContentHasChanged(String key) {
        for (int i = 0; i < objectResolvers.length; i++) {
            objectResolvers[i].notifyContentHasChanged(key);
        }
    }
    
    public Object getObject(String key, ContentResolver contentResolver) {
        Object result = null;
        for (int i = 0; i < objectResolvers.length; i++) {
            result = objectResolvers[i].getObject(key, contentResolver);
            if (result != null){
                return result;
            }
        }
        return result;
    }

    public int setObject(String key, ContentResolver contentResolver, Object value) {
        int status = SETTING_NOT_POSSIBLE;
        for (int i = 0; i < objectResolvers.length; i++) {
            status = objectResolvers[i].setObject(key, contentResolver, value);
            if (status != SETTING_NOT_POSSIBLE){
                return status;
            }
        }
        return status;
    }

    public void setCached(boolean cached){
        // The UnionObjectResolver cannot cache VAlues
        // The UnionObjectResolver only delegate all requests to his objectResolvers
    }

}
