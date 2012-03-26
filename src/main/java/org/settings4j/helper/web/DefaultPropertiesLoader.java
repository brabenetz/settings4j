package org.settings4j.helper.web;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.settings4j.Settings4j;
import org.settings4j.connector.PropertyFileConnector;

public class DefaultPropertiesLoader {

	/** General Logger for this Class */
	private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
			.getLog(DefaultPropertiesLoader.class);
	
	public static final String DEFAULT_PROPERTIES = "settings4jDefaultProperties";
	
	private static Boolean isInitialized = Boolean.FALSE;
	
	public void initDefaultPropertiesConnector(ServletContext servletContext){
		synchronized (DefaultPropertiesLoader.class) {
			if (!isInitialized.booleanValue()) {
				addPropertyConnector(servletContext);
				isInitialized = Boolean.TRUE;
			} else {
				LOG.info("DefaultConfigLoader cannot run twice in one VM");
			}
		}
	}

	private void addPropertyConnector(ServletContext servletContext) {
		if (servletContext.getInitParameter(DEFAULT_PROPERTIES) != null) {
			Properties property = getDefaultProperties(servletContext);
			addPropertyConnector(property);
		}
	}

	private void addPropertyConnector(Properties property) {
		PropertyFileConnector propertyFileConnector =  new PropertyFileConnector();
		propertyFileConnector.setProperty(property);
		Settings4j.getSettingsRepository().getSettings().addConnector(propertyFileConnector);
	}

	private Properties getDefaultProperties(ServletContext servletContext) {
		String defaultProperties = servletContext.getInitParameter(DEFAULT_PROPERTIES);
		Properties  property = new Properties();
		try {
			property.load(new ByteArrayInputStream(defaultProperties.getBytes("ISO-8859-1")));
		} catch (UnsupportedEncodingException e) {
			// every JDK must have the ISO-8859-1 Charset;
			throw new IllegalStateException(e.getMessage());
		} catch (IOException e) {
			// IOException never happens in  ByteArrayInputStream implementation.
			throw new IllegalStateException(e.getMessage());
		}
		return property;
	}
}
