package cn.pconline.bbs6.ubb.node;

import cn.pconline.bbs6.ubb.EscapeUtils;
import cn.pconline.bbs6.ubb.Node;

/**
 *
 * @author xhchen
 */
public class WmaNode extends AbstractNode {

	public WmaNode() {}

	private WmaNode(String name, String attribute) {
		super.name = name;
		super.attribute = attribute;
	}

	@Override
	public StringBuilder getText(Node parent) {
		StringBuilder sb = new StringBuilder();
		String width = "400";
		String height = "64";

		if (attribute != null) {
			String[] wh = attribute.split("[x|\\,]");
			if (wh.length == 2) {
				width  = wh[0];
				height = wh[1];
			}
		}

		sb.append("<object width=\"").append(width).append("\" height=\"").append(height).append("\" classid=\"CLSID:6BF52A52-394A-11d3-B153-00C04F79FAA6\" align=\"center\" border=\"0\">");
		sb.append("<param name=\"AutoStart\" value=\"0\"><param name=\"Balance\" value=\"0\"><param name=\"enabled\" value=\"-1\"><param name=\"EnableContextMenu\" value=\"-1\"><param name=\"url\" value=\"").append(text).append("\"><param name=\"PlayCount\" value=\"3\"><param name=\"rate\" value=\"1\"><param name=\"currentPosition\" value=\"0\"><param name=\"invokeURLs\" value=\"0\"><param name=\"baseURL\" value=\"\"><param name=\"stretchToFit\" value=\"0\"><param name=\"volume\" value=\"100\"><param name=\"mute\" value=\"0\"><param name=\"uiMode\" value=\"full\"><param name=\"windowlessVideo\" value=\"-1\"><param name=\"fullScreen\" value=\"0\"><param name=\"enableErrorDialogs\" value=\"-1\">");
		sb.append("</object>");

		return sb;
	}

	@Override
	public void appendText(CharSequence text) {
		EscapeUtils.appendAttribute(text, super.text);
	}

	@Override
	public Node copyWithoutText() {
		return new WmaNode(name, attribute);
	}

	@Override
	public Node build(String name, String attribute) {
		return new WmaNode(name, attribute);
	}

}
