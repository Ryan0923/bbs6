package cn.pconline.bbs6.repository;

public class MySqlPageBuilder implements SqlPageBuilder {

	@Override
	public String buildPageSql(String sql, int pageNo, int pageSize) {
		return sql + " LIMIT " + (pageNo - 1) * pageSize + "," + pageSize;
	}

}
