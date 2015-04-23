package cn.pconline.bbs6.ubb.node;

import cn.pconline.bbs6.ubb.Node;

/**
 *
 * @author xhchen
 */
public class EmotNode extends AbstractNode {

	String emotRoot;

	public EmotNode() {}

	private EmotNode(String name, String attribute) {
		super.name = name;
		super.attribute = attribute;
	}

	public void setEmotRoot(String emotRoot) {
		this.emotRoot = emotRoot;
	}

	@Override
	public StringBuilder getText(Node parent) {
		StringBuilder sb = new StringBuilder();
		sb.append("<img src=\"").append(emotRoot).append('/').append(name).append(".gif").append("\"/>");
		return sb;
	}

	@Override
	public void appendText(CharSequence text) {}

	@Override
	public Node copyWithoutText() {
		EmotNode result = new EmotNode(name, attribute);
		result.setEmotRoot(emotRoot);
		return result;
	}

	@Override
	public Node build(String name, String attribute) {
		EmotNode result = new EmotNode(name, attribute);
		result.setEmotRoot(emotRoot);
		return result;
	}

}
