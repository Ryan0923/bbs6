package cn.pconline.bbs6.ubb.node;

import cn.pconline.bbs6.ubb.EscapeUtils;
import cn.pconline.bbs6.ubb.Node;

/**
 *
 * @author xhchen
 */
public class UrlNode extends AbstractNode {

	public UrlNode() {}

	private UrlNode(String name, String attribute) {
		super.name = name;
		super.attribute = attribute;
	}

	@Override
	public StringBuilder getText(Node parent) {
		StringBuilder sb = new StringBuilder();

		sb.append("<a target=\"_blank\" href=\"");
		if (attribute != null) {
			EscapeUtils.appendAttribute(attribute, sb);
		} else {
			sb.append(text);
		}
		sb.append("\">").append(text).append("</a>");

		return sb;
	}

	@Override
	public void appendText(CharSequence text) {
		if (attribute == null) {
			EscapeUtils.appendAttribute(text, super.text);
		} else {
			EscapeUtils.appendHtml(text, super.text);
		}
	}

	@Override
	public Node copyWithoutText() {
		return new UrlNode(name, attribute);
	}

	@Override
	public Node build(String name, String attribute) {
		return new UrlNode(name, attribute);
	}

}
