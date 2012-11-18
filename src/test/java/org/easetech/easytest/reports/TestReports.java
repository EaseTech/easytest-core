package org.easetech.easytest.reports;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import org.easetech.easytest.reports.data.ReportTotalsBean;
import org.easetech.easytest.reports.utils.ChartUtils;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.junit.Test;

public class TestReports {

//	@Test
//	public void testReports() {
//
//		String className = "TestRouteCalculationBusiness";
		
//		ReportDataContainer testReportContainer = new ReportDataContainer(className);
//		Map<String, Object> input1 = new HashMap<String, Object>();
//		input1.put("name", "molly");
//		input1.put("cars", 0);
//		input1.put("object", new Object());
//		Object output1 = new HashMap();
//		String output2 = "OUTPUT string";
//		
//		testReportContainer.addTestResult("method1", input1, output1, true, null, null, null, new Date(), getDymmyDate());
//		testReportContainer.addTestResult("method1", new HashMap<String, Object>(), null, true, null, null, null, new Date(), getDymmyDate());
//		testReportContainer.addTestResult("method1", new HashMap<String, Object>(), null, false, "string not blank", null, null, new Date(), getDymmyDate());
//		testReportContainer.addTestResult("method1", new HashMap<String, Object>(), null, false, "int too high", false, null, new Date(), getDymmyDate());
//		testReportContainer.addTestResult("method1", new HashMap<String, Object>(), null, null, null, true, "NoDataAccessException", new Date(), getDymmyDate());
//		testReportContainer.addTestResult("method1", new HashMap<String, Object>(), null, null, null, true, "RuntimeException", new Date(), getDymmyDate());
//		testReportContainer.addTestResult("method1", new HashMap<String, Object>(), null, true, null, null, null, new Date(), getDymmyDate());
//		testReportContainer.addTestResult("method1", new HashMap<String, Object>(), null, true, null, null, null, new Date(), getDymmyDate());
//		testReportContainer.addTestResult("method1", new HashMap<String, Object>(), output2, true, null, null, null, new Date(), getDymmyDate());
//		testReportContainer.addTestResult("method1", new HashMap<String, Object>(), null, true, null, null, null, new Date(), getDymmyDate());
//
//		testReportContainer.addTestResult("method2", new HashMap<String, Object>(), null, true, "", null, "", new Date(), getDymmyDate());
//		testReportContainer.addTestResult("method2", new HashMap<String, Object>(), null, true, "", null, "", new Date(), getDymmyDate());
//		testReportContainer.addTestResult("method2", new HashMap<String, Object>(), null, false, "", null, "", new Date(), getDymmyDate());
//		testReportContainer.addTestResult("method2", new HashMap<String, Object>(), null, false, "", null, "", new Date(), getDymmyDate());
//		testReportContainer.addTestResult("method2", new HashMap<String, Object>(), null, null, "", true, "", new Date(), getDymmyDate());
//		testReportContainer.addTestResult("method2", new HashMap<String, Object>(), null, null, "", true, "", new Date(), getDymmyDate());
//		testReportContainer.addTestResult("method2", new HashMap<String, Object>(), null, true, "", null, "", new Date(), getDymmyDate());
//		testReportContainer.addTestResult("method2", new HashMap<String, Object>(), null, true, "", null, "", new Date(), getDymmyDate());
//		testReportContainer.addTestResult("method2", new HashMap<String, Object>(), null, true, "", null, "", new Date(), getDymmyDate());
//		testReportContainer.addTestResult("method2", new HashMap<String, Object>(), null, true, "", null, "", new Date(), getDymmyDate());
//
//		ReportRunner testReportHelper = new ReportRunner();
//		testReportHelper.createReports(testReportContainer);
//
//	}

	private Date getDymmyDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MILLISECOND, 0 + (int) (Math.random() * 2000));
		return calendar.getTime();
	}

	@Test
	public void testPieChart() {
		String locationToSaveFile = "c:/temp";
		JFreeChart chart = getDummyChart();

		int width = 300;
		int height = 300;
		try {
			File file = new File(locationToSaveFile + "/dummychart.png");
			ChartUtilities.saveChartAsPNG(file, chart, width, height);
			BufferedImage createBufferedImage = chart.createBufferedImage(width, height);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private JFreeChart getDummyChart() {
		ReportTotalsBean testReportTotals = new ReportTotalsBean();
		testReportTotals.setPassed(12L);
		testReportTotals.setFailed(3L);
		testReportTotals.setException(2L);

		// create a dataset...
		DefaultPieDataset dataset = new DefaultPieDataset();
		dataset.setValue("Passed ("+testReportTotals.getPercentagePassed()+"%)", testReportTotals.getPercentagePassed());
		dataset.setValue("Failed ("+testReportTotals.getPercentageFailed()+"%)", testReportTotals.getPercentageFailed());
		dataset.setValue("Exception ("+testReportTotals.getPercentageException()+"%)", testReportTotals.getPercentageException());

		JFreeChart pieChart = ChartUtils.getPieChart("TotalsReport", dataset);
		BufferedImage bufferedImageChartImage = ChartUtils.getBufferedImageChartImage(pieChart, 400, 400);

		return pieChart;
	}

}
