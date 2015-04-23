package cn.pconline.bbs6.ubb.node;

import cn.pconline.bbs6.ubb.EscapeUtils;
import cn.pconline.bbs6.ubb.Node;

/**
 *
 * @author xhchen
 */
public class TdNode extends AbstractNode {

	public TdNode() {}

	private TdNode(String name, String attribute) {
		super.name = name;
		super.attribute = attribute;
	}

	@Override
	public StringBuilder getText(Node parent) {
		StringBuilder sb = new StringBuilder();
		StringBuilder text = super.text;

		boolean allWhiteSpace = true;
		for (int i = 0, c = text.length(); i < c; ++i ) {
			if (!Character.isWhitespace(text.charAt(i))) {
				allWhiteSpace = false;
				break;
			}
		}

		sb.append("<td class=\"wysiwygTd\"");

		if (attribute != null) {
			String[] items = attribute.split(",");
			if (!items[0].isEmpty()) {
				sb.append(" colspan=\"").append(items[0]).append('"');
			}
			if (items.length > 1) {
				if (!items[1].isEmpty()) {
					sb.append(" rowspan=\"").append(items[1]).append('"');
				}
			}
			if (items.length > 2) {
				if (!items[2].isEmpty()) {
					sb.append(" width=\"").append(items[2]).append('"');
				}
			}
		}

		sb.append('>').append(allWhiteSpace ? "&nbsp;" : text).append("</td>");
		return sb;
	}

	@Override
	public void appendText(CharSequence text) {
		EscapeUtils.appendHtml(text, super.text);
	}

	@Override
	public Node copyWithoutText() {
		return new TdNode(name, attribute);
	}

	@Override
	public Node build(String name, String attribute) {
		return new TdNode(name, attribute);
	}

}
