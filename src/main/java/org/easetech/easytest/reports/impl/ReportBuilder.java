package org.easetech.easytest.reports.impl;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.easetech.easytest.reports.data.Duration;
import org.easetech.easytest.reports.data.ImageBean;
import org.easetech.easytest.reports.data.MethodDurationReportBean;
import org.easetech.easytest.reports.data.ReportDataContainer;
import org.easetech.easytest.reports.data.ReportTotalsBean;
import org.easetech.easytest.reports.data.TestMethodDuration;
import org.easetech.easytest.reports.data.TestResultBean;
import org.easetech.easytest.reports.utils.ChartUtils;
import org.easetech.easytest.util.CommonUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

/**
 * This class prepares all the data before being offered to be processed by the
 * ReportRunner
 * 
 * @author gpcmol
 * 
 */
public class ReportBuilder {

	private static final String CLASS_NAME = "classname";
	public static final int MAX_METHODS_PER_GRAPH = 5;

	/**
	 * Contains all the report data
	 */
	private ReportDataContainer reportDataContainer;

	/**
	 * Constructor
	 * 
	 * @param reportDataContainer
	 */
	public ReportBuilder(ReportDataContainer reportDataContainer) {
		this.reportDataContainer = reportDataContainer;
	}

	/**
	 * Produces the main test datasource, which will be displayed in the details
	 * band of the report
	 */
	public JRDataSource buildDefaultTestReport(Map<String, Object> reportParameters) {
		JRDataSource defaultDataSource = new JRBeanCollectionDataSource(reportDataContainer.getTestResults());
		fillMainReportDataParameters(reportParameters, reportDataContainer.getClassName(), reportDataContainer.getMethodTestResults());
		return defaultDataSource;
	}

	/**
	 * Produces the method duration datasource, which will be displayed in the details band of the report
	 * @param reportParameters
	 */
	public JRDataSource buildTestMethodDurationReport(Map<String, Object> reportParameters) {
		JRDataSource methodDurationDataSource = fillMethodDurationReportDataParametersAndGetDataSource(reportParameters, reportDataContainer.getClassName(), reportDataContainer.getMethodTestResults());
		return methodDurationDataSource;
	}

	private void fillMainReportDataParameters(Map<String, Object> reportParameters, String className, Map<String, List<TestResultBean>> methodTestResults) {
		List<ReportTotalsBean> reportTotalsBeans = new ArrayList<ReportTotalsBean>();

		// Add method totals for every method to the reportParameters. These
		// will be showed in the column header band
		for (String methodName : methodTestResults.keySet()) {
			ReportTotalsBean createTestMethodSummary = this.createTestMethodSummary(methodTestResults.get(methodName));
			// add totals percentage to method totals bean
			createTestMethodSummary.setTotalsGraph(getPercentageImage(methodName, createTestMethodSummary));
			reportParameters.put(methodName, createTestMethodSummary);
			reportTotalsBeans.add(createTestMethodSummary);
		}

		ReportTotalsBean createTestClassSummary = this.createTestClassSummary(reportTotalsBeans);
		// add totals percentage to class totals bean
		createTestClassSummary.setTotalsGraph(getPercentageImage(className, createTestClassSummary));
		reportParameters.put(className, createTestClassSummary);
	}

	private JRDataSource fillMethodDurationReportDataParametersAndGetDataSource(Map<String, Object> reportParameters, String className, Map<String, List<TestResultBean>> methodTestResults) {
		List<Duration> durationBeans = new ArrayList<Duration>();
		
		// sort the methods on name
		Map<String, Duration> methodDurationBeans = CommonUtils.sortByKeys(getMethodDurationBeans(methodTestResults));
		
		// add the duration beans to the list
		for (String methodName: methodDurationBeans.keySet()) {
			Duration duration = methodDurationBeans.get(methodName);
			durationBeans.add(duration);
		}
		
		List<MethodDurationReportBean> methodDurationReportBeans = new ArrayList<MethodDurationReportBean>();
		
		// partition the max amount of method on 1 graph (in this case max # = MAX_METHODS_PER_GRAPH)
		List<List<Duration>> partitionList = CommonUtils.partitionList(durationBeans, MAX_METHODS_PER_GRAPH);
		for (List<Duration> partList : partitionList) {
			BufferedImage methodDurationImage = getMethodDurationImage(className, partList);
			ImageBean imageBean = new ImageBean(methodDurationImage, "", "");
			MethodDurationReportBean methodDurationBean = new MethodDurationReportBean();
			methodDurationBean.setImageBean(imageBean);
			methodDurationBean.setDurations(partList);
			methodDurationReportBeans.add(methodDurationBean);
		}
		
		reportParameters.put(CLASS_NAME, className);
		
		JRDataSource methodDurationDataSource = new JRBeanCollectionDataSource(methodDurationReportBeans);
		
		return methodDurationDataSource;
	}
	
	/**
	 * Produces the summary of the test methed with statistics about totals
	 * (passed/failed/exception etc.)
	 */
	public ReportTotalsBean createTestMethodSummary(List<TestResultBean> testResultBeans) {
		ReportTotalsBean testReportTotals = new ReportTotalsBean();
		for (TestResultBean testResultBean : testResultBeans) {
			if (testResultBean.getPassed() != null && testResultBean.getPassed()) {
				testReportTotals.addPassed();
			} else if (testResultBean.getPassed() != null && !testResultBean.getPassed()) {
				testReportTotals.addFailed();
			} else if (testResultBean.getException() != null && testResultBean.getException()) {
				testReportTotals.addException();
			}
		}
		return testReportTotals;
	}

