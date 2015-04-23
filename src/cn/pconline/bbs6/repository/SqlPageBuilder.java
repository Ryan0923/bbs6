package cn.pconline.bbs6.repository;

public interface SqlPageBuilder {
	public String buildPageSql(String sql, int pageNo, int pageSize);
}
