package cn.pconline.bbs6.ubb.node;

import cn.pconline.bbs6.ubb.EscapeUtils;
import cn.pconline.bbs6.ubb.Node;

/**
 *
 * @author xhchen
 */
public class CodeNode extends AbstractNode {

	public CodeNode() {}

	private CodeNode(String name, String attribute) {
		super.name = name;
		super.attribute = attribute;
	}

	@Override
	public StringBuilder getText(Node parent) {
		StringBuilder sb = new StringBuilder();
		sb.append("<blockquote>\n<table border=\"0\" cellspacing=\"0\" cellpadding=\"6\" bgcolor=\"#DDDDDD\">\n<tr>\n<td>\n<b>以下内容为程序代码:</b><br/>\n");
		sb.append(text);
		sb.append("\n</td>\n</tr>\n</table>\n</blockquote>\n");
		return sb;
	}

	@Override
	public void appendText(CharSequence text) {
		EscapeUtils.appendHtml(text, super.text);
	}

	@Override
	public Node copyWithoutText() {
		return new CodeNode(name, attribute);
	}

	@Override
	public Node build(String name, String attribute) {
		return new CodeNode(name, attribute);
	}

}
