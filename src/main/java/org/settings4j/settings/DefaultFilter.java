package org.settings4j.settings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.settings4j.Filter;

public class DefaultFilter implements Filter {

	/** General Logger for this Class */
	private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
			.getLog(DefaultFilter.class);
	private List includePatterns = new ArrayList();
	private List excludePatterns = new ArrayList();
	
	public void addExclude(String pattern) {
		try {
			Pattern p = Pattern.compile(pattern);
			excludePatterns.add(p);
		} catch (Exception e) {
			LOG.warn("cannnot compile Pattern-String '" + pattern + "'" + e.getMessage(), e);
		}
	}

	public void addInclude(String pattern) {
		try {
			Pattern p = Pattern.compile(pattern);
			includePatterns.add(p);
		} catch (Exception e) {
			LOG.warn("cannnot compile Pattern-String '" + pattern + "'" + e.getMessage(), e);
		}
	}

	public boolean isValid(String key) {
		// if exclude match, return always false.
		for (Iterator iterator = excludePatterns.iterator(); iterator.hasNext();) {
			Pattern pattern = (Pattern) iterator.next();
			if (pattern.matcher(key).matches()){
				return false;
			}
		}

		// if no include is defined, return always true
		if (includePatterns.isEmpty()){
			return true;
		}
		
		// if include match, return true.
		for (Iterator iterator = includePatterns.iterator(); iterator.hasNext();) {
			Pattern pattern = (Pattern) iterator.next();
			if (pattern.matcher(key).matches()){
				return true;
			}
		}
		
		// no includePattern matched.
		return false;
	}

}
