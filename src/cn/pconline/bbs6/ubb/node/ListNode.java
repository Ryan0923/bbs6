package cn.pconline.bbs6.ubb.node;

import cn.pconline.bbs6.ubb.EscapeUtils;
import cn.pconline.bbs6.ubb.Node;

/**
 *
 * @author xhchen
 */
public class ListNode extends AbstractNode {

	public ListNode() {}

	private ListNode(String name, String attribute) {
		super.name = name;
		super.attribute = attribute;
	}

	@Override
	public StringBuilder getText(Node parent) {
		StringBuilder sb = new StringBuilder();

		sb.append("<ul");
		if ("1".equals(attribute)) {
			sb.append(" class=\"wysiwygUl1\">");
		} else {
			sb.append(" class=\"wysiwygUl\">");
		}

		String[] items = text.toString().split("\\[\\*\\]");
		for (int i = 1; i < items.length; i ++) {
			sb.append("<li class=\"wysiwygLi\">").append(items[i]).append("</li>");
		}

		sb.append("</ul>");

		return sb;
	}

	@Override
	public void appendText(CharSequence text) {
		EscapeUtils.appendHtml(text, super.text);
	}

	@Override
	public Node copyWithoutText() {
		return new ListNode(name, attribute);
	}

	@Override
	public Node build(String name, String attribute) {
		return new ListNode(name, attribute);
	}

}
