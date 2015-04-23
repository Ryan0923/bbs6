package cn.pconline.bbs6.util;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TryCatchFinally;

/**
 *
 * @author xhchen
 */
public class MemcachedTag extends BodyTagSupport implements TryCatchFinally {
	long time = 3600;
	String key;
	String content;

	public void setTime(long time) {
		this.time = time;
	}

	public void setKey(String key) {
		this.key = key;
	}

	private String getKey() {
		if (key != null) {
			return key;
		}
		return EnvUtils.getEnv().getRequest().getServletPath() + "?" + EnvUtils.getEnv().getRequest().getQueryString();
	}

	@Override
	public int doStartTag() throws JspException {
		content = (String)EnvUtils.getEnv().getCacheClient().get(getKey());

		if (content != null) {
			try {
				pageContext.getOut().write(content);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}

			return SKIP_BODY;
		}

		return EVAL_BODY_BUFFERED;
	}


	@Override
	public int doAfterBody() throws JspException {
		try {
			content = bodyContent.getString();
			EnvUtils.getEnv().getCacheClient().set(getKey(), content,
					new java.util.Date(System.currentTimeMillis() + time * 1000));

			bodyContent.clearBody();
			bodyContent.write(content);
			bodyContent.writeOut(bodyContent.getEnclosingWriter());
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		return SKIP_BODY;
	}

	@Override
	public void doCatch(Throwable ex) throws Throwable {
		throw ex;
	}

	@Override
	public void doFinally() {
		key = null;
		content = null;
	}

}
