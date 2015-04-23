package cn.pconline.bbs6.ubb.node;

import cn.pconline.bbs6.ubb.EscapeUtils;
import cn.pconline.bbs6.ubb.Node;

/**
 *
 * @author xhchen
 */
public class RmNode extends AbstractNode {

	public RmNode() {}

	private RmNode(String name, String attribute) {
		super.name = name;
		super.attribute = attribute;
	}

	@Override
	public StringBuilder getText(Node parent) {
		StringBuilder sb = new StringBuilder();
		StringBuilder url = text;
		String width = "400";
		String height = "150";

		if (attribute != null) {
			String[] wh = attribute.split("[x|\\,]");
			if (wh.length == 2) {
				width  = wh[0];
				height = wh[1];
			}
		}

		sb.append("<div><OBJECT classid=clsid:CFCDAA03-8BE4-11cf-B84B-0020AFBBCCFA class=OBJECT id=RAOCX width=\"").append(width).append("\" height=\"").append(height).append("\"><PARAM NAME=SRC VALUE=").append(url).append("><PARAM NAME=CONSOLE VALUE=Clip1><PARAM NAME=CONTROLS VALUE=imagewindow><PARAM NAME=AUTOSTART VALUE=false></OBJECT><br/>");
		sb.append("<OBJECT classid=CLSID:CFCDAA03-8BE4-11CF-B84B-0020AFBBCCFA height=32 id=video2 width=\"").append(width).append("\"><PARAM NAME=SRC VALUE=").append(url).append("><PARAM NAME=AUTOSTART VALUE=false><PARAM NAME=CONTROLS VALUE=controlpanel><PARAM NAME=CONSOLE VALUE=Clip1></OBJECT></div>");

		return sb;
	}

	@Override
	public void appendText(CharSequence text) {
		EscapeUtils.appendAttribute(text, super.text);
	}

	@Override
	public Node copyWithoutText() {
		return new RmNode(name, attribute);
	}

	@Override
	public Node build(String name, String attribute) {
		return new RmNode(name, attribute);
	}

}
