package cn.pconline.bbs6.util;

import cn.pconline.bbs6.ubb.*;
import cn.pconline.bbs6.ubb.node.*;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author xhchen
 */
public class UBB2 {
	static Map<String, Node> builderMap = new HashMap<String, Node>();
	

	public void setEmotRoot(String emotRoot) {

		EmotNode emotNode = new EmotNode();
		emotNode.setEmotRoot(emotRoot);
		builderMap.put("emot", emotNode);

		builderMap.put("img", new ImgNode());
		builderMap.put("url", new UrlNode());
		builderMap.put("email", new EmailNode());

		builderMap.put("quote", new QuoteNode());
		builderMap.put("code", new CodeNode());

		HideNode hideNodeFormatter = new HideNode();
		builderMap.put("hide", hideNodeFormatter);
		builderMap.put("_hide_", hideNodeFormatter);

		SimpleNode simpleNodeFormatter = new SimpleNode();
		builderMap.put("b", simpleNodeFormatter);
		builderMap.put("i", simpleNodeFormatter);
		builderMap.put("u", simpleNodeFormatter);

		builderMap.put("flash", new FlashNode());
		builderMap.put("rm", new RmNode());
		builderMap.put("wma", new WmaNode());
		builderMap.put("wmv", new WmvNode());

		builderMap.put("list", new ListNode());
		builderMap.put("table", new TableNode());
		builderMap.put("tr", new TrNode());
		builderMap.put("td", new TdNode());

		SimpleNode ignoreNodeFormatter = new SimpleNode();
		builderMap.put("fly", ignoreNodeFormatter);
		builderMap.put("move", ignoreNodeFormatter);
		builderMap.put("shadow", ignoreNodeFormatter);
		builderMap.put("glow", ignoreNodeFormatter);

		FontNode fontNodeFormatter = new FontNode();
		builderMap.put("font", fontNodeFormatter);
		builderMap.put("face", fontNodeFormatter);
		builderMap.put("color", fontNodeFormatter);
		builderMap.put("size", fontNodeFormatter);

		builderMap.put("align", new AlignNode());
		builderMap.put("center", new CenterNode());
	}

	public static String ubb2html(String ubb) {
		UBBHandler handler = new UBBHandler();
		handler.setBuilderMap(builderMap);

		UBBParser.parse(ubb, handler);

		return handler.getText();
	}
}
