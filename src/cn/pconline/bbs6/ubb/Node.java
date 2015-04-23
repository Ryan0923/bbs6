package cn.pconline.bbs6.ubb;

/**
 * UBB 节点对象接口
 * @author xhchen
 */
public interface Node {
	String getName();
	String getAttribute();
	StringBuilder getText(Node parent);
	StringBuilder getText();
	void appendText(CharSequence text);
	void appendNode(Node node);
	Node copyWithoutText();
	Node build(String name, String attribute);
}
