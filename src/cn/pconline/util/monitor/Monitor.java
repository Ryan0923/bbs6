package cn.pconline.util.monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Monitor {
    private static Map items = new HashMap(256);
    private static Set samples = new HashSet();
    private static long startTime = System.currentTimeMillis();
    public static java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static java.text.NumberFormat doubleFormat = new java.text.DecimalFormat("0.##");
    
    public static synchronized void close(RequestHolder holder) {
    	holder.finish();
        String uri = holder.uri;
        
        Item item = (Item)items.get(uri);
        if (item == null) {
            item = new Item(uri);
            items.put(uri, item);
        }
        item.count(holder);
        if (samples.contains(uri)) {
        	MovingAverageMonitor.count(uri, holder.duration, holder.error);
			//PendingMonitor.close(holder);
        }
    }

	public static void open(RequestHolder holder) {
        if (samples.contains(holder.uri)) {
			//PendingMonitor.open(holder);
        }
	}
    
    public static synchronized void addSample(String uri) {
    	samples.add(uri);
    }
    
    public static synchronized void removeSample(String uri) {
    	samples.remove(uri);
    }
    
    public static synchronized void reset() {
        items.clear();
        startTime = System.currentTimeMillis();
        PendingMonitor.reset();
    }
    
    public static synchronized Collection getReport() {
        List result = new ArrayList();
        for(Iterator itr = items.values().iterator(); itr.hasNext();){
          Item item = (Item) itr.next();
          if(item.count > 0){
            result.add(item);
          }
        }
        Collections.sort(result);
        return result;
    }
    
    public static long getStartTime() {
        return startTime;
    }
    
    public static class Item implements Comparable{
        String uri;
        long count;
        long errors;
        long minButtomDuration = 0;
        Object[] bottoms = new Object[10];
        int[] bottomKeys = new int[10];
        long count1, count3, count5, count10, count20, count20x;

        public Item(String uri) {
            this.uri = uri;
        }

        void count(RequestHolder holder) {
            count ++;
            if (holder.error) errors ++;
            
            int duration = holder.duration;
            
            if (duration < 1000) {          count1 ++;
            } else if (duration < 3000) {   count3 ++;
            } else if (duration < 5000) {   count5 ++;
            } else if (duration < 10000) {  count10 ++;
            } else if (duration < 20000) {  count20 ++;
            } else {                        count20x ++;}
            
            if (duration < minButtomDuration) {
                return;
            }
            
            // bottom 10 process
            String[] bottom = holder.getFields();
            
            for (int i = 0; i < 10; i ++) {
                if (duration > bottomKeys[i]) {
                    if (i == 9) {
                        bottomKeys[i] = duration;
                        bottoms[i] = bottom;
                    } else if (i == 8) {
                        bottomKeys[9] = bottomKeys[8];
                        bottoms[9] = bottoms[8];
                        bottomKeys[i] = duration;
                        bottoms[i] = bottom;
                    } else {
                        System.arraycopy(bottomKeys, i, bottomKeys, i + 1, 10 - i - 1);
                        System.arraycopy(bottoms, i, bottoms, i + 1, 10 - i - 1);
                        bottomKeys[i] = duration;
                        bottoms[i] = bottom;
                    }
                    break;
                } 
            }
            minButtomDuration = bottomKeys[9];
            
        }

        public int compareTo(Object o) {
            if (this == o) return 0;
            if (!(o instanceof Item)) return 1;
            return (int)(((Item)o).count - this.count);
        }

        public Object[] getBottoms() {
            return bottoms;
        }

        public long getCount() {
            return count;
        }

        public long getError() {
        	return errors;
        }
        
        public long getMaxDuration() {
            return bottomKeys[0];
        }

        public String getUri() {
            return uri;
        }

        public long getCount1() {
            return count1;
        }

        public long getCount10() {
            return count10;
        }

        public long getCount20() {
            return count20;
        }

        public long getCount3() {
            return count3;
        }

        public long getCount5() {
            return count5;
        }

        public long getCount20x() {
            return count20x;
        }
        
        public String getPerMinute() {
            return doubleFormat.format(60000d * count / ((System.currentTimeMillis() + 1 - Monitor.startTime))); 
        }
    }


}
