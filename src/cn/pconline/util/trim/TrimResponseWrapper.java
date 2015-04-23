package cn.pconline.util.trim;

import java.io.*;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class TrimResponseWrapper extends HttpServletResponseWrapper {

	private ByteArrayOutputStream output;
	private PrintWriter writer;
	private HttpServletResponse origResponse;
	private int contentLength;
	private String contentType;

	public TrimResponseWrapper(HttpServletResponse httpservletresponse) {
		super(httpservletresponse);
		output = null;
		writer = null;
		output = new ByteArrayOutputStream();
		origResponse = httpservletresponse;
	}

	public byte[] getData() {
		try {
			finishResponse();
		} catch (Exception exception) {
		}
		return output.toByteArray();
	}

	@Override
	public ServletOutputStream getOutputStream() {
		return new FilterServletOutputStream(output);
	}

	public ServletOutputStream createOutputStream() throws IOException {
		return new FilterServletOutputStream(output);
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if (writer != null) {
			return writer;
		} else {
			ServletOutputStream servletoutputstream = getOutputStream();
			writer = new PrintWriter(new OutputStreamWriter(servletoutputstream, origResponse.getCharacterEncoding()));
			return writer;
		}
	}

	public void finishResponse() {
		try {
			if (writer != null) {
				writer.close();
			} else if (output != null) {
				output.flush();
				output.close();
			}
		} catch (IOException ioexception) {
		}
	}

	@Override
	public void flushBuffer() throws IOException {
		output.flush();
	}

	@Override
	public void setContentLength(int cl) {
		contentLength = cl;
		super.setContentLength(cl);
	}

	public int getContentLength() {
		return contentLength;
	}

	@Override
	public void setContentType(String ct) {
		contentType = ct;
		super.setContentType(ct);
	}

	@Override
	public String getContentType() {
		return contentType;
	}
}
