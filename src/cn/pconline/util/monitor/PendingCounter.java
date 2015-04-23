package cn.pconline.util.monitor;

import java.util.HashSet;
import java.util.LinkedList;

public class PendingCounter {
	LinkedList queue = new LinkedList();
    HashSet pending = new HashSet();
	
	public void open(RequestHolder holder) {
		queue.addLast(holder);
	}
	
	public void close(RequestHolder holder) {
		
		if (pending.remove(holder)) {
            return;
        }
		
		queue.remove(holder);
        refresh();
	}
	
    public void refresh() {
        while (queue.size() > 0) {
            RequestHolder h = (RequestHolder)queue.getFirst();
            if (System.currentTimeMillis() -  h.start < 10000) return;
            
            queue.removeFirst();
            pending.add(h);
        }
    }
    
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("queue size:").append(queue.size());
		buf.append(", pending size:").append(pending.size());
		return buf.toString();
	}
	
	public synchronized void clear() {
		queue.clear();
		pending.clear();
	}

	public RequestHolder[] getLongPendings() {
	    RequestHolder[] longPending = (RequestHolder[])pending.toArray(new RequestHolder[0]);
	    for (int i = 0; i < longPending.length; i ++) {
	        longPending[i] = longPending[i].duplicate();
	    }
	    
	    return longPending;
	}
}
