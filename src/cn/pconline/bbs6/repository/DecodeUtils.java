package cn.pconline.bbs6.repository;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;

public class DecodeUtils {
	public static enum Type {
		NUMBER,
		STRING,
		ARRAY
	}
	public static interface ItemHandler {
		public void handle(String name, String value, Type type);
	}
	
	public static void decode(String text, ItemHandler handler) {
		if (text == null || text.trim().equals("")) {
			return;
		}
		
		int pos = 0;
		int pos2 = 0;
		while (pos < text.length()) {
			pos = text.indexOf(':', pos2);
			if (pos == -1) {
				break;
			}
			String name = text.substring(pos2, pos);
			String value = null;
			Type type = null;
			char nextChar = text.charAt(pos + 1);
			if (nextChar == '{') {
				pos2 = text.indexOf('}', pos);
				int len = Integer.parseInt(text.substring(pos + 2, pos2));
				pos = pos2 + len + 1;
				value = text.substring(pos2 + 1, pos);
				pos ++;
				pos2 = pos;
				type = Type.STRING;
			} else if (nextChar == '[') {
				pos2 = text.indexOf(']', pos);
				value = text.substring(pos + 2, pos2);
				pos2 += 2;
				pos = pos2;
				type = Type.ARRAY;
			} else {
				pos2 = text.indexOf(',', pos);
				if (pos2 == -1) {
					pos2 = text.length();
				}
				value = text.substring(pos + 1, pos2);
				pos2 ++;
				pos = pos2;
				type = Type.NUMBER;
			}
			
			handler.handle(name, value, type);
		}
	}

	static final long[] EMPTY_LONG_ARRAY = new long[0];

	public static long[] str2LongArray(String value) {
		if (StringUtils.isEmpty(value)) {
			return EMPTY_LONG_ARRAY;
		}
		String[] sa = value.split(",");
		long[] result = new long[sa.length];
		for (int i = 0; i < sa.length; i ++) {
			result[i] = Long.parseLong(sa[i]);
		}
		return result;
	}

	public static String longArray2str(long[] ids){
		StringBuilder builder = new StringBuilder();
		for (int i = 0, c = ids.length, d = c - 1; i < c; ++i) {
			builder.append(ids[i]);
			if (i < d) {
				builder.append(',');
			}
		}
		return builder.toString();
	}

	public static String encodeLongArray(long[] ids) {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (int i = 0, c = ids.length, d = c - 1; i < c; ++i) {
			sb.append(ids[i]);
			if (i < d) {
				sb.append(',');
			}
		}
		sb.append(']');
		return sb.toString();
	}

	public static long[] decodeLongArray(String value) {
		if (value.length() == 2) return EMPTY_LONG_ARRAY;
		int pos = 1;
		List<Long> list = new ArrayList<Long>();
		while (true) {
			int pos2 = value.indexOf(',', pos);
			if (pos2 < 0) {
				list.add(Long.valueOf(value.substring(pos, value.length() - 1)));
				break;
			}
			list.add(Long.valueOf(value.substring(pos, pos2)));
			pos = pos2 + 1;
		}
		long[] result = new long[list.size()];
		for (int i = 0, c = result.length; i < c; ++i)
			result[i] = list.get(i);
		return result;

	}

}
