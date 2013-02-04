package org.easetech.easytest.reports.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class gathers all the test results during a test run. Results are used as input
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
	 * @param method The test method name
	 * @param input The map of input data (key=parameter name, value=data)
	 * @param output The output (this can be the returning data of the test method
	 * @param passed The passed result (passed/failed/exception)
	 * @param result The assertion message
	 * @param exception True if exception
	 * @param exceptionResult The exception message when execution of method throws exception
	 */
	public void addTestResult(String method, Map<String, Object> input,
			Object output, Boolean passed, String result, Boolean exception,
			String exceptionResult) {
		TestResultBean testResultBean = new TestResultBean(method, input,
				output, passed, result, exception, exceptionResult, new Date());
		this.addTestResult(testResultBean);
	}

	/**
	 * Add a {@link TestResultBean} to the list
	 * @param testResult
	 */
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
