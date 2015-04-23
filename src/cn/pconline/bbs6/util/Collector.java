package cn.pconline.bbs6.util;

import java.util.ArrayList;
import java.util.List;

public class Collector {
	List<CollectItem> items = new ArrayList<CollectItem>();
	long start;
	CollectItem item;

	public void start(String string) {
		start = System.currentTimeMillis();
		item = new CollectItem(string);
	}
	
	public void stop() {
		item.usedMillis = System.currentTimeMillis() - start;
		items.add(item);
		item = null;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0, c = items.size(); i < c; ++i) {
			sb.append(items.get(i).toString());
			if (i < c - 1) sb.append('\n');
		}
		return sb.toString();
	}
	
	static class CollectItem {
		String key;
		long usedMillis;
		
		public CollectItem(String key) {
			this.key = key;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(' ').append('+');
			sb.append(usedMillis).append(',');
			sb.append(key);
			return sb.toString();
		}
	}
}
