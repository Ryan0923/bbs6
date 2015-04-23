package cn.pconline.bbs6.ubb.node;

import cn.pconline.bbs6.ubb.EscapeUtils;
import cn.pconline.bbs6.ubb.Node;

/**
 *
 * @author xhchen
 */
public class SimpleNode extends AbstractNode {

	public SimpleNode() { }

	public SimpleNode(String name, String attribute) {
		this.name = name;
		this.attribute = attribute;
	}

	@Override
	public StringBuilder getText(Node parent) {
		StringBuilder sb = new StringBuilder();
		sb.append("<").append(name);
		if (attribute != null) {
			sb.append('=');
			EscapeUtils.appendAttribute(attribute, sb);
		}
		sb.append('>').append(text).append("</").append(name).append('>');
		return sb;
	}

	@Override
	public void appendText(CharSequence text) {
		EscapeUtils.appendHtml(text, super.text);
	}

	@Override
	public Node copyWithoutText() {
		return new SimpleNode(name, attribute);
	}

	@Override
	public Node build(String name, String attribute) {
		return new SimpleNode(name, attribute);
	}

}
