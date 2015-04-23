package cn.pconline.bbs6.ubb;

import java.util.*;
import org.apache.commons.logging.*;

/**
 * 处理UBB解析的节点事件，将整段UBB转换成HTML的UBB解析事件监听器
 *
 * @author xhchen
 */
public class UBBHandler {
	static final Log LOG = LogFactory.getLog(UBBHandler.class);

	/**
	 * 待处理的UBB节点栈
	 */
	LinkedList<Node> stack;

	/**
	 * 处理解析开始事件
	 */
	public void startUBB() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Start UBB");
		}

		if (stack == null) {
			stack = new LinkedList<Node>();
		} else {
			stack.clear();
		}

		stack.push(new RootNode());
	}

	/**
	 * 处理解析结束事件
	 */
	public void endUBB() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("End UBB");
		}

		// 处理栈中需要关闭的标签
		if (stack.size() > 1) {
			for (int i = 0, c = stack.size() - 1; i < c; i ++) {
				Node node = stack.pop();
				stack.peek().appendNode(node);
			}
		}
	}

	/**
	 * 处理节点开始事件
	 * 
	 * @param name		节点名称
	 * @param attribute	节点属性，可能为 null
	 * @param pos		UBB源码中的当前字符位置，主要用于出错是提示
	 */
	public void startElement(String name, String attribute, int pos) {
		// 将[*]当成普通的文本来处理
		if (name.length() == 1 && name.charAt(0) == '*') {
			stack.peek().appendText("[*]");
			return;
		}

		if (builderMap.containsKey(name.toLowerCase())) {
			stack.push(buildNode(name, attribute));
		} else {
			// [em.*?]属于自动关闭的标签
			if (name.startsWith("em") && name.length() > 2) {
				stack.peek().appendNode(builderMap.get("emot").build(name, null));
			} else {
				StringBuilder sb = new StringBuilder();
				sb.append('[').append(name);
				if (attribute != null) sb.append('=').append(attribute);
				sb.append(']');
				stack.peek().appendText(sb);
			}
		}
	}

	/**
	 * 处理节点结束事件
	 * 需要处理节点结束不匹配节点开始的情况，处理原则为：
	 * 待定
	 * 
	 * @param name	关闭节点的名称
	 * @param pos	UBB源码中的当前字符位置，主要用于出错是提示	
	 */
	public void endElement(String name, int pos) {
		// 保证正常的栈结构
		if (stack.size() <= 1) {
			stack.peek().appendText("[/" + name + ']');
			return;
		}

		// 在栈中匹配标签开始
		int matched = -1;
		for (int i = 0; i < stack.size() - 1; i ++) {
			Node node = stack.get(i);
			if (node.getName().equals(name)) {
				matched = i;
				break;
			}
		}

		// 刚好匹配到栈顶
		if (matched == 0) {
			Node node = stack.pop();
			stack.peek().appendNode(node);
			return;
		}

		// 匹配到栈中的非顶部，模拟浏览器处理标签的方式进行处理
		if (matched > 0) {
			List<Node> nodeList = new ArrayList<Node>(matched);
			for (int i = 0; i < matched + 1; i ++) {
				Node n = stack.pop();
				stack.peek().appendNode(n);
				nodeList.add(0, n.copyWithoutText());
			}
			nodeList.remove(0);

			for (int i = 0; i < nodeList.size(); i ++) {
				stack.push(nodeList.get(i));
			}
		} else {
			// 匹配不到
			// 这里会将不能匹配的标签当成文本处理，实际浏览器处理html是会直接忽略这些不能匹配的标签
			stack.peek().appendText("[/" + name + ']');
		}

	}

	/**
	 * 处理节点文本内容事件，解析时文本部分可能通过多次事件完成
	 * 
	 * @param ubb
	 * @param start
	 * @param length
	 */
	public void text(String ubb, int start, int length) {
		stack.peek().appendText(ubb.substring(start, start + length));
	}

	/**
	 * 读取UBB转换为HTML的结果
	 * 
	 * @return
	 */
	public String getText() {

		if (stack.size() == 1) {
			return stack.getLast().getText().toString();
		}

		if (stack.isEmpty()) {
			throw new IllegalStateException("Stack is empty, some error when process UBB.");
		}

		if (stack.size() > 1) {
			throw new IllegalStateException("Stack size is " + stack.size() + " , some error when process UBB.");
		}

		return null;
	}


	Map<String, Node> builderMap;

	public void setBuilderMap(Map<String, Node> builderMap) {
		this.builderMap = builderMap;
	}
	
	public Node buildNode(String name, String attribute) {
		Node builder = builderMap.get(name.toLowerCase());
		return builder.build(name, attribute);
	}

}
