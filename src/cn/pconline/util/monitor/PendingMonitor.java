package cn.pconline.util.monitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PendingMonitor {
    static Map items = new HashMap();
    
    public synchronized static void open(RequestHolder holder) {
    	PendingCounter counter = (PendingCounter)items.get(holder.uri);
    	if (counter == null) {
    		counter = new PendingCounter();
    		items.put(holder.uri, counter);
    	}
    	
    	counter.open(holder);
    }
    
    public synchronized static void close(RequestHolder holder) {
    	PendingCounter counter = (PendingCounter)items.get(holder.uri);
    	counter.close(holder);
	}
    
    public static int getQueueSize(String uri) {
        PendingCounter counter = (PendingCounter)items.get(uri);
        if (counter == null) return 0;
        counter.refresh();
        return counter.queue.size();
    }
    
    public static int getPendingSize(String uri) {
        PendingCounter counter = (PendingCounter)items.get(uri);
        if (counter == null) return 0;
        // counter.refresh();
        return counter.pending.size();
    }
    
    public static synchronized void reset() {
    	for (Iterator it = items.values().iterator(); it.hasNext();) {
    		PendingCounter counter = (PendingCounter)it.next();
    		counter.clear();
    	}
    	items.clear();
    }
    
    public static List getLongPendings() {
        List result = new ArrayList();
        
        PendingCounter[] counters = (PendingCounter[])items.values().toArray(new PendingCounter[0]);
        for (int i = 0; i < counters.length; i++) {
            RequestHolder[] holders = counters[i].getLongPendings();
            for (int j = 0; j < holders.length; j ++) {
                result.add(holders[j]);
            }
        }
        
        Collections.sort(result);
        
        return result;
    }
    
}
