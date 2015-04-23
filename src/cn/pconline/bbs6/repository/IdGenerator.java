package cn.pconline.bbs6.repository;

public interface IdGenerator {
	long generate(String tableName, String columnName);
}
