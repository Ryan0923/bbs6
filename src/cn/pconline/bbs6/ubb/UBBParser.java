package cn.pconline.bbs6.ubb;

/**
 * UBB解析器，扫描UBB代码并生成合适的事件来调用
 * @see UBBHandler 来转换UBB代码到HTML
 * 本解析器一次扫描就可以完成UBB的整个处理，以效率为主要设计目标
 *
 * @author xhchen
 *
 */
public final class UBBParser {
	static enum State {
		TEXT,		// 文本
		START,		// 可能开始元素
		END,		// 可能结束元素
	}

	/**
	 * Singleton
	 */
	private UBBParser() {}

	/**
	 * 解析过程的主方法，通过一次扫描完成整个处理过程
	 *
	 * @param ubb		要解析的UBB代码
	 * @param handler	使用的事件处理器
	 */
	public static void parse(String ubb, UBBHandler handler) {
		// 解析过程中的当前状态
		State state = State.TEXT;
		// 解析过程中的当前待决字符开始位置
		int start = 0;
		
		// 开始处理
		handler.startUBB();

		// 一个一个的扫描字符
		for (int i = 0, ubb_length = ubb.length(); i < ubb_length; ++ i) {
			char ch = ubb.charAt(i);
			switch (ch) {
				case '[':	// 处理节点开始或结束的标签开始
					if (i > start) { // 需要提交文本
						handler.text(ubb, start, i - start);
					}
					state = State.START;	// 重新开始标签
					start = i;				// 重置待决字符开始位置
					break;
				case ']':	// 处理节点开始或结束的标签完成
					switch (state) {
						case START:	// 节点开始标签完成
							String elem = ubb.substring(start + 1, i);
							int pos = elem.indexOf('=');
							if (pos > 0) {	// 节点有属性
								handler.startElement(elem.substring(0, pos), elem.substring(pos + 1), i);
							} else {		// 节点无属性
								handler.startElement(elem, null, i);
							}
							start = i + 1;
							state = State.TEXT;
							break;
						case END:	// 节点结束标签完成
							handler.endElement(ubb.substring(start + 1, i), i);
							start = i + 1;
							state = State.TEXT;
							break;
					}
					break;
				case '/':	// 处理结束标签开始
					if (state == State.START) {
						if (start == i - 1) { // 匹配 "[/"
							state = State.END;
							start = i;
						}
					}
					break;

			} // end switch ch
		
		} // end for

		// 处理节点外(或者说是根节点)的普通文本
		if (ubb.length() > start) {
			handler.text(ubb, start, ubb.length() - start);
		}

		// 处理完成
		handler.endUBB();
	}

}
