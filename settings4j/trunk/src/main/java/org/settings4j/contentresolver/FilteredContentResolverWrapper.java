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
import org.settings4j.Filter;

public class FilteredContentResolverWrapper implements ContentResolver {

    private ContentResolver targetContentResolver;
	private Filter filter;

    public FilteredContentResolverWrapper(ContentResolver targetContentResolver, Filter filter) {
        super();
		if (targetContentResolver == null){
			throw new IllegalArgumentException("FilteredConnectorWrapper needs a ContentResolver Object");
		}
		if (filter == null){
			throw new IllegalArgumentException("FilteredConnectorWrapper needs a Filter Object");
		}
		this.targetContentResolver = targetContentResolver;
		this.filter = filter;
    }
    
    public synchronized void addContentResolver(ContentResolver contentResolver) {
        targetContentResolver.addContentResolver(contentResolver);
    }

    public byte[] getContent(String key) {
    	if (!filter.isValid(key)){
            return null;
    	}
        return targetContentResolver.getContent(key);
    }

    public int setContent(String key, byte[] value) {
    	if (!filter.isValid(key)){
            return Constants.SETTING_NOT_POSSIBLE;
    	}
        return targetContentResolver.setContent(key, value);
    }
}
