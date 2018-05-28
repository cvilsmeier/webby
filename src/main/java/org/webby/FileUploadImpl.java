package org.webby;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.Part;

public class FileUploadImpl implements FileUpload {

	private final Part part;

	public FileUploadImpl(Part part) {
		super();
		this.part = part;
	}
	
	@Override
	public String getName() {
		return part.getName();
	}
	
	@Override
	public String getContentType() {
		return part.getContentType();
	}
	
	@Override
	public String getSubmittedFileName() {
		return part.getSubmittedFileName();
	}
	
	@Override
	public long getSize() {
		return part.getSize();
	}
	
	@Override
	public InputStream getInputStream() throws IOException {
		return part.getInputStream();
	}
}
