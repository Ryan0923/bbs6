package cn.pconline.bbs6.ubb;

/**
 *
 * @author xhchen
 */
public class RootNode implements Node {
	StringBuilder text = new StringBuilder();
	@Override
	public String getName() {
		return "__ubb__";
	}

	@Override
	public String getAttribute() {
		return null;
	}

	@Override
	public StringBuilder getText(Node parent) {
		return text;
	}

	@Override
	public StringBuilder getText() {
		return text;
	}

	@Override
	public void appendText(CharSequence text) {
		EscapeUtils.appendHtml(text, this.text);
	}

	@Override
	public void appendNode(Node node) {
		this.text.append(node.getText());
	}

	@Override
	public Node copyWithoutText() {
		return null;
	}

	@Override
	public Node build(String name, String attribute) {
		return new RootNode();
	}

}
