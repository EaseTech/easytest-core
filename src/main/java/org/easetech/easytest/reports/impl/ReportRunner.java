package org.easetech.easytest.reports.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import org.easetech.easytest.annotation.Report.EXPORT_FORMAT;
import org.easetech.easytest.annotation.Report.REPORT_TYPE;
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
	
	private REPORT_TYPE[] types;

	private String destinationLocation;

	/**
	 * Constructor
	 * @param testReportContainer report container
	 * @param formats format types
	 * @param types report types (main or method duration report)
	 * @param destinationLocation destination
	 */
	public ReportRunner(ReportDataContainer testReportContainer, EXPORT_FORMAT[] formats, REPORT_TYPE[] types, String destinationLocation) {
		this.testReportContainer = testReportContainer;
		this.formats = formats;
		this.types = types;
		this.destinationLocation = destinationLocation;
		this.reportBuilder = new ReportBuilder(testReportContainer);
		this.reportExporter = new ReportExporter();
	}

	/**
	 * Prepares the report and calls the print method
	 */
	public Boolean call() throws Exception {
		Map<String, Object> reportParameters = new HashMap<String, Object>();

		JRDataSource reportDataSource;

		Boolean returnValue = true;
		
		for (REPORT_TYPE type: types) {
			if (!returnValue) {
				return false;
			}
			switch (type) {
			case DEFAULT:
				reportDataSource = reportBuilder.buildDefaultTestReport(reportParameters);
				break;
			case METHOD_DURATION:
				reportDataSource = reportBuilder.buildTestMethodDurationReport(reportParameters);
				break;
			default:
				return false;
			}
			returnValue = printReportForType(reportParameters, reportDataSource, type);
		}
		return returnValue;
	}

	/**
	 * Print report for type (main report or method dureation report)
	 * @param reportParameters report parameters
	 * @param reportDataSource datasource
	 * @param type report type
	 * @return true if succeeded, false otherwise
	 */
	private boolean printReportForType(Map<String, Object> reportParameters, JRDataSource reportDataSource, REPORT_TYPE type) {
		String className = testReportContainer.getClassName();
		reportParameters.put("TEST_CLASS_NAME", className);

		try {
			reportExporter.printReport(reportDataSource, reportParameters, destinationLocation, className,
					formats, type);
		} catch (JRException e) {
			System.out.println(e);
			e.printStackTrace();
			LOG.error("JRException occurred during generation of report", e);
			return false;
		} catch (IOException e) {
			System.out.println(e);
			LOG.error("IOException occurred during generation of report", e);
			return false;
		} catch (Exception e) {
			System.out.println(e);
			LOG.error("Exception occurred during generation of report", e);
			return false;
		}

		return true;
	}

}
