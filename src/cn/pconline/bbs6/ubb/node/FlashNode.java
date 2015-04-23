package cn.pconline.bbs6.ubb.node;

import cn.pconline.bbs6.ubb.EscapeUtils;
import cn.pconline.bbs6.ubb.Node;

/**
 *
 * @author xhchen
 */
public class FlashNode extends AbstractNode {

	public FlashNode() {}

	private FlashNode(String name, String attribute) {
		super.name = name;
		super.attribute = attribute;
	}

	@Override
	public StringBuilder getText(Node parent) {
		StringBuilder url = text;
		String width = "488";
		String height = "422";

		if (attribute != null) {
			String[] wh = attribute.split("[x|\\,]");
			if (wh.length == 2) {
				width  = wh[0];
				height = wh[1];
			}
		}

		StringBuilder sb = new StringBuilder();
		sb.append("<object codebase=\"http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=7,0,19,0\" classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\" width=\"").append(width).append("\" height=\"").append(height).append("\">\n");
		sb.append("<param name=\"movie\" value=\"").append(url).append("\" />\n <param name=\"quality\" value=\"high\" />\n ");
		sb.append("<param name=\"allowScriptAccess\" value=\"never\" />\n ");
		sb.append("<param name=\"wmode\" value=\"opaque\" />\n ");
		sb.append("<param name=\"allowNetworking\" value=\"internal\" />\n ");
		sb.append("<embed wmode=\"opaque\" src=\"").append(url).append("\" quality=\"high\" pluginspage=\"http://www.macromedia.com/go/getflashplayer\" type=\"application/x-shockwave-flash\" width=\"").append(width).append("\" height=\"").append(height).append("\" ");
		sb.append("  allowscriptaccess=\"never\"></embed>\n");
		sb.append("</object>");
		return sb;
	}

	@Override
	public void appendText(CharSequence text) {
		EscapeUtils.appendAttribute(text, super.text);
	}

	@Override
	public Node copyWithoutText() {
		return new FlashNode(name, attribute);
	}

	@Override
	public Node build(String name, String attribute) {
		return new FlashNode(name, attribute);
	}

}
