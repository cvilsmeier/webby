package org.webby;

import java.io.InputStream;

/**
 * Defines a set of methods to access then underlying ServletContext.
 * When a Webby app is started, an AppContext is created and passed to the Webby App.
 */
public interface AppContext {

	/**
	 * The servlet's context path, e.g. /myApp.
	 */
	String getContextPath();

	/**
	 * Returns an input stream for a resource.
	 */
	InputStream getResourceAsStream(String path);

	/**
	 * Returns a servlet context init parameter.
	 */
	String getInitParameter(String name);

}
