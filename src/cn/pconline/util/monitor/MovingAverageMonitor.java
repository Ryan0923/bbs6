package cn.pconline.util.monitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class MovingAverageMonitor {
    static Map items = new HashMap();
    
    public static synchronized void count(String key, float value, boolean error) {
        MovingAverageCounter counter = (MovingAverageCounter)items.get(key);
        if (counter == null) {
            counter = new MovingAverageCounter(key);
            items.put(key, counter);
        }
        
        counter.count(value, error);
    }

    public static List getReport() {
        List result = new ArrayList();
        synchronized (MovingAverageMonitor.class) {
            for (Iterator itr = items.values().iterator(); itr.hasNext(); ) {
                MovingAverageCounter counter = (MovingAverageCounter)itr.next();
                result.add(counter.duplicate());
            }
        }
        
        for (Iterator itr = result.iterator(); itr.hasNext(); ) {
            MovingAverageCounter counter = (MovingAverageCounter)itr.next();
            counter.adjust();
        }
        
        Collections.sort(result);
        
        return result;
    }
    
    public static String getInfo() {
        StringBuffer buf = new StringBuffer();
        for (Iterator it = getReport().iterator(); it.hasNext();) {
            buf.append(it.next()).append("\n");
        }
        return buf.toString();
    }
    
    public static String getLog() {
        StringBuffer buf = new StringBuffer();
        List counters = getReport();
        for (int i =0, c = counters.size(); i < c; ++i) {
            MovingAverageCounter counter = (MovingAverageCounter)counters.get(i);
            buf.append(counter.getLog()).append("\n");
        }
        return buf.toString();
    }

}
