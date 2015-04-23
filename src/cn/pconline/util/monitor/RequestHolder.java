package cn.pconline.util.monitor;

import javax.servlet.http.HttpServletRequest;

public class RequestHolder implements Comparable {
	String uri;
	long start;
	int duration;
	boolean error = false;
	Object request;

	public RequestHolder(Object request) {
		this.start = System.currentTimeMillis();
		this.request = request;
		genUri();
	}
	
	private RequestHolder() {}
	
	public void finish() {
		this.duration = (int)(System.currentTimeMillis() - start);
	}

	public void error() {
		this.error = true;
	}
	
	protected void genUri() {
		HttpServletRequest req = (HttpServletRequest)request;
        this.uri = req.getRequestURI();
	}
	
	public String[] getFields() {
		HttpServletRequest req = (HttpServletRequest)request;
        String parameters = req.getQueryString();
        String reffer = req.getHeader("referer");
        String ip = req.getRemoteAddr();
        String foward = req.getHeader("X-Forwarded-For");
        if (foward != null)
            ip = foward + '/' + ip;
        
        String[] fields = new String[]{
                duration+"",
                Monitor.dateFormat.format(new java.util.Date(start)),
                ip,
                parameters == null ? "" : parameters,
                reffer == null ? "" : reffer};
        
        return fields;

	}
	
	public String getUri() {
	    return this.uri;
	}
	
	public int compareTo(Object o) {
		RequestHolder other = (RequestHolder)o;
		return (int)(this.start - other.start);
	}

	public RequestHolder duplicate() {
		RequestHolder dup = new RequestHolder();
		dup.uri = this.uri;
		dup.start = this.start;
		dup.request = this.request;
		return dup;
	}

	@Override
    public String toString() {
        return super.toString() + ", hash:" + hashCode();
    }
    
}

