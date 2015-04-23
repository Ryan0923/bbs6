package cn.pconline.bbs6.ubb.node;

import cn.pconline.bbs6.ubb.EscapeUtils;
import cn.pconline.bbs6.ubb.Node;

/**
 *
 * @author xhchen
 */
public class ImgNode extends AbstractNode {

	public ImgNode() {}

	private ImgNode(String name, String attribute) {
		super.name = name;
		super.attribute = attribute;
	}

	@Override
	public StringBuilder getText(Node parent) {
		StringBuilder sb = new StringBuilder();
		String url = null;
		String width = null;
		String height = null;

		if (attribute != null) {
			if (attribute.length() > 10 && attribute.charAt(0) == 'h') {
				url = attribute;
			} else {
				String[] wh = attribute.split("[x|\\,]");
				if (wh.length == 2) {
					width  = wh[0];
					height = wh[1];
				}
			}
		}

		if (url != null) {
			sb.append("<a href=\"");
			EscapeUtils.appendAttribute(url, sb);
			sb.append("\" target=\"_blank\">");
			sb.append("<img border=\"0\"");
			sb.append(" onload=\"aw_(this)\" onmouseenter=\"aw_(this)\"");
			sb.append(" src=\"").append(text).append("\"/>");
			sb.append("</a>");
		} else {
			sb.append("<img");

			if (parent != null && parent.getName().equals("url")) {
				// inside [url]
				sb.append(" border=\"0\"");
			} else {
				// outside [url]
				sb.append(" class=\"img_link\" onclick=\"vp_(this)\"");
			}

			if (width != null) {
				sb.append(" style=\"width:").append(width).append("px;");
				sb.append("height:").append(height).append("px;\"");
			}

			sb.append(" onload=\"aw_(this)\" onmouseenter=\"aw_(this)\"");
			sb.append(" src=\"").append(text).append("\"/>");
		}

		return sb;
	}

	@Override
	public void appendText(CharSequence text) {
		if (attribute == null) {
			EscapeUtils.appendAttribute(text, super.text);
		} else {
			EscapeUtils.appendHtml(text, super.text);
		}
	}

	@Override
	public Node copyWithoutText() {
		return new ImgNode(name, attribute);
	}

	@Override
	public Node build(String name, String attribute) {
		return new ImgNode(name, attribute);
	}

}
