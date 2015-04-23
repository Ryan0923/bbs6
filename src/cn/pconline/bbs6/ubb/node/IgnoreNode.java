package cn.pconline.bbs6.ubb.node;

import cn.pconline.bbs6.ubb.EscapeUtils;
import cn.pconline.bbs6.ubb.Node;

/**
 *
 * @author xhchen
 */
public class IgnoreNode extends AbstractNode {

	public IgnoreNode() {}

	private IgnoreNode(String name, String attribute) {
		super.name = name;
		super.attribute = attribute;
	}

	@Override
	public StringBuilder getText(Node parent) {
		return text;
	}

	@Override
	public void appendText(CharSequence text) {
		EscapeUtils.appendHtml(text, super.text);
	}

	@Override
	public Node copyWithoutText() {
		return new IgnoreNode(name, attribute);
	}

	@Override
	public Node build(String name, String attribute) {
		return new IgnoreNode(name, attribute);
	}

}
