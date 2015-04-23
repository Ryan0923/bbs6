/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.pconline.bbs6.ubb.node;

import cn.pconline.bbs6.ubb.Node;

/**
 *
 * @author xhchen
 */
public class TableNode extends AbstractNode {

	public TableNode() {}

	private TableNode(String name, String attribute) {
		super.name = name;
		super.attribute = attribute;
	}

	@Override
	public StringBuilder getText(Node parent) {
		StringBuilder sb = new StringBuilder();
		sb.append("<table class=\"wysiwygTable\"");

		if (attribute != null) {
			String[] wb = attribute.split(",");
			String width = wb[0];
			String bgcolor = null;
			if (wb.length > 1) {
				bgcolor = wb[1];
			}
			int w = 0;
			try {
				if (width.endsWith("%")) {
					w = Integer.parseInt(width.substring(0, width.length() - 1));
					if (w > 98) { width = "98%"; }
				} else {
					w = Integer.parseInt(width);
					if (w > 560) { width = "560"; }
				}
			} catch (Exception ex) {
				width = null;
			}
			if (width != null) {
				sb.append(" width=\"").append(width).append('"');
			}

			sb.append(" style=\"");

			if (bgcolor != null) {
				sb.append("background:").append(bgcolor).append(";");
			}
			if (width != null) {
				sb.append("width:").append(width.endsWith("%") ? width : (width + "px")).append(";");
			}
			sb.append('"');
		}

		sb.append('>');

		sb.append(text);

		sb.append("</table>");

		return sb;
	}

	@Override
	public void appendText(CharSequence text) {
	}

	@Override
	public Node copyWithoutText() {
		return new TableNode(name, attribute);
	}

	@Override
	public Node build(String name, String attribute) {
		return new TableNode(name, attribute);
	}

}
