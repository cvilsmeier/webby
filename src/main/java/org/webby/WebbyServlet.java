package org.webby;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebbyServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private App app = null;
	private VelocityEngine velocityEngine = null;

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		try {
			// app
			ServletContext servletContext = servletConfig.getServletContext();
			AppContextImpl appContext = new AppContextImpl(servletContext);
			String appClass = servletConfig.getInitParameter("appClass");
			Constructor<?> ctor = Class.forName(appClass).getConstructor(AppContext.class);
			this.app = (App) ctor.newInstance(appContext);
			// velocity
			String templatePath = getServletContext().getRealPath("/WEB-INF/templates");
			Properties velocityProperties = new Properties();
			velocityProperties.setProperty("resource.loader", "file");
			velocityProperties.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
			velocityProperties.setProperty("file.resource.loader.path", templatePath);
			String velocityCache = servletConfig.getInitParameter("velocityCache");
			if ( velocityCache == null ) {
				velocityCache = servletContext.getInitParameter("velocityCache");
			}
			velocityCache = "true".equals(velocityCache) ? "true" : "false";
			velocityProperties.setProperty("file.resource.loader.cache", velocityCache);
			velocityProperties.setProperty("input.encoding", "UTF-8");
			velocityProperties.setProperty("output.encoding", "UTF-8");
			this.velocityEngine = new VelocityEngine(velocityProperties);
		} catch (Exception ex) {
			throw new ServletException("init error: " + ex, ex);
		}
	}

	@Override
	public void destroy() {
		super.destroy();
		app.destroy();
	}

	@Override
	protected void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException {
		doGetOrPost("GET", httpRequest, httpResponse);
	}

	@Override
	protected void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException {
		doGetOrPost("POST", httpRequest, httpResponse);
	}

	private void doGetOrPost(String method, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException {
		httpRequest.setCharacterEncoding("UTF-8");
		String path = httpRequest.getPathInfo();
		if (logger.isTraceEnabled()) {
			logger.trace(method + " " + path);
		}
		if (path == null) {
			path = "";
		}
		// serve request
		long t1 = System.nanoTime();
		WebRequestImpl req = new WebRequestImpl(method, path, httpRequest);
		try {
			app.serve(req);
		} catch (Exception ex) {
			logger.warn(method + " " + path + " threw exception: " + ex.getMessage(), ex);
			req.setError(500, ex.toString());
		}
		// add cookies, if any
		for (Cookie addedCookie : req.getAddedCookies()) {
			httpResponse.addCookie(addedCookie);
		}
		// send response
		if (req.getTemplate() != null) {
			VelocityContext ctx = new VelocityContext();
			ctx.put("req", req);
			for (String k : req.getModel().keySet()) {
				ctx.put(k, req.getModel().get(k));
			}
			httpResponse.setContentType("text/html; charset=utf-8");
			httpResponse.setCharacterEncoding("UTF-8");
			PrintWriter out = httpResponse.getWriter();
			velocityEngine.mergeTemplate(req.getTemplate(), "UTF-8", ctx, out);
			out.flush();
			httpResponse.flushBuffer();
		} else if (req.getJson() != null) {
			httpResponse.setContentType("application/json");
			httpResponse.setCharacterEncoding("UTF-8");
			ServletOutputStream out = httpResponse.getOutputStream();
			byte[] data = req.getJson().getBytes(StandardCharsets.UTF_8);
			out.write(data);
			out.flush();
			httpResponse.flushBuffer();
		} else if (req.getContentBytes() != null) {
			httpResponse.setContentType(req.getContentType());
			httpResponse.setCharacterEncoding("UTF-8");
			ServletOutputStream out = httpResponse.getOutputStream();
			out.write(req.getContentBytes());
			out.flush();
			httpResponse.flushBuffer();
		} else if (req.getDownloadFile() != null) {
			ServletOutputStream out = httpResponse.getOutputStream();
			File file = req.getDownloadFile();
			if (file.isFile()) {
				httpResponse.setContentType(req.getDownloadContentType());
				if (req.isDownloadAsAttachment()) {
					httpResponse.setHeader("Content-disposition", "attachment; filename=" + req.getDownloadName());
				}
				pipeFileToStream(file, out);
				if (req.isDownloadDeleteAfterDownload()) {
					boolean deleted = file.delete();
					if (!deleted) {
						logger.warn("could not delete download file " + file);
					}
				}
			} else {
				httpResponse.setStatus(404);
				String content = "status 404: download file not found: \"" + file.getName() + "\"\r\n";
				out.write(content.getBytes(StandardCharsets.UTF_8));
			}
			out.flush();
			httpResponse.flushBuffer();
		} else if (req.getRedirect() != null) {
			httpResponse.sendRedirect(req.getRedirect());
		} else if (req.getErrorStatus() != 0) {
			httpResponse.setStatus(req.getErrorStatus());
			String content = "status " + req.getErrorStatus() + ": " + req.getErrorMessage() + "\r\n";
			ServletOutputStream out = httpResponse.getOutputStream();
			out.write(content.getBytes(StandardCharsets.UTF_8));
			out.flush();
			httpResponse.flushBuffer();
		} else {
			int status = 404;
			httpResponse.setStatus(status);
			String content = "status " + status + ": path \"" + path + "\" did not produce any result\r\n";
			ServletOutputStream out = httpResponse.getOutputStream();
			out.write(content.getBytes(StandardCharsets.UTF_8));
			out.flush();
			httpResponse.flushBuffer();
		}
		// report duration of this request
		long t2 = System.nanoTime();
		long millis = (t2 - t1) / 1_000_000L;
		if (millis > 3000) {
			logger.info(method + " " + path + " took " + millis + " ms");
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug(method + " " + path + " took " + millis + " ms");
			}
		}
	}

	private void pipeFileToStream(File file, ServletOutputStream out) throws IOException {
		try (FileInputStream in = new FileInputStream(file)) {
			pipeStreamtoStream(in, out);
		}
	}

	private void pipeStreamtoStream(FileInputStream in, ServletOutputStream out) throws IOException {
		byte[] buf = new byte[1024];
		int c;
		while ((c = in.read(buf)) > 0) {
			out.write(buf, 0, c);
		}
	}

}
