package cn.pconline.bbs6.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

public class IdTableGenerator implements IdGenerator {
	DataSource idGenDataSource;
	
	public void setIdDataSource(DataSource idGenDataSource) {
		this.idGenDataSource = idGenDataSource;
	}
	
	int size = 10;
	
	Map<String, IdHolder> holderMap = new java.util.concurrent.ConcurrentHashMap<String, IdHolder>();
	
	@Override
	public long generate(String tableName, String columnName) {
		IdHolder holder = holderMap.get(tableName);
		if (holder == null) {
			holder = new IdHolder();
			holderMap.put(tableName, holder);
		}
		synchronized (holder) {
			if (holder.needAlloc()) {
				long lastUsedId = alloc(tableName, columnName, size);
				holder.currentId = lastUsedId + 1;
				holder.limit = lastUsedId + size;
			} else {
				holder.currentId ++;
			}

			return holder.currentId;
		}
		
	}
	
	static class IdHolder {
		long currentId;
		long limit;
		boolean needAlloc() {return currentId >= limit; }
	}
	
	public long alloc(String tableName, String columnName, int size) {
		long result = 0;
		Connection con = null;
		boolean oldAutoCommit = false;
		try {
			con = idGenDataSource.getConnection();
			oldAutoCommit = con.getAutoCommit();
			con.setAutoCommit(false);
			int updateCount = updateLastUsedId(con, tableName, columnName, size);
			
			if (updateCount == 0) {
				initIdTable(con, tableName, columnName);
//				updateLastUsedId(con, tableName, columnName);
			}
			result = getLastUsedId(con, tableName, columnName);
			
			con.commit();
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			throw new RuntimeException(e);
		} finally {
			if (con != null) {
				try {
					con.setAutoCommit(oldAutoCommit);
					con.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		
		return result;
	}

	static long getLastUsedId(Connection con, String tableName, String columnName) throws SQLException {
		PreparedStatement ps = con.prepareStatement("select last_used_id from bbs6_keygen where table_name = ?");
		ps.setString(1, tableName);
		ResultSet rs = ps.executeQuery();
		rs.next();
		long result = rs.getLong(1);
		rs.close();
		ps.close();
		return result;
	}
	
	static int updateLastUsedId(Connection con, String tableName, String columnName, int size) throws SQLException  {
		PreparedStatement ps = con.prepareStatement("update bbs6_keygen set last_used_id = last_used_id + ?" +
		" where table_name = ?");

		ps.setInt(1, size);
		ps.setString(2, tableName);
		
		int result = ps.executeUpdate();
		ps.close();
		return result;
	}
	
	static void initIdTable(Connection con, String tableName, String columnName) throws SQLException {
		PreparedStatement ps = con.prepareStatement("select max(" + columnName + ") from " + tableName);
		ResultSet rs = ps.executeQuery();
		rs.next();
		long maxId = rs.getLong(1);
		rs.close();
		ps.close();
		
		ps = con.prepareStatement("insert into bbs6_keygen (table_name, last_used_id) values (?, ?)");
		ps.setString(1, tableName);
		ps.setLong(2, maxId);
		ps.executeUpdate();
		ps.close();
	}
	
}
