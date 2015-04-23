package cn.pconline.bbs6.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

public class EnvService {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	SimpleJdbcTemplate simpleJdbcTemplate;
	
    @Autowired
    private PlatformTransactionManager transactionManager;


    public TransactionTemplate getTransactionTemplate() {
        return new TransactionTemplate(transactionManager);
    }
    
    public JdbcTemplate getJdbcTemplate() {
    	return jdbcTemplate;
    }
    
    public SimpleJdbcTemplate getSimpleJdbcTemplate() {
    	return simpleJdbcTemplate;
    }
}
