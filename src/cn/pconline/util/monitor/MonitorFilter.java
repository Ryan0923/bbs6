package cn.pconline.util.monitor;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MonitorFilter implements Filter {
	private final Log logger = LogFactory.getLog(getClass());

	@Override
	public void destroy() {
		logger.info("MonitorFilter destroied.");
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		
        String sample = config.getInitParameter("sample");
        if (sample != null) {
        	String[] items = sample.split(" ");
        	for (int i = 0; i < items.length; i++) {
        		Monitor.addSample(items[i]);
        	}
        }
        
        logger.info("MonitorFilter inited.");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws ServletException, IOException {
        RequestHolder holder = new RequestHolder(request);
        Monitor.open(holder);
        try {

            chain.doFilter(request, response);

        } catch (ServletException se) {
        	holder.error(); throw se;
        } catch (IOException ioe) {
        	holder.error(); throw ioe;
        } catch (RuntimeException ex) {
        	holder.error(); throw (RuntimeException)ex;
        } catch (Error err) {
        	holder.error(); throw err;
        } finally {
        	Monitor.close(holder);
        }
	}
}
