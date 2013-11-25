package org.easetech.easytest.reports.data;

import java.io.Serializable;

public class Duration implements Serializable, Comparable<String> {
	
	private static final long serialVersionUID = 6732313448995436466L;

	/**
	 * Item name
	 */
	private String method = "";
	
	/**
	 * Minimum value
	 */
	private int min = 0;
	
	/**
	 * Maximum value
	 */
	private int max = 0;
	
	/**
	 * Average of all the values
	 */
	private int avg = 0;
	
	/**
	 * The total number of method calls
	 */
	private int count = 0;

	/**
	 * Constructor
	 * @param item item name
	 */
	public Duration(String item) {
		this.method = item;
	}

	/**
	 * Constructor
	 * @param method method call
	 * @param min minimum value
	 * @param max maximum value
	 * @param avg average value
	 * @param count total number of items
	 */
	public Duration(String method, int min, int max, int avg, int count) {
		this(method);
		this.min = min;
		this.max = max;
		this.avg = avg;
		this.count = count;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getAvg() {
		return avg;
	}

	public void setAvg(int avg) {
		this.avg = avg;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "Duration [method=" + method + ", min=" + min + ", max=" + max
				+ ", avg=" + avg + ", count=" + count + "]";
	}

	public int compareTo(String o) {
		return this.method.compareTo(o);
	}

}
