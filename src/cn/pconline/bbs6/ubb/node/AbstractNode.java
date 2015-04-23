package cn.pconline.bbs6.ubb.node;

import cn.pconline.bbs6.ubb.Node;

/**
 *
 * @author xhchen
 */
public abstract class AbstractNode implements Node {
	String name;
	String attribute;
	StringBuilder text = new StringBuilder();

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getAttribute() {
		return attribute;
	}

	@Override
	public void appendNode(Node node) {
		text.append(node.getText(this));
	}

	@Override
	public StringBuilder getText() {
		return getText(null);
	}

}
