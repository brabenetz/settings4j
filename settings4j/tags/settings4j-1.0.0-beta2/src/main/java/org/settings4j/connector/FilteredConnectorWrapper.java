package org.settings4j.connector;

import org.settings4j.Connector;
import org.settings4j.Constants;
import org.settings4j.ContentResolver;
import org.settings4j.Filter;
import org.settings4j.ObjectResolver;

public class FilteredConnectorWrapper implements Connector {

	private Connector targetConnector;
	private Filter filter;

	public FilteredConnectorWrapper(Connector targetConnector, Filter filter) {
		super();
		if (targetConnector == null){
			throw new IllegalArgumentException("FilteredConnectorWrapper needs a Connector Object");
		}
		if (filter == null){
			throw new IllegalArgumentException("FilteredConnectorWrapper needs a Filter Object");
		}
		this.targetConnector = targetConnector;
		this.filter = filter;
	}

	public void addConnector(Connector connector) {
		targetConnector.addConnector(connector);
	}

	public byte[] getContent(String key) {
    	if (!filter.isValid(key)){
            return null;
    	}
		return targetConnector.getContent(key);
	}

	public String getName() {
		
		return targetConnector.getName();
	}

	public Object getObject(String key) {
    	if (!filter.isValid(key)){
            return null;
    	}
		return targetConnector.getObject(key);
	}

	public String getString(String key) {
    	if (!filter.isValid(key)){
            return null;
    	}
		return targetConnector.getString(key);
	}

	public void init() {
		targetConnector.init();
	}

	public boolean isReadonly() {
		return targetConnector.isReadonly();
	}

	public int setContent(String key, byte[] value) {
    	if (!filter.isValid(key)){
            return Constants.SETTING_NOT_POSSIBLE;
    	}
		return targetConnector.setContent(key, value);
	}

	public void setContentResolver(ContentResolver contentResolver) {
		targetConnector.setContentResolver(contentResolver);
	}

	public void setName(String name) {
		targetConnector.setName(name);
	}

	public int setObject(String key, Object value) {
    	if (!filter.isValid(key)){
            return Constants.SETTING_NOT_POSSIBLE;
    	}
		return targetConnector.setObject(key, value);
	}

	public void setObjectResolver(ObjectResolver objectResolver) {
		targetConnector.setObjectResolver(objectResolver);
	}

	public int setString(String key, String value) {
    	if (!filter.isValid(key)){
            return Constants.SETTING_NOT_POSSIBLE;
    	}
		return targetConnector.setString(key, value);
	}
	
	
}