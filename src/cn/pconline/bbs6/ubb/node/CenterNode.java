package cn.pconline.bbs6.ubb.node;

import cn.pconline.bbs6.ubb.EscapeUtils;
import cn.pconline.bbs6.ubb.Node;

/**
 *
 * @author xhchen
 */
public class CenterNode extends AbstractNode {

	public CenterNode() {}

	public CenterNode(String name, String attribute) {
		super.name = name;
		super.attribute = attribute;
	}

	@Override
	public StringBuilder getText(Node parent) {
		StringBuilder sb = new StringBuilder();
		sb.append("<div align=\"center\">").
				append(text).
				append("</div>");

		return sb;
	}

	@Override
	public void appendText(CharSequence text) {
		EscapeUtils.appendHtml(text, super.text);
	}

	@Override
	public Node copyWithoutText() {
		return new CenterNode(name, attribute);
	}

	@Override
	public Node build(String name, String attribute) {
		return new CenterNode(name, attribute);
	}

}
