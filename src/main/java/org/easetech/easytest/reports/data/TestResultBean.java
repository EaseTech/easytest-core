package org.easetech.easytest.reports.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is a single result of a test run for one test method.
 * 
 * @author gpcmol
 * 
 */
public class TestResultBean implements Serializable {

	private static final long serialVersionUID = -2336621400716756554L;

	/**
	 * Method name
	 */
	private String method;

	/**
	 * Input values. key=parameter name, value=value
	 */
	private Map<String, Object> input;

	/**
	 * Return object of method
	 */
	private Object output;

	/**
	 * True if passed
	 */
	private Boolean passed;

	/**
	 * Assertion message
	 */
	private String result;

	/**
	 * True if exception
	 */
	private Boolean exception;

	/**
	 * Exception message when execution of method throws exception
	 */
	private String exceptionResult;

	/**
	 * Test item duration beans (contains items that in the method run are
	 * called, e.g. service method calls. These method calls need to be
	 * annotated with @Intercept)
	 */
	private List<DurationBean> testItemDurations;
	
	/**
	 * Duration bean data structure: key = item name, value is the bean
	 */
	private Map<String, DurationBean> testItemDurationsMap;

	/**
	 * Date of run
	 */
	private Date date;

	public TestResultBean() {
		this.input = new LinkedHashMap<String, Object>();
		this.testItemDurations = new ArrayList<DurationBean>();
		this.testItemDurationsMap = new HashMap<String, DurationBean>();
	}

	public TestResultBean(String method, Map<String, Object> input,
			Object output, Boolean passed, String result, Boolean exception,
			String exceptionResult, Date date) {
		this.method = method;
		this.input = input;
		this.output = output;
		this.passed = passed;
		this.result = result;
		this.exception = exception;
		this.exceptionResult = exceptionResult;
		this.date = date;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Map<String, Object> getInput() {
		return input;
	}

	public void setInput(Map<String, Object> input) {
		this.input = input;
	}

	public void addInput(String key, String value) {
		this.input.put(key, value);
	}

	public Object getOutput() {
		return output;
	}

	public void setOutput(Object output) {
		this.output = output;
	}

	public Boolean getPassed() {
		return passed;
	}

	public void setPassed(Boolean passed) {
		this.passed = passed;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Boolean getException() {
		return exception;
	}

	public void setException(Boolean exception) {
		this.exception = exception;
	}

	public String getExceptionResult() {
		return exceptionResult;
	}

	public void setExceptionResult(String exceptionResult) {
		this.exceptionResult = exceptionResult;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * Returns either the result of an assertion or an exception trace
	 * 
	 * @return string
	 */
	public String getResultString() {
		if (result != null) {
			return result;
		} else if (exceptionResult != null) {
			return exceptionResult;
		}
		return null;
	}

	public List<DurationBean> getTestItemDurations() {
		return testItemDurations;
	}

	public void setTestItemDurations(List<DurationBean> testItemDurations) {
		this.testItemDurations = testItemDurations;
	}
	
	public Map<String, DurationBean> getTestItemDurationsMap() {
		return testItemDurationsMap;
	}

	public void setTestItemDurationsMap(Map<String, DurationBean> testItemDurationsMap) {
		this.testItemDurationsMap = testItemDurationsMap;
	}

	public void addTestItemDurationBean(DurationBean testItemDurationBean) {
		this.testItemDurations.add(testItemDurationBean);
		if (testItemDurationBean.getItem() != null && !(testItemDurationBean.getItem().length() <= 0)) {
			this.testItemDurationsMap.put(testItemDurationBean.getItem(), testItemDurationBean);
		}
	}

	@Override
	public String toString() {
		return "TestResultBean [getMethod()=" + getMethod() + ", getInput()="
				+ getInput() + ", getOutput()=" + getOutput()
				+ ", getPassed()=" + getPassed() + ", getResult()="
				+ getResult() + ", getException()=" + getException()
				+ ", getExceptionResult()=" + getExceptionResult()
				+ ", getDate()=" + getDate() + ", getResultString()="
				+ getResultString() + ", getTestItemDurations()="
				+ getTestItemDurations() + "]";
	}

}
