package cn.pconline.bbs6.ubb.node;

import cn.pconline.bbs6.ubb.EscapeUtils;
import cn.pconline.bbs6.ubb.Node;

/**
 *
 * @author xhchen
 */
public class FontNode extends AbstractNode {

	public FontNode() {}

	public FontNode(String name, String attribute) {
		super.name = name;
		super.attribute = attribute;
	}

	@Override
	public StringBuilder getText(Node parent) {
		StringBuilder sb = new StringBuilder();

		sb.append("<font ");
		if (name.equals("font")) {
			sb.append("face");
		} else {
			sb.append(name);
		}
		sb.append("=\"");
		if (name.toLowerCase().equals("size")) {
			if (attribute != null) {
				if (attribute.length() == 1 || attribute.length() == 2) {
					boolean allNumbers = true;
					for (int i = 0, c = attribute.length(); i < c; ++i) {
						if (attribute.charAt(i) > '9' || attribute.charAt(i) < '0') {
							allNumbers = false;
							break;
						}
					}
					if (allNumbers) {
						EscapeUtils.appendAttribute(attribute, sb);
					}
				}
			}
		} else {
			EscapeUtils.appendAttribute(attribute, sb);
		}
		sb.append("\">").
				append(text).
				append("</font>");

		return sb;
	}

	@Override
	public void appendText(CharSequence text) {
		EscapeUtils.appendHtml(text, super.text);
	}

	@Override
	public Node copyWithoutText() {
		return new FontNode(name, attribute);
	}

	@Override
	public Node build(String name, String attribute) {
		return new FontNode(name, attribute);
	}

}
