package cn.pconline.bbs6.domain;

import org.apache.commons.logging.LogFactory;

/**
 *
 * @author xhchen
 */
public final class DataIntegrityException extends RuntimeException {
	static final org.apache.commons.logging.Log LOG = LogFactory.getLog(DataIntegrityException.class);

	public DataIntegrityException(String message, Throwable cause) {
		super(message, cause);
		LOG.info(message);
	}

	public DataIntegrityException(String message) {
		super(message);
		LOG.info(message);
	}

}
