package cn.pconline.bbs6.repository;

import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import cn.pconline.bbs6.cache.CacheClient;
import cn.pconline.bbs6.util.Env;
import cn.pconline.bbs6.util.EnvUtils;
import java.util.Map;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

public abstract class AbstractRepository <T> {
	private Class<T> type;
	
	@Autowired
	CacheClient cacheClient;
	
	@Autowired
	SimpleJdbcTemplate simpleJdbcTemplate;

	@Autowired
	SqlPageBuilder sqlPageBuilder;
	
	@Autowired
	IdGenerator idGenerator;
	
	protected SimpleJdbcCall getSimpleJdbcCall() {
		// ! 注意，必须每次构造一个新的对象
		return new SimpleJdbcCall((JdbcTemplate)simpleJdbcTemplate.getJdbcOperations());
	}

	public static ParameterizedRowMapper<Long> idRowMapper = new ParameterizedRowMapper<Long> () {
		@Override
		public Long mapRow(ResultSet rs, int index) throws SQLException {
			return rs.getLong(1);
		}
	};
	
	public List<T> page(String sql, int pageNo, int pageSize, Object ...args) {
		return buildListBatch(simpleJdbcTemplate.query(
				sqlPageBuilder.buildPageSql(sql, pageNo, pageSize), 
				idRowMapper, args));
	}
	
	public List<T> page(String sql, int pageNo, int pageSize, Map params) {
		return buildListBatch(simpleJdbcTemplate.query(
				sqlPageBuilder.buildPageSql(sql, pageNo, pageSize),
				idRowMapper, params));
	}

	public List<T> list(String sql, Object ...args) {
		return buildListBatch(simpleJdbcTemplate.query(sql,idRowMapper,args));
	}

	public List<T> list(String sql, Map params) {
		return buildListBatch(simpleJdbcTemplate.query(sql,idRowMapper,params));
	}

	public int count(String sql , Object ...args) {
		return simpleJdbcTemplate.queryForInt(sql, args);
	}
	
	public int count(String sql , Map params) {
		return simpleJdbcTemplate.queryForInt(sql, params);
	}

	public List<T> buildList(List<Long> idList) {
		List<T> result = new ArrayList<T>(idList.size());
		for (Long id : idList) {
			result.add(find(id));
		}
		return result;
	}

	public List<T> buildListBatch(List<Long> idList) {
		Env env = EnvUtils.getEnv();

		List<T> result = new ArrayList<T>(idList.size());
		String[] keys = new String[idList.size()];
		Object[] cached = batchGetCache(idList, keys, env);

		// 可能返回null
		if (cached == null) {
			cached = new String[idList.size()];
		}

		for (int i = 0; i < cached.length; i ++) {
			String obj = (String)cached[i];
			if (obj != null) {
				result.add(decode(obj));
			} else {
				result.add(find(idList.get(i)));
			}
		}

		return result;
	}

	public void preload(List<Long> idList) {
		Env env = EnvUtils.getEnv();

		String[] keys = new String[idList.size()];
		Object[] cached = batchGetCache(idList, keys, env);

		// 可能返回null
		if (cached == null) { return; }

		for (int i = 0; i < cached.length; i ++) {
			String obj = (String)cached[i];
			if (obj != null) {
				env.putObjectToCache(keys[i], decode(obj));
			}
		}
	}
	
	protected AbstractRepository(Class<T> type) {
		this.type = type;
	}
	
	public T find(long id) {
		T obj = findFromCache(id);
		if (obj == null) {
			obj = findFromDb(id);
			putToCache(obj);
		}
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	public T findFromCache(long id) {
		String key = type.getSimpleName() + "-" + id;
		Env env = EnvUtils.getEnv();

		if (enableFirstLevelCache()) {
			Object v = env.getObjectFromCache(key);
			if (v != null) return (T)v;
		}

		env.startCacheRead(key);
		try {
			String value = (String)cacheClient.get(key);
			if (value == null) {
				return null;
			}
			T obj = decode(value);
			if (enableFirstLevelCache()) {
				env.putObjectToCache(key, obj);
			}
			return obj;
		} finally {
			env.stopCacheOperation();
		}
	}
	
	public void putToCache(T obj) {
		String key = obj.getClass().getSimpleName() + "-" + getKey(obj);
		Env env = EnvUtils.getEnv();

		if (enableFirstLevelCache()) {
			env.putObjectToCache(key, obj);
		}

		env.startCacheUpdate(key);
		cacheClient.set(key, encode(obj));
		env.stopCacheOperation();
	}
	
	public void removeFromCache(T obj){
		String key = obj.getClass().getSimpleName() + "-" + getKey(obj);
		Env env = EnvUtils.getEnv();

		if (enableFirstLevelCache()) {
			env.removeObjectFromCache(key);
		}

		env.startCacheUpdate(key);
		cacheClient.delete(key);
		env.stopCacheOperation();
	}
	
	public List<T> findObjectsFromCache(String cacheKey){
		if(StringUtils.isEmpty(cacheKey)) return null;
		List<T> list = null;
		EnvUtils.getEnv().startCacheRead(cacheKey);
		String value = null;
		try{
			value = (String)cacheClient.get(cacheKey);
		} finally {
			EnvUtils.getEnv().stopCacheOperation();
		}
		if(value != null){
			list = new ArrayList<T>();
			String[] ids = value.split(",");
			for(String id:ids){
				long oid = NumberUtils.toLong(id, 0l);
				if(oid  > 0l){
					try {
						list.add(find(oid));
					} catch (EmptyResultDataAccessException ex) {
						// 有可能缓冲的对象已经被删除，忽略。
					}
				}
			}
		}
		return list;
	}
	
	public void putStringToCache(String key, String value){
		EnvUtils.getEnv().startCacheUpdate(key);
		cacheClient.set(key, value);
		EnvUtils.getEnv().stopCacheOperation();
	}
	
	public void removeFromCacheForKeyNoLog(String key){
		cacheClient.delete(key);
	}

	public void removeFromCacheForKey(String key){
		EnvUtils.getEnv().startCacheUpdate(key);
		cacheClient.delete(key);
		EnvUtils.getEnv().stopCacheOperation();
	}
	
	public static String clob2String(Clob clob) throws SQLException{
		if(clob == null) return null;
		String result = null;
		Reader inReader = clob.getCharacterStream();
		char[] c = new char[(int)clob.length()];
		try{
			inReader.read(c);
			result = new String(c);
			inReader.close();
		} catch(IOException e){
			e.printStackTrace();
		}
		return result;
	}
	
	public long[] findIds(String sql, Object ...args) {
		List<Long> ids = simpleJdbcTemplate.query(sql, idRowMapper ,args);
		long[] result = new long[ids.size()];
		for (int i = 0; i < result.length; i ++) {
			result[i] = ids.get(i);
		}
		return result;
	}

 	abstract protected T findFromDb(long id);
 	abstract protected T decode(String value);
 	abstract protected String encode(T obj);
 	abstract protected long getKey(T obj);
	protected boolean enableFirstLevelCache() { return false; }

	protected Object[] batchGetCache(List<Long> idList, String[] keys, Env env) {
		if (idList.isEmpty()) return new Object[0];
		String keyPerfix = type.getSimpleName();
		for (int i = 0; i < keys.length; i++) {
			keys[i] = keyPerfix + '-' + idList.get(i);
		}
		env.startCacheRead(keys[0] + "*[" + keys.length);

		try {
			return cacheClient.getMultiArray(keys);
		} finally {
			env.stopCacheOperation();
		}
	}
 	
}
