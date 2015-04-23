package cn.pconline.bbs6.util;

/**
 * 将字符串编码成16进制格式，用于在url上显示中文
 * @author xhchen
 */
abstract public class HexStringer {

	public static String encode(String input) {
		if (input == null || "".equals(input)) {
			return input;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0, c = input.length(); i < c; ++i) {
			int ch = input.charAt(i);
			sb.append('g').append(Integer.toHexString(ch));
		}
		return sb.toString();
	}

	public static String decode(String input) {
		if (input == null || "".equals(input)) {
			return input;
		}
		if (input.charAt(0) != 'g') {
			return input;
		}
		String[] chars = input.split("g");
		StringBuilder sb = new StringBuilder();
		for (int i = 1, c = chars.length; i < c; ++ i) {
			int ch = Integer.parseInt(chars[i], 16);
			sb.append((char)ch);
		}
		return sb.toString();
	}

}
