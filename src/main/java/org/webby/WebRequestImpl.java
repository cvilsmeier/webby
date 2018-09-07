package org.webby;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

public class WebRequestImpl implements WebRequest {

	// request
	private final String method;
	private final String path;
	private final HttpServletRequest httpRequest;
	// cookies
	private List<Cookie> addedCookies = new ArrayList<>();
	// model
	private Map<String, Object> model = new HashMap<>();
	// template response
	private String template = null;
	// json response
	private String json = null;
	// content response
	private byte[] contentBytes = null;
	private String contentType = null;
	// download response
	private File downloadFile = null;
	private String downloadName = null;
	private String downloadContentType = null;
	private boolean downloadDeleteAfterDownload = false;
	// redirect response
	private String redirect = null;
	// error response
	private int errorStatus = 0;
	private String errorMessage = null;

	public WebRequestImpl(String method, String path, HttpServletRequest httpRequest) {
		super();
		this.method = method;
		this.path = path;
		this.httpRequest = httpRequest;
	}

	// request

	@Override
	public String getMethod() {
		return method;
	}

	@Override
	public boolean isGet() {
		return method.equals("GET");
	}

	@Override
	public boolean isPost() {
		return method.equals("POST");
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public boolean hasParameter(String name) {
		return httpRequest.getParameter(name) != null;
	}

	@Override
	public String getParameter(String name) {
		String v = httpRequest.getParameter(name);
		if (v == null) {
			throw new RuntimeException("parameter \"" + name + "\" not found in request");
		}
		return v;
	}

	@Override
	public String getParameter(String name, String defaultValue) {
		String v = httpRequest.getParameter(name);
		return v != null ? v : defaultValue;
	}

	@Override
	public FileUpload findFileUpload(String name) throws IOException {
		try {
			Part p = httpRequest.getPart(name);
			return p != null ? new FileUploadImpl(p) : null;
		} catch (Exception ex) {
			throw new IOException("cannot get part", ex);
		}
	}

	// attributes

	@Override
	public void putAttribute(String key, Object value) {		
		httpRequest.setAttribute(key, value);
	}

	@Override
	public Object getAttribute(String key, Object defaultValue) {
		Object value = httpRequest.getAttribute(key);
		return value != null ? value : defaultValue;
	}

	@Override
	public Object getAttribute(String key) {
		Object value = httpRequest.getAttribute(key);
		if (value == null) {
			throw new RuntimeException("attribute \"" + key + "\" not found in request");
		}
		return value;
	}

	// session

	@Override
	public void putSession(String key, Object value) {
		HttpSession httpSession = httpRequest.getSession(true);
		httpSession.setAttribute(key, value);
	}

	@Override
	public Object getSession(String key) {
		HttpSession httpSession = httpRequest.getSession(false);
		if (httpSession == null) {
			throw new RuntimeException("session value \"" + key + "\" not found: no session");
		}
		Object v = httpSession.getAttribute(key);
		if (v == null) {
			throw new RuntimeException("session value \"" + key + "\" not found in session");
		}
		return v;
	}

	@Override
	public Object getSession(String key, Object defaultValue) {
		HttpSession httpSession = httpRequest.getSession(false);
		if (httpSession == null) {
			return defaultValue;
		}
		Object v = httpSession.getAttribute(key);
		if (v == null) {
			return defaultValue;
		}
		return v;
	}

	@Override
	public void removeSession(String key) {
		HttpSession httpSession = httpRequest.getSession(false);
		if (httpSession != null) {
			httpSession.removeAttribute(key);
		}
	}

	@Override
	public void killSession() {
		HttpSession httpSession = httpRequest.getSession(false);
		if (httpSession != null) {
			httpSession.invalidate();
		}
	}

	// cookies

	@Override
	public String getCookieValue(String cookieName) {
		String value = getCookieValue(cookieName, null);
		if( value == null ) {
			throw new RuntimeException("cookie name '"+cookieName+"' not found");
		}
		return value;
	}

	@Override
	public String getCookieValue(String cookieName, String defaultValue) {
		if (httpRequest.getCookies() != null) {
			for (Cookie c : httpRequest.getCookies()) {
				if (c.getName().equals(cookieName)) {
					return c.getValue();
				}
			}
		}
		return defaultValue;
	}

	@Override
	public void addCookie(String name, String value, int maxAgeSeconds, boolean secure) {
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(maxAgeSeconds);
		cookie.setSecure(secure);
		addedCookies.add(cookie);
	}

	@Override
	public List<Cookie> getAddedCookies() {
		return addedCookies;
	}

	// responses

	private void resetResponses() {
		template = null;
		json = null;
		contentBytes = null;
		contentType = null;
		downloadFile = null;
		downloadName = null;
		downloadContentType = null;
		downloadDeleteAfterDownload = false;
		redirect = null;
		errorStatus = 0;
		errorMessage = null;
	}

	@Override
	public boolean hasResponse() {
		return template != null || json != null || contentBytes != null || downloadFile != null || redirect != null || errorStatus != 0;
	}

	// model

	@Override
	public void putModel(String key, Object value) {
		model.put(key, value);
	}

	@Override
	public Object getModel(String key) {
		Object v = model.get(key);
		if (v == null) {
			throw new RuntimeException("value \"" + key + "\" not found in model");
		}
		return v;
	}

	@Override
	public Object getModel(String key, Object defaultValue) {
		Object v = model.get(key);
		if (v == null) {
			return defaultValue;
		}
		return v;
	}

	@Override
	public Map<String, Object> getModel() {
		return model;
	}

	// template response

	@Override
	public String getTemplate() {
		return template;
	}

	@Override
	public void setTemplate(String template) {
		resetResponses();
		this.template = template;
	}

	// json response

	@Override
	public String getJson() {
		return json;
	}

	@Override
	public void setJson(String json) {
		resetResponses();
		this.json = json;
	}

	// content response

	@Override
	public void setContent(byte[] contentBytes, String contentType) {
		resetResponses();
		this.contentBytes = contentBytes;
		this.contentType = contentType;
	}

	@Override
	public void setHtmlContent(String html) {
		resetResponses();
		this.contentBytes = html.getBytes(StandardCharsets.UTF_8);
		this.contentType = "text/html; charset=utf8";
	}

	@Override
	public void setTextContent(String text) {
		resetResponses();
		this.contentBytes = text.getBytes(StandardCharsets.UTF_8);
		this.contentType = "text/plain; charset=utf8";
	}

	@Override
	public byte[] getContentBytes() {
		return contentBytes;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	// download response

	@Override
	public void setDownload(File file, String name, String contentType, boolean deleteAfterDownload) {
		resetResponses();
		this.downloadFile = file;
		this.downloadName = name;
		this.downloadContentType = contentType;
		this.downloadDeleteAfterDownload = deleteAfterDownload;
	}

	@Override
	public File getDownloadFile() {
		return downloadFile;
	}

	@Override
	public String getDownloadName() {
		return downloadName;
	}

	@Override
	public String getDownloadContentType() {
		return downloadContentType;
	}

	@Override
	public boolean isDownloadDeleteAfterDownload() {
		return downloadDeleteAfterDownload;
	}

	// redirect response

	@Override
	public void setRedirect(String redirect) {
		resetResponses();
		this.redirect = redirect;
	}

	@Override
	public String getRedirect() {
		return redirect;
	}

	// error response

	@Override
	public void setError(int status, String message) {
		resetResponses();
		this.errorStatus = status;
		this.errorMessage = message;
	}

	@Override
	public int getErrorStatus() {
		return errorStatus;
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}

}
