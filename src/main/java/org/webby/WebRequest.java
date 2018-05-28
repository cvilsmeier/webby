package org.webby;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

/**
 * A WebRequest represents an incoming HTTP request.
 */
public interface WebRequest {

	// request

	/**
	 * Returns the HTTP method, e.g. "GET" or "POST".
	 */
	String getMethod();

	/**
	 * Returns true if the HTTP method is "GET", otherwise false.
	 */
	boolean isGet();

	/**
	 * Returns true if the HTTP method is "POST", otherwise false.
	 */
	boolean isPost();

	/**
	 * Returns the HTTP url path.
	 */
	String getPath();

	/**
	 * Returns true if the HTTP request contains a named parameter, otherwise false.
	 */
	boolean hasParameter(String name);

	/**
	 * Returns a named parameter, or throws a Exception if the parameter is not found.
	 */
	String getParameter(String name);

	/**
	 * Returns a named parameter, or a default value if the parameter is not found.
	 */
	String getParameter(String name, String defaultValue);

	/**
	 * Returns a file upload paramter or null.
	 */
	FileUpload findFileUpload(String name) throws IOException;

	// attributes

	/**
	 * Sets (or overwrites) a request attribute.
	 */
	void putAttribute(String key, Object value);

	/**
	 * Returns the named request attribute, or throws an Exception if not found.
	 */
	Object getAttribute(String key);

	/**
	 * Returns the named request attribute, or a default value if not found.
	 */
	Object getAttribute(String key, Object defaultValue);

	// session

	/**
	 * Sets (or overwrites) a session atribute.
	 */
	void putSession(String key, Object value);

	/**
	 * Returns the named session attribute, or throws an Exception if not found.
	 */
	Object getSession(String key);

	/**
	 * Returns the named session attribute, or a default value if not found.
	 */
	Object getSession(String key, Object defaultValue);

	/**
	 * Removes the named session attribute.
	 */
	void removeSession(String key);

	/**
	 * Kills the http session. If no HTTP session exists, this method does nothing. 
	 */
	void killSession();

	// cookies

	/**
	 * Returns the named cookie value, or throws an Exception if not found.
	 */
	String getCookieValue(String cookieName);

	/**
	 * Returns the named cookie value, or a default value if not found.
	 */
	String getCookieValue(String cookieName, String defaultValue);

	/**
	 * Adds a cookie.
	 */
	void addCookie(String name, String value, int maxAgeSeconds, boolean secure);

	/**
	 * Returns all cookies that were added for this request.
	 */
	List<Cookie> getAddedCookies();

	// responses

	/**
	 * Returns true if this request has a response, otherwise false.
	 */
	boolean hasResponse();

	// model

	/**
	 * Sets (or overwrites) a model attribute.
	 */
	void putModel(String key, Object value);

	/**
	 * Returns a model attribute by name, or throws an Exception if not found.
	 */
	Object getModel(String key);

	/**
	 * Returns a model attribute by name, or a default value if not found.
	 */
	Object getModel(String key, Object defaultValue);

	/**
	 * Returns the model as a <code>java.util.Map</code>.
	 */
	Map<String, Object> getModel();

	// template response

	/**
	 * Sets the template name.
	 */
	void setTemplate(String template);

	/**
	 * Returns the template name, may be null if not set.
	 */
	String getTemplate();

	// content response

	/**
	 * Sets a content response.
	 */
	void setContent(byte[] contentBytes, String contentType);

	/**
	 * Sets a content response with content type 'text/html; charset=utf8'.
	 */
	void setHtmlContent(String html);

	/**
	 * Sets a content response with content type 'text/plain; charset=utf8'.
	 */
	void setTextContent(String text);

	/**
	 * Returns the content bytes, may be null if not set.
	 */
	byte[] getContentBytes();

	/**
	 * Returns the content type, may be null if not set.
	 */
	String getContentType();

	// download response

	/**
	 * Sets a download response.
	 */
	void setDownload(File file, String name, String contentType, boolean deleteAfterDownload);

	/**
	 * Returns the download file, may be null if not set.
	 */
	File getDownloadFile();

	/**
	 * Returns the download name, may be null if not set.
	 */
	String getDownloadName();

	/**
	 * Returns the download content type, may be null if not set.
	 */
	String getDownloadContentType();

	/**
	 * Returns true if the download file should be deleted after download.
	 */
	boolean isDownloadDeleteAfterDownload();

	// redirect response

	/**
	 * Sets a redirect response.
	 */
	void setRedirect(String redirect);

	/**
	 * Returns the redirect response, may be null if not set.
	 */
	String getRedirect();

	// error response

	/**
	 * Sets an error response.
	 */
	void setError(int status, String message);

	/**
	 * Returns the error status, may be 0 if not set.
	 */
	int getErrorStatus();

	/**
	 * Returns the error message, may be null if not set.
	 */
	String getErrorMessage();

}
