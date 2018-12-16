package org.webby;

import java.io.InputStream;

import javax.servlet.ServletContext;

public class AppContextImpl implements AppContext {

	private final ServletContext servletContext;

	public AppContextImpl(ServletContext servletContext) {
		super();
		this.servletContext = servletContext;
	}

	@Override
	public String getContextPath() {
		return servletContext.getContextPath();
	}

	@Override
	public String getRealPath(String path) {
		return servletContext.getRealPath(path);
	}

	@Override
	public InputStream getResourceAsStream(String path) {
		return servletContext.getResourceAsStream(path);
	}

	@Override
	public String getInitParameter(String name) {
		return servletContext.getInitParameter(name);
	}

}
