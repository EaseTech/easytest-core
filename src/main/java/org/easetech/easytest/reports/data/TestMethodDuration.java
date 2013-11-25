package org.easetech.easytest.reports.data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Class encapsulating the information about the test methods exact executiopn time.
 * This class is actually used by the Reports functionality to provide useful reports
 * information to the end user.
 * 
 * @author gpcmol
 * 
 */
public class TestMethodDuration implements Serializable {

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
	public TestMethodDuration() {
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
	public TestMethodDuration(String item, long startInNano, long endInNano) {
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
	 * @return Time difference in nano seconds
	 */
	public long getNanoDifference() {
		return (this.endInNano - this.startInNano);
	}

	/**
	 * Returns the difference end - start in milli seconds
	 * 
	 * @return difference in ms
	 */
	public long getMsDifference() {
		return getNanoDifference() / NANO_TO_MILLI;
	}
	
	/**
	 * Returns rounded milli seconds
	 * @return rounded difference in ms
	 */
	public BigDecimal getRoundedMsDifference() {
		return new BigDecimal((double) getMsDifference());
	}

	@Override
	public String toString() {
		return "TestItemDurationBean [getItem()=" + getItem()
				+ ", getStartInNano()=" + getStartInNano()
				+ ", getEndInNano()=" + getEndInNano() + ", getMsDifference()="
				+ getMsDifference() + "]";
	}

}
