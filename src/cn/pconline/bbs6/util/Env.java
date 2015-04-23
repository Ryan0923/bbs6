package cn.pconline.bbs6.util;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import cn.pconline.bbs6.cache.CacheClient;
import cn.pconline.bbs6.domain.User;
import cn.pconline.bbs6.repository.*;
import cn.pconline.bbs6.service.*;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Env {
	private Map<String, Object> firstLevelCache = new HashMap<String, Object>();

	private HttpServletRequest request;
    private HttpServletResponse response;
    private PageContext pageContext;
    private ServletContext servletContext;
    private User user;

    public HttpServletRequest getRequest() {
		return request;
	}
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	public HttpServletResponse getResponse() {
		return response;
	}
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
	public PageContext getPageContext() {
		return pageContext;
	}
	public void setPageContext(PageContext pageContext) {
		this.pageContext = pageContext;
	}
	public ServletContext getServletContext() {
		return servletContext;
	}
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public Object getObjectFromCache(String key) {
		return firstLevelCache.get(key);
	}
	public void putObjectToCache(String key, Object value) {
		firstLevelCache.put(key, value);
	}
	public void removeObjectFromCache(String key) {
		firstLevelCache.remove(key);
	}

	boolean userLoaded = false;
	public User getUser() {
		return null;
	}

	String ip = null;
	public String getIp() {
		if (ip == null) {
			ip = IpUtils.getIp(request);
		}
		return ip;
	}

    public WebApplicationContext getApplicationContext(){
        if (request != null) {
            return WebApplicationContextUtils.getWebApplicationContext(servletContext);
        } else {
            return null;
        }
    }
    
    long startTime = System.currentTimeMillis();
    int databaseReadTimes = 0;
    int databaseUpdateTimes = 0;
    int cacheReadTimes = 0;
    int cacheUpdateTimes = 0;
    Collector collector = new Collector();
    public int getDbRead() {
    	return databaseReadTimes;
    }
    public int getCacheRead() {
    	return cacheReadTimes;
    }
    public int getDbUpdate() {
    	return databaseUpdateTimes;
    }
    public int getCacheUpdate() {
    	return cacheUpdateTimes;
    }

	public String getRoot(){
		return null;
	}
    
	public Date getNow(){
		return new Date();
	}

	public static Date getToday() {
			Calendar cld = Calendar.getInstance();
			cld.set(Calendar.HOUR_OF_DAY, 0);
			cld.set(Calendar.MINUTE, 0);
			cld.set(Calendar.SECOND, 0);
			cld.set(Calendar.MILLISECOND, 0);
			return cld.getTime();
		}

    public void startDatabaseRead(String sql) {
    	databaseReadTimes ++;
    	collector.start("DR:" + sql);
    }
    public void startDatabaseUpdate(String sql) {
    	databaseUpdateTimes ++;
    	collector.start("DU:" + sql);
    }
    public void stopDatabaseOperation() {
    	collector.stop();
    }
    public void startCacheRead(String key) {
    	cacheReadTimes ++;
    	collector.start("CR:" + key);
    }
    public void startCacheUpdate(String key) {
    	cacheUpdateTimes ++;
    	collector.start("CU:" + key);
    }
    public void stopCacheOperation() {
    	collector.stop();
    }
    
    public String getLogString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append(request.getRequestURI()).append(',');
    	sb.append(System.currentTimeMillis() - startTime).append(',');
    	sb.append("DR").append(databaseReadTimes);
    	sb.append("DU").append(databaseUpdateTimes);
    	sb.append("CR").append(cacheReadTimes);
    	sb.append("CU").append(cacheUpdateTimes).append(',');
    	sb.append(request.getRemoteAddr()).append('\n');
    	sb.append(collector.toString());
    	return sb.toString();
    }
    
    public TransactionTemplate getTransactionTemplate() {
        WebApplicationContext ctx = getApplicationContext();
        EnvService envService = (EnvService)ctx.getBean("envService");
        
        return envService.getTransactionTemplate();
    }
    
    public JdbcTemplate getJdbcTemplate() {
        WebApplicationContext ctx = getApplicationContext();
        EnvService envService = (EnvService)ctx.getBean("envService");
        
        return envService.getJdbcTemplate();
    }
    public SimpleJdbcTemplate getSimpleJdbcTemplate() {
        WebApplicationContext ctx = getApplicationContext();
        EnvService envService = (EnvService)ctx.getBean("envService");
        
        return envService.getSimpleJdbcTemplate();
    }
    public CacheClient getCacheClient() {
    	return (CacheClient)getApplicationContext().getBean("cacheClient");
    }
	public UserRepository getUserRepository() {
		return (UserRepository)getApplicationContext().getBean("userRepository");
	}
    public UserService getUserService() {
        return (UserService)getApplicationContext().getBean("userService");
    }
	public ForumRepository getForumRepository() {
		return (ForumRepository)getApplicationContext().getBean("forumRepository");
	}
    public ForumService getForumService() {
        return (ForumService)getApplicationContext().getBean("forumService");
    }
	public TopicRepository getTopicRepository() {
		return (TopicRepository)getApplicationContext().getBean("topicRepository");
	}
	public PostRepository getPostRepository() {
		return (PostRepository)getApplicationContext().getBean("postRepository");
	}
    public TopicService getTopicService(){
		return (TopicService)getApplicationContext().getBean("topicService");
    }

}
