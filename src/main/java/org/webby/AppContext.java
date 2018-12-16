package org.webby;

import java.io.InputStream;

/**
 * Defines a set of methods to access then underlying ServletContext.
 * When a Webby app is started, an AppContext is created and passed to the Webby App.
 * 
 * @see App
 */
public interface AppContext {

	/**
	 * The servlet's context path, e.g. /myApp.
	 */
	String getContextPath();

	/**
	 * Returns the real path for a virtual path, or null if transformation cannot be performed.
	 */
	String getRealPath(String path);

	/**
	 * Returns an input stream for a resource, or null if not found.
	 */
	InputStream getResourceAsStream(String path);

	/**
	 * Returns a servlet context init parameter, or null if not found.
	 */
	String getInitParameter(String name);

}
