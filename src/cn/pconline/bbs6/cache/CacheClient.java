package cn.pconline.bbs6.cache;

import java.util.Date;
import com.danga.MemCached.MemCachedClient;
import java.util.Map;

public class CacheClient {

	MemCachedClient mcc;

	public void setMemCachedClient(MemCachedClient mcc) {
		this.mcc = mcc;
	}

	public Object get(String key) {
		return mcc.get(key);
	}

	public Map<String, Object> getMulti(String[] keys) {
		return mcc.getMulti(keys);
	}

	public Object[] getMultiArray(String[] keys) {
		return mcc.getMultiArray(keys);
	}

	public Object[] getMultiArray(String[] keys, Integer[] hashCodes, boolean asString) {
		return mcc.getMultiArray(keys, hashCodes, asString);
	}

	public boolean set(String key, Object value) {
		return mcc.set(key, value);
	}

	public boolean delete(String key) {
		return mcc.delete(key);
	}

	public long addOrIncr(String key, long count) {
		return mcc.addOrIncr(key, count);
	}

	public long getCounter(String key) {
		return mcc.getCounter(key);
	}

	public boolean storeCounter(String key, long count) {
		return mcc.storeCounter(key, count);
	}

	public boolean set(String key, String value, Date expiryAt) {
		return mcc.set(key, value, expiryAt);
	}
}
