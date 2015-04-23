package cn.pconline.util.trim;

import java.io.*;
import javax.servlet.ServletOutputStream;

public class FilterServletOutputStream extends ServletOutputStream {
	private DataOutputStream stream;

	public FilterServletOutputStream(OutputStream outputstream) {
		stream = new DataOutputStream(outputstream);
	}

	@Override
	public void write(int i) throws IOException {
		stream.write(i);
	}

	@Override
	public void write(byte bytes[]) throws IOException {
		stream.write(bytes);
	}

	@Override
	public void write(byte bytes[], int i, int j) throws IOException {
		stream.write(bytes, i, j);
	}
}
