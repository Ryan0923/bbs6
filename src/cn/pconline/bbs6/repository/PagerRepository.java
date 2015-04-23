package cn.pconline.bbs6.repository;

import cn.pconline.bbs6.cache.CacheClient;
import cn.pconline.bbs6.domain.Pager;
import cn.pconline.bbs6.repository.DecodeUtils.Type;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author xhchen
 */
public abstract class PagerRepository <T> {

	@Autowired
	CacheClient cacheClient;

	public Pager<T> get(String key) {
		String value = (String)cacheClient.get(key);
		if (value == null) {
			return null;
		}
		return decode(value);
	}

	public void set(String key, Pager<T> pager) {
		cacheClient.set(key, encode(pager));
	}

	public void delete(String key) {
		cacheClient.delete(key);
	}

	Pager<T> decode(String value) {
		PageDecodeHandler handler = new PageDecodeHandler();
		DecodeUtils.decode(value, handler);
		return handler.getPager();
	}

	static enum Field {
		pageSize,
		pageNo,
		total,
		resultList
	}

	class PageDecodeHandler implements DecodeUtils.ItemHandler {
		Pager<T> pager = new Pager<T>();
		public Pager<T> getPager() {return pager;}

		@Override
		public void handle(String name, String value, Type type) {
			Field f = Field.valueOf(name);
			switch (f) {
				case pageSize: pager.setPageSize(Integer.parseInt(value)); break;
				case pageNo: pager.setPageNo(Integer.parseInt(value)); break;
				case total: pager.setTotal(Integer.parseInt(value)); break;
				case resultList: pager.setResultList(decodeResultList(value)); break;
				default: break;
			}
		}
	}

	String encode(Pager<T> pager) {
		EncodeBuilder eb = new EncodeBuilder();
		eb.append(Field.pageSize, pager.getPageSize());
		eb.append(Field.pageNo, pager.getPageNo());
		eb.append(Field.total, pager.getTotal());
		eb.append(Field.resultList, encodeResultList(pager.getResultList()));
		return eb.toString();
	}

	abstract protected String encodeResultList(List<T> resultList);
	abstract protected List<T> decodeResultList(String value);

}
