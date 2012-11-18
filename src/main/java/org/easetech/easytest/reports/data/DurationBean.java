package org.easetech.easytest.reports.data;

import java.io.Serializable;

/**
 * Class contains duration of item. Item can be a method call. A test method
 * runs for every test data input. In the test method, it is possible to measure
 * the time of a service call which has been called in the test method. In this
 * case to have this service method call, annotate it with @Intercept
 * 
 * @author gpcmol
 * 
 */
public class DurationBean implements Serializable {

	private static final long serialVersionUID = -6175933760799880386L;

	public static final long NANO_TO_MILLI = 1000000L;

	/**
	 * Item, e.g. intercepted method call
	 */
	private String item;

	/**
	 * Start system time in nano
	 */
	private long startInNano;

	/**
	 * End system time in nano
	 */
	private long endInNano;

	/**
	 * Constructor
	 */
	public DurationBean() {
		// empty constructor
	}

	/**
	 * Constructor
	 * 
	 * @param item
	 *            e.g. method call
	 * @param startInNano
	 *            start system time in nano
	 * @param endInNano
	 *            end system time in nano
	 */
	public DurationBean(String item, long startInNano, long endInNano) {
		this();
		this.item = item;
		this.startInNano = startInNano;
		this.endInNano = endInNano;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public long getStartInNano() {
		return startInNano;
	}

	public void setStartInNano(long startInNano) {
		this.startInNano = startInNano;
	}

	public long getEndInNano() {
		return endInNano;
	}

	public void setEndInNano(long endInNano) {
		this.endInNano = endInNano;
	}

	/**
	 * Time difference in nano seconds
	 * 
	 * @return
	 */
	public long getNanoDifference() {
		return (this.endInNano - this.startInNano);
	}

	/**
	 * Returns the difference end - start in milli seconds
	 * 
	 * @return
	 */
	public long getMsDifference() {
		return getNanoDifference() / NANO_TO_MILLI;
	}

	@Override
	public String toString() {
		return "TestItemDurationBean [getItem()=" + getItem()
				+ ", getStartInNano()=" + getStartInNano()
				+ ", getEndInNano()=" + getEndInNano() + ", getMsDifference()="
				+ getMsDifference() + "]";
	}

}
