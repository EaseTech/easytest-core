package org.easetech.easytest.reports.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import org.easetech.easytest.annotation.Report.EXPORT_FORMAT;
import org.easetech.easytest.reports.data.ReportDataContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class receives the preprared data from the ReportBuilder and runs the
 * reports
 * 
 * @author gpcmol
 * 
 */
public class ReportRunner implements Callable<Boolean> {
	private static final Logger LOG = LoggerFactory.getLogger(ReportRunner.class);

	private ReportBuilder reportBuilder;
	private ReportExporter reportExporter;
	private ReportDataContainer testReportContainer;

	private EXPORT_FORMAT[] formats;

	private String destinationLocation;

	/**
	 * Constructor
	 * @param testReportContainer The class that holds the report data
	 * @param formats The export format types (PDF, HTML, XLS)
	 * @param destinationLocation The final export destination location
	 */
	public ReportRunner(ReportDataContainer testReportContainer, EXPORT_FORMAT formats[], String destinationLocation) {
		this.testReportContainer = testReportContainer;
		this.formats = formats;
		this.destinationLocation = destinationLocation;
		this.reportBuilder = new ReportBuilder(testReportContainer);
		this.reportExporter = new ReportExporter();
	}

	/**
	 * The actual export runs in a seperate thread
	 */
	public Boolean call() throws Exception {
		Map<String, Object> reportParameters = new HashMap<String, Object>();

		JRDataSource reportDataSource = reportBuilder.buildTestReport(reportParameters);
		
		String className = testReportContainer.getClassName();
		reportParameters.put("TEST_CLASS_NAME", className);
		
		try {
			reportExporter.printDefaultReport(reportDataSource, reportParameters, destinationLocation, className,
					formats);
		} catch (JRException e) {
			System.out.println(e);
			LOG.error("JRException occurred during generation of report", e);
			return false;
		} catch (IOException e) {
			System.out.println(e);
			LOG.error("IOException occurred during generation of report", e);
			return false;
		}

		return true;
	}

}