	/**
	 * Produces the summary of the test class with statistics about totals
	 * (passed/failed/exception etc.) This is all the summary of the test
	 * methods accumulated
	 */
	public ReportTotalsBean createTestClassSummary(List<ReportTotalsBean> testReportTotalsList) {
		ReportTotalsBean testReportTotals = new ReportTotalsBean();
		testReportTotals.setItem("");
		for (ReportTotalsBean reportTotalsBean : testReportTotalsList) {
			testReportTotals.addPassed(reportTotalsBean.getPassed());
			testReportTotals.addFailed(reportTotalsBean.getFailed());
			testReportTotals.addException(reportTotalsBean.getException());
		}
		return testReportTotals;
	}

	/**
	 * Get a map of key = method name, value = list of test result beans
	 * Each individual TestResultBean has a list of TestMethodDuration bean (which hold the duration of each individual test method call)
	 * @param methodTestResults list of TestResultBean
	 * @return map with key = test method call and value = list of TestResultBean
	 */
	public Map<String, Duration> getMethodDurationBeans(Map<String, List<TestResultBean>> methodTestResults) {
		Map<String, Duration> resultMap = new HashMap<String, Duration>();
		
		Map<String, List<TestMethodDuration>> subResult = new HashMap<String, List<TestMethodDuration>>();
		
		for (String methodName: methodTestResults.keySet()) {
			List<TestMethodDuration> subResultBeans = subResult.get(methodName);
			if (subResultBeans == null) {
				subResultBeans = new ArrayList<TestMethodDuration>();
				subResult.put(methodName, subResultBeans);
			}
			
			List<TestResultBean> testResultBeans = methodTestResults.get(methodName);
			
			for (TestResultBean testResultBean : testResultBeans) {
				subResultBeans.addAll(testResultBean.getTestItemDurations());
			}
		}
		
		for (String methodName: subResult.keySet()) {
			List<TestMethodDuration> testMethodDurations = subResult.get(methodName);
			Duration calculateTestDurationBean = this.calculateTestDurationBean(methodName, testMethodDurations);
			resultMap.put(methodName, calculateTestDurationBean);
		}
		
		return resultMap;
	}

	/**
	 * Calculate the duration beans based on the test method duration beans
	 * @param item test method call
	 * @param testItemDurations list of test item durations
	 * @return duration bean with max/min/avg in ms
	 */
	public Duration calculateTestDurationBean(String method, List<TestMethodDuration> testMethodDurations) {
		Duration resultBean = new Duration(method);
		
		double total = 0.0;
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		int count = 0;
			
		for (TestMethodDuration testMethodDuration : testMethodDurations) {
			double timeInMs = testMethodDuration.getRoundedMsDifference().doubleValue();
			//double timeInMs = testMethodDuration.getNanoDifference();
			
			if (timeInMs > max) {
				max = timeInMs;
			}
			if (timeInMs < min) {
				min = timeInMs;
			}
			total += timeInMs;
			count++;
		}
		
		resultBean.setMin(min != Double.MAX_VALUE ? (int)Math.round(min) : 0);
		resultBean.setMax(max != Double.MIN_VALUE ? (int)Math.round(max) : 0);
		resultBean.setAvg((int)Math.round(total/count));
		resultBean.setCount(count);

		return resultBean;
	}
	
	/**
	 * Returns BufferedImage for ReportTotalsBean
	 * 
	 * @param itemName
	 * @param testReportTotals
	 * @return
	 */
	private BufferedImage getPercentageImage(String itemName, ReportTotalsBean testReportTotals) {
		Map<String, Double> totalsDatasetValuesMap = new LinkedHashMap<String, Double>();
		totalsDatasetValuesMap.put("Passed (" + testReportTotals.getPercentagePassed() + "%)", testReportTotals.getPercentagePassed());
		totalsDatasetValuesMap.put("Failed (" + testReportTotals.getPercentageFailed() + "%)", testReportTotals.getPercentageFailed());
		totalsDatasetValuesMap.put("Exception (" + testReportTotals.getPercentageException() + "%)", testReportTotals.getPercentageException());
		DefaultPieDataset pieChartDataset = ChartUtils.getPieChartDataset(totalsDatasetValuesMap);

		JFreeChart pieChart = ChartUtils.getPieChart("", pieChartDataset);
		BufferedImage percentageImage = ChartUtils.getBufferedImageChartImage(pieChart, 150, 150);
		return percentageImage;
	}
	
	/**
	 * Returns Buffered image for method duration report
	 * @param chartName
	 * @param durationBeans
	 * @return
	 */
	private BufferedImage getMethodDurationImage(String chartName, List<Duration> durationBeans) {
		CategoryDataset barDataset = ChartUtils.createDatasetDuration(durationBeans, "min", "max", "avg");
		CategoryDataset lineDataset = ChartUtils.createDatasetCountLine(durationBeans, "count");

		JFreeChart dualAxisChart = ChartUtils.getDualAxisChart(chartName, barDataset, lineDataset);
		BufferedImage methodDurationImage = ChartUtils.getBufferedImageChartImage(dualAxisChart, 800, 600);

		return methodDurationImage;
	}
}
