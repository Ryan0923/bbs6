package cn.pconline.bbs6.repository;

import java.util.List;

public class EncodeBuilder {
	StringBuilder sb = new StringBuilder();
	
	public EncodeBuilder append(Object name, String value) {
		if(value == null) return this;
		sb.append(name).append(':');
		sb.append('{').append(value.length()).append('}');
		sb.append(value).append(',');
		return this;
	}
	
	public EncodeBuilder append(Object name, long value) {
		sb.append(name).append(':').append(value).append(',');
		return this;
	}
	
	public EncodeBuilder append(Object name, int value) {
		sb.append(name).append(':').append(value).append(',');
		return this;
	}
	
	public EncodeBuilder append(Object name, boolean value) {
		sb.append(name).append(':').append(value).append(',');
		return this;
	}

	public EncodeBuilder append(Object name, List<Long> value) {
		if(value == null) return this;
		sb.append(name).append(":[");
		for (int i = 0, c = value.size(); i < c; ++i) {
			sb.append(value.get(i));
			if (i < c - 1) {
				sb.append(',');
			}
		}
		sb.append("],");
		return this;
	}

	public EncodeBuilder append(Object name, long[] value) {
		if(value == null) return this;
		sb.append(name).append(":[");
		for (int i = 0, c = value.length; i < c; ++i) {
			sb.append(value[i]);
			if (i < c - 1) {
				sb.append(',');
			}
		}
		sb.append("],");
		return this;
	}

	@Override
	public String toString() {
		if (sb.length() > 0) {
			return sb.substring(0, sb.length() - 1);
		}
		return sb.toString();
	}
	
}
