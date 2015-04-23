package cn.pconline.bbs6.ubb;

/**
 *
 * @author xhchen
 */
public abstract class EscapeUtils {

	public static void appendHtml(CharSequence add, StringBuilder to) {
		if (add == null) {
			return;
		}

		for (int i = 0, c = add.length(); i < c; ++i) {
			char ch = add.charAt(i);
			switch (ch) {
				case ' ':
					to.append("&nbsp;");
					break;
				case '<':
					to.append("&lt;");
					break;
				case '>':
					to.append("&gt;");
					break;
				case '\n':
					to.append("<br/>");
					break;
				case '\r':
					// ignore
					break;
				default:
					to.append(ch);
					break;
			}
		}
	}

	public static void appendAttribute(CharSequence add, StringBuilder to) {
		if (add == null) {
			return;
		}

		for (int i = 0, c = add.length(); i < c; ++i) {
			char ch = add.charAt(i);
			switch (ch) {
				case '"':
					to.append("&quot;");
					break;
				case '\n':
					to.append("<br/>");
					break;
				case '\r':
					// ignore
					break;
				default:
					to.append(ch);
					break;
			}
		}

	}

}
