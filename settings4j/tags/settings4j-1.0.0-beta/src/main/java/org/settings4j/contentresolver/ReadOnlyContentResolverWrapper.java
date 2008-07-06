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

import org.settings4j.Constants;
import org.settings4j.ContentResolver;

public class ReadOnlyContentResolverWrapper implements ContentResolver {

    private ContentResolver targetContentResolver;

    public ReadOnlyContentResolverWrapper(ContentResolver targetContentResolver) {
        super();
        this.targetContentResolver = targetContentResolver;
    }
    
    public synchronized void addContentResolver(ContentResolver contentResolver) {
        targetContentResolver.addContentResolver(contentResolver);
    }

    public byte[] getContent(String key) {
        return targetContentResolver.getContent(key);
    }

    public int setContent(String key, byte[] value) {
        return Constants.SETTING_NOT_POSSIBLE;
    }

}
