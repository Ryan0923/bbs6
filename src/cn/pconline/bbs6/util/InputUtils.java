package cn.pconline.bbs6.util;

import javax.servlet.http.HttpServletRequest;

public class InputUtils {
	public static String filterInput(String html) {
		if (html == null) return html;
		StringBuilder sb = new StringBuilder(html.length());
		for (int i = 0, c = html.length(); i < c; ++i) {
			char ch = html.charAt(i);
			switch (ch) {
			case '>':
				sb.append("&gt;"); break;
			case '<':
				sb.append("&lt;"); break;
			case '&':
				sb.append("&amp;"); break;
			case '"':
				sb.append("&quot;"); break;
			case '\'':
				sb.append("&#039"); break;
			default:
				sb.append(ch); break;
			}
		}
		return sb.toString();
	}

	public static boolean validate(HttpServletRequest request) {
		if (!request.getMethod().equalsIgnoreCase("POST")) {
			return false;
		}
		return true;
	}

}
