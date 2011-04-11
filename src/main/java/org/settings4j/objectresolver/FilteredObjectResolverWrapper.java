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
import org.settings4j.Filter;
import org.settings4j.ObjectResolver;

public class FilteredObjectResolverWrapper implements ObjectResolver{

    private ObjectResolver targetObjectResolver;
	private Filter filter;

    public FilteredObjectResolverWrapper(ObjectResolver targetObjectResolver, Filter filter) {
        super();
		if (targetObjectResolver == null){
			throw new IllegalArgumentException("FilteredConnectorWrapper needs a ObjectResolver Object");
		}
		if (filter == null){
			throw new IllegalArgumentException("FilteredConnectorWrapper needs a Filter Object");
		}
		this.targetObjectResolver = targetObjectResolver;
		this.filter = filter;
    }


    public void addObjectResolver(ObjectResolver objectResolver) {
        targetObjectResolver.addObjectResolver(objectResolver);
    }


    public Object getObject(String key, ContentResolver contentResolver) {
    	if (!filter.isValid(key)){
            return null;
    	}
        return targetObjectResolver.getObject(key, contentResolver);
    }
}