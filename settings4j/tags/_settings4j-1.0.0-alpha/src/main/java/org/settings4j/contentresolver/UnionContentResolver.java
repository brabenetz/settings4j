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

import org.settings4j.ContentResolver;

public class UnionContentResolver implements ContentResolver {

    private ContentResolver[] contentResolvers = new ContentResolver[0];
    
    public synchronized void addContentResolver(ContentResolver contentResolver) {
        
        ContentResolver[] contentResolversNew = new ContentResolver[contentResolvers.length+1];
        for (int i = 0; i < contentResolvers.length; i++) {
            contentResolversNew[i] = contentResolvers[i];
        }
        contentResolversNew[contentResolvers.length] = contentResolver;
        
        contentResolvers = contentResolversNew;
    }

    public byte[] getContent(String key) {
        byte[] result = null;
        for (int i = 0; i < contentResolvers.length; i++) {
            result = contentResolvers[i].getContent(key);
            if (result != null){
                return result;
            }
        }
        return result;
    }

    public int setContent(String key, byte[] value) {
        int status = SETTING_NOT_POSSIBLE;
        for (int i = 0; i < contentResolvers.length; i++) {
            status = contentResolvers[i].setContent(key, value);
            if (status != SETTING_NOT_POSSIBLE){
                return status;
            }
        }
        return status;
    }

}
