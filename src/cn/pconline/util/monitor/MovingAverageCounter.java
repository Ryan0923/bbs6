package cn.pconline.util.monitor;

public class MovingAverageCounter implements Comparable {
	static final int ONE_MINUTE = 60000;
	static final int FIVE_MINUTES = 5 * ONE_MINUTE;
	static final int FIFTEEN_MINUTES = 15 * ONE_MINUTE;
	static final int DEFAULT_UNIT = 10000; // 10 seconds

	int unit;
	String name;
	long start;

	int[] counts;
	double[] values;
	int[] errors;

	public MovingAverageCounter(String name) {
		this(name, DEFAULT_UNIT);
	}

	public MovingAverageCounter(String name, int unit) {
		this(name, unit, FIFTEEN_MINUTES / unit);
	}

	public MovingAverageCounter(String name, int unit, int size) {
		this.unit = unit;
		this.name = name;
		if (size < FIFTEEN_MINUTES / unit) {
			size = FIFTEEN_MINUTES / unit;
		}
		counts = new int[size];
		values = new double[size];
		errors = new int[size];
		long now = System.currentTimeMillis();
		start = now - now % unit;
	}

	public void count(double value, boolean error) {
		adjust();
		counts[0] += 1;
		values[0] += value;
		if (error) errors[0] += 1;
	}

	public void adjust() {
		long now = System.currentTimeMillis();
		long start2 = now - now % unit;
		if (start2 > start) {
			int shift = (int) (start2 - start) / unit;
			if (shift > counts.length)
				shift = counts.length;
			start = start2;
			System.arraycopy(counts, 0, counts, shift, counts.length - shift);
			System.arraycopy(values, 0, values, shift, values.length - shift);
			System.arraycopy(errors, 0, errors, shift, errors.length - shift);
			for (int i = 0; i < shift; i++) {
				counts[i] = 0;
				values[i] = 0.0f;
				errors[i] = 0;
			}
		}
	}

	public String getName() {
		return name;
	}

	public double getCurrentMinuteAverage() {
		return getAverage(ONE_MINUTE / unit);
	}

	public double getFiveMinuteAverage() {
		return getAverage(FIVE_MINUTES / unit);
	}

	public double getFifteenMinuteAverage() {
		return getAverage(FIFTEEN_MINUTES / unit);
	}

	public int getCurrentMinuteCount() {
		return getCount(ONE_MINUTE / unit);
	}

	public int getFiveMinuteCount() {
		return getCount(FIVE_MINUTES / unit);
	}

	public int getFifteenMinuteCount() {
		return getCount(FIFTEEN_MINUTES / unit);
	}

	public int getCurrentMinuteError() {
		return getError(ONE_MINUTE / unit);
	}

	public int getFiveMinuteError() {
		return getError(FIVE_MINUTES / unit);
	}

	public int getFifteenMinuteError() {
		return getError(FIFTEEN_MINUTES / unit);
	}

	public double getAverage(int size) {
		double total = 0.0f;
		int c = 0;
		for (int i = 0; i < size; i++) {
			if (counts[i] > 0) {
				total += values[i];
				c += counts[i];
			}
		}
		return (c == 0) ? 0.0f : total / c;
	}

    public int getQueueSize() {
        return PendingMonitor.getQueueSize(name);
    }
    
    public int getPendingSize() {
        return PendingMonitor.getPendingSize(name);
    }
    
	public int getCount(int size) {
		int total = 0;
		for (int i = 0; i < size; i++) {
			if (counts[i] > 0) {
				total += counts[i];
			}
		}
		return total;
	}

	public int getError(int size) {
		int total = 0;
		for (int i = 0; i < size; i++) {
			if (errors[i] > 0) {
				total += errors[i];
			}
		}
		return total;
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(name).append("(unit:").append(unit).append(")");

		buf.append(": {");
		buf.append("minute-error=").append(getCurrentMinuteError()).append(", ");
		buf.append("5-minute-error=").append(getFiveMinuteError()).append(", ");
		buf.append("15-minute-error=").append(getFifteenMinuteError()).append(", ");
		buf.append("minute-count=").append(getCurrentMinuteCount()).append(", ");
		buf.append("5-minute-connt=").append(getFiveMinuteCount()).append(", ");
		buf.append("15-minute-count=").append(getFifteenMinuteCount()).append(", ");
		buf.append("minute-average=").append(getCurrentMinuteAverage()).append(", ");
		buf.append("5-minute-average=").append(getFiveMinuteAverage()).append(", ");
		buf.append("15-minute-average=").append(getFifteenMinuteAverage()).append("}");

		return buf.toString();
	}

	public String getLog() {
		StringBuffer buf = new StringBuffer(name).append('\t');
		buf.append(unit).append('\t').append(start).append('\t');
		for (int i = 0; i < counts.length; i++) {
			if (counts[i] > 0) {
				buf.append(values[i]).append('/').append(counts[i]).append('/').append(errors[i]);
			} else {
				buf.append('-');
			}
			buf.append(' ');
		}
		return buf.toString();
	}

	public int compareTo(Object o) {
		if (this == o)
			return 0;
		if (!(o instanceof MovingAverageCounter))
			return 1;
		return (int) (((MovingAverageCounter) o).getCurrentMinuteAverage() - this
				.getCurrentMinuteAverage());
	}

	public MovingAverageCounter duplicate() {
		MovingAverageCounter dup = new MovingAverageCounter(this.name,
				this.unit);
		dup.start = this.start;
		System.arraycopy(this.counts, 0, dup.counts, 0, this.counts.length);
		System.arraycopy(this.values, 0, dup.values, 0, this.values.length);
		System.arraycopy(this.errors, 0, dup.errors, 0, this.errors.length);
		return dup;
	}

}
