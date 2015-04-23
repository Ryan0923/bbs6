package cn.pconline.bbs6.ubb.node;

import cn.pconline.bbs6.ubb.EscapeUtils;
import cn.pconline.bbs6.ubb.Node;

/**
 *
 * @author xhchen
 */
public class TrNode extends AbstractNode {

	public TrNode() {}

	private TrNode(String name, String attribute) {
		super.name = name;
		super.attribute = attribute;
	}

	@Override
	public StringBuilder getText(Node parent) {
		StringBuilder sb = new StringBuilder();

		sb.append("<tr class=\"wysiwygTr\"");
		if (attribute != null) {
			sb.append(" style=\"background:");
			EscapeUtils.appendAttribute(attribute, sb);
			sb.append(";\"");
		}
		sb.append('>').append(text).append("</tr>");

		return sb;
	}

	@Override
	public void appendText(CharSequence text) {
	}

	@Override
	public Node copyWithoutText() {
		return new TrNode(name, attribute);
	}

	@Override
	public Node build(String name, String attribute) {
		return new TrNode(name, attribute);
	}

}
