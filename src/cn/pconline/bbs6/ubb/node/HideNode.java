package cn.pconline.bbs6.ubb.node;

import cn.pconline.bbs6.ubb.EscapeUtils;
import cn.pconline.bbs6.ubb.Node;

/**
 *
 * @author xhchen
 */
public class HideNode extends AbstractNode {
	String contribution = "贡献值";

	public HideNode() {}

	private HideNode(String name, String attribute) {
		super.name = name;
		super.attribute = attribute;
	}

	public void setContribution(String contribution) {
		this.contribution = contribution;
	}

	@Override
	public StringBuilder getText(Node parent) {
		StringBuilder sb = new StringBuilder();

		int limit = -1;
		if (attribute != null) {
			try {
				limit = Integer.parseInt(attribute);
			} catch (Exception ex) {}
		}
		if ("hide".equals(name)) {
			sb.append("<div class=\"hidden_content\">***隐藏信息 ");
			if (limit > 0) {
				sb.append(contribution).append("大于").append(limit);
			} else {
				sb.append("回复后");
			}
			sb.append("才能显示***</div>");

		} else if ("_hide_".equals(name)) {
			sb.append("<div class=\"hidden_content_begin\">***以下是隐藏信息内容 ");
			if (limit > 0) {
				sb.append(contribution).append("大于").append(limit);
			} else {
				sb.append("回复后");
			}
			sb.append("才能显示***</div>");

			sb.append(text);

			sb.append("<div class=\"hidden_content_end\">以上是隐藏信息内容</div>");
		}

		return sb;
	}

	@Override
	public void appendText(CharSequence text) {
		EscapeUtils.appendHtml(text, super.text);
	}

	@Override
	public Node copyWithoutText() {
		return new HideNode(name, attribute);
	}

	@Override
	public Node build(String name, String attribute) {
		return new HideNode(name, attribute);
	}

}
