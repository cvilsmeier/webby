package org.webby;

import java.io.IOException;
import java.io.InputStream;

/**
 * A FileUpload represents an uploaded file.
 */
public interface FileUpload {

	/**
	 * The name of the HTML form field.
	 */
	String getName();

	/**
	 * The content type, e.g. "application/octet-stream".
	 */
	String getContentType();

	/**
	 * The original name of the uploaded file.
	 */
	String getSubmittedFileName();

	/**
	 * The size of the upload in bytes.
	 */
	long getSize();

	/**
	 * Opens an InputStream to read the binary data or the upload. 
	 * Once the caller is done, it must close the InputStream.
	 */
	InputStream getInputStream() throws IOException;
}
