package cn.pconline.bbs6.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import cn.pconline.bbs6.util.EnvUtils;


@Aspect
public class AopSqlMonitor {
	/*
     * 
     */
	@Around("execution(* org.springframework.jdbc.core.JdbcTemplate.query*(..)) " +
			"|| execution(* org.springframework.jdbc.core.JdbcTemplate.update*(..)) " +
			"|| execution(* org.springframework.jdbc.core.JdbcTemplate.execute*(..)) ")
	public Object monitor(ProceedingJoinPoint pjp) throws Throwable {
		if (pjp.getSignature().getName().startsWith("query")) {
			EnvUtils.getEnv().startDatabaseRead(pjp.getArgs()[0].toString());
		} else {
			EnvUtils.getEnv().startDatabaseUpdate(pjp.getArgs()[0].toString());
		}
		try {
			return pjp.proceed();
		} finally {
			EnvUtils.getEnv().stopDatabaseOperation();
		}
	}
	
}
