package org.settings4j.helper.spring;

import java.util.Properties;

import javax.servlet.ServletContext;

import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoader;

public class Settings4jContextLoader extends ContextLoader {

	/**
	 * Name of servlet context parameter (i.e., "<code>contextConfigLocation</code>")
	 * that can specify the config location for the root context, falling back
	 * to the implementation's default otherwise.
	 * @see org.springframework.web.context.support.XmlWebApplicationContext#DEFAULT_CONFIG_LOCATION
	 */
	public static final String CONFIG_DEFAULT_VALUES = "settings4jContextConfigDefaultValues";

	protected void customizeContext(ServletContext servletContext,
			ConfigurableWebApplicationContext wac) {
		Settings4jPlaceholderConfigurer placeholderConfigurer = createPlaceholderConfigurer();
		// TODO hbrabenetz 23.03.2012 : get Prefix from Context!?
		Properties props = getProperties(servletContext);
		String[] configLocations = wac.getConfigLocations();
		for (int i = 0; i < configLocations.length; i++) {
			String configLocation = configLocations[i];
			configLocations[i] = placeholderConfigurer.parseStringValue(configLocation, props);
		}
	}

	private Properties getProperties(ServletContext servletContext) {
		// TODO get Properties from Context "settings4jDefaultValues"
		return new Properties();
	}

	protected Settings4jPlaceholderConfigurer createPlaceholderConfigurer() {
		return new Settings4jPlaceholderConfigurer();
	}

}
