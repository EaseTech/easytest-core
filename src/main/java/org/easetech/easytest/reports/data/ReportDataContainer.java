package org.easetech.easytest.reports.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Accumulates all the test results during a test run. Results are used as input
 * for the TestReportHelper
 * 
 * @author gpcmol
 * 
 */
public class ReportDataContainer {

	/**
	 * The tested class
	 */
	private Class<?> testClass;

	/**
	 * Holds all the test results
	 */
	private List<TestResultBean> testResults;

	/**
	 * Holds the test results for method. Key is test method name, value is a
	 * list of test results
	 */
	private Map<String, List<TestResultBean>> methodTestResults;

	// TODO map method, list duration ??? This can also be obtained from the
	// testresultbean list

	/**
	 * Private constructor Initializes the report data structures
	 */
	private ReportDataContainer() {
		this.testResults = new ArrayList<TestResultBean>();
		this.methodTestResults = new LinkedHashMap<String, List<TestResultBean>>();
	}

	/**
	 * Constructor
	 * 
	 * @param testClass
	 */
	public ReportDataContainer(Class<?> testClass) {
		this();
		this.testClass = testClass;
	}

	/**
	 * Creates a TestResultBean which contains all the test result data and adds
	 * it to the report data structures
	 * 
	 * @param method
	 * @param input
	 * @param output
	 * @param passed
	 * @param result
	 * @param exception
	 * @param exceptionResult
	 */
	public synchronized void addTestResult(String method, Map<String, Object> input,
			Object output, Boolean passed, String result, Boolean exception,
			String exceptionResult) {
		TestResultBean testResultBean = new TestResultBean(method, input,
				output, passed, result, exception, exceptionResult, new Date());
		this.addTestResult(testResultBean);
	}

	public void addTestResult(TestResultBean testResult) {
		String key = testResult.getMethod();
		List<TestResultBean> list = this.methodTestResults.get(key);
		if (list == null) {
			list = new ArrayList<TestResultBean>();
			this.methodTestResults.put(key, list);
		}
		list.add(testResult);

		this.testResults.add(testResult);
	}

	public List<TestResultBean> getTestResults() {
		return testResults;
	}

	public Map<String, List<TestResultBean>> getMethodTestResults() {
		return methodTestResults;
	}

	public String getClassName() {
		return getTestClass().getName();
	}

	public Class<?> getTestClass() {
		return testClass;
	}

}
