package org.settings4j.helper.spring;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;

public class Settings4jContextLoaderListener extends ContextLoaderListener {

	protected ContextLoader createContextLoader() {
		return new Settings4jContextLoader();
	}

}
