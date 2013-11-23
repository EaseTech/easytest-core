package org.easetech.easytest.reports.impl;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.easetech.easytest.reports.data.ReportDataContainer;
import org.easetech.easytest.reports.data.ReportTotalsBean;
import org.easetech.easytest.reports.data.TestResultBean;
import org.easetech.easytest.reports.utils.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

/**
 * This class prepares all the data before being offered to be processed by the
 * ReportRunner
 * 
 * @author gpcmol
 * 
 */
public class ReportBuilder {

	public enum REPORT_TYPES {
		DEFAULT //ALL, PERFORMANCE
	}

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
	public JRDataSource buildTestReport(Map<String, Object> reportParameters) {
		JRDataSource defaultDataSource = new JRBeanCollectionDataSource(reportDataContainer.getTestResults());
		fillReportDataParameters(reportParameters);
		return defaultDataSource;
	}

	private void fillReportDataParameters(Map<String, Object> reportParameters) {
		Map<String, List<TestResultBean>> methodTestResults = reportDataContainer.getMethodTestResults();
		List<ReportTotalsBean> reportTotalsBeans = new ArrayList<ReportTotalsBean>();

		// Add method totals for every method to the reportParameters. These
		// will be showed in the column header band
		for (String methodname : methodTestResults.keySet()) {
			ReportTotalsBean createTestMethodSummary = createTestMethodSummary(methodTestResults.get(methodname));
			// add totals percentage to method totals bean
			createTestMethodSummary.setTotalsGraph(getPercentageImage(methodname, createTestMethodSummary));
			reportParameters.put(methodname, createTestMethodSummary);
			reportTotalsBeans.add(createTestMethodSummary);
		}

		// Add class totals to the reportParameters. This will be showed in the
		// header of the report
		String className = reportDataContainer.getClassName();
		ReportTotalsBean createTestClassSummary = createTestClassSummary(reportTotalsBeans);
		// add totals percentage to class totals bean
		createTestClassSummary.setTotalsGraph(getPercentageImage(className, createTestClassSummary));
		reportParameters.put(className, createTestClassSummary);
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
	 * Produces the graphs of the totals for the test report
	 */
	public void createTestReportSummaryGraphs(ReportTotalsBean reportTotalsBean) {

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
		totalsDatasetValuesMap.put("Passed (" + testReportTotals.getPercentagePassed() + "%)",
				testReportTotals.getPercentagePassed());
		totalsDatasetValuesMap.put("Failed (" + testReportTotals.getPercentageFailed() + "%)",
				testReportTotals.getPercentageFailed());
		totalsDatasetValuesMap.put("Exception (" + testReportTotals.getPercentageException() + "%)",
				testReportTotals.getPercentageException());
		DefaultPieDataset pieChartDataset = ChartUtils.getPieChartDataset(totalsDatasetValuesMap);

		JFreeChart pieChart = ChartUtils.getPieChart("", pieChartDataset);
		BufferedImage percentageImage = ChartUtils.getBufferedImageChartImage(pieChart, 150, 150);
		return percentageImage;
	}
}
