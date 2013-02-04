package org.easetech.easytest.reports.data;

import java.awt.image.BufferedImage;
import java.io.Serializable;

import org.easetech.easytest.util.CommonUtils;

/**
 * This pojo contains the totals of a set of tests
 * This is used to feed to a report
 * 
 * @author gpcmol
 * 
 */
public class ReportTotalsBean implements Serializable {

	private static final long serialVersionUID = 2033649257986672921L;

	/**
	 * Can be a test method name, servicename or class name etc.
	 */
	private String item;

	/**
	 * # of tests passed
	 */
	private Long passed = 0L;

	/**
	 * # of tests failed
	 */
	private Long failed = 0L;

	/**
	 * # of tests has exception
	 */
	private Long exception = 0L;

	/**
	 * Chart of the totals
	 */
	private BufferedImage totalsGraph;

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public Long getPassed() {
		return passed;
	}

	public void setPassed(Long passed) {
		this.passed = passed;
	}

	public void addPassed() {
		this.passed++;
	}

	public void addPassed(long passed) {
		this.passed += passed;
	}

	public Long getFailed() {
		return failed;
	}

	public void setFailed(Long failed) {
		this.failed = failed;
	}

	public void addFailed() {
		this.failed++;
	}

	public void addFailed(long failed) {
		this.failed += failed;
	}

	public Long getException() {
		return exception;
	}

	public void setException(Long exception) {
		this.exception = exception;
	}

	public void addException() {
		this.exception++;
	}

	public void addException(long exception) {
		this.exception += exception;
	}

	public BufferedImage getTotalsGraph() {
		return totalsGraph;
	}

	public void setTotalsGraph(BufferedImage totalsGraph) {
		this.totalsGraph = totalsGraph;
	}

	/**
	 * passed + failed + exception
	 */
	public Long getTotal() {
		return this.passed + this.failed + this.exception;
	}

	/**
	 * Percentage of tests passed
	 * 
	 * @return
	 */
	public Double getPercentagePassed() {
		return CommonUtils.getRounded(
				((double) this.passed / this.getTotal()) * 100, 2);
	}

	/**
	 * Percentage of tests failed
	 * 
	 * @return
	 */
	public Double getPercentageFailed() {
		return CommonUtils.getRounded(
				((double) this.failed / this.getTotal()) * 100, 2);
	}

	/**
	 * Percentage of tests exception
	 * 
	 * @return
	 */
	public Double getPercentageException() {
		return CommonUtils.getRounded(
				((double) this.exception / this.getTotal()) * 100, 2);
	}

	@Override
	public String toString() {
		return "ReportTotalsBean [getItem()=" + getItem() + ", getPassed()="
				+ getPassed() + ", getFailed()=" + getFailed()
				+ ", getException()=" + getException() + ", getTotal()="
				+ getTotal() + ", getPercentagePassed()="
				+ getPercentagePassed() + ", getPercentageFailed()="
				+ getPercentageFailed() + ", getPercentageException()="
				+ getPercentageException() + "]";
	}

}
