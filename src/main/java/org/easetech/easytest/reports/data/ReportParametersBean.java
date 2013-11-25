package org.easetech.easytest.reports.data;

import java.util.ArrayList;
import java.util.List;

import org.easetech.easytest.annotation.Report;
import org.easetech.easytest.annotation.Report.EXPORT_FORMAT;
import org.easetech.easytest.annotation.Report.REPORT_TYPE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This bean holds the report parameters, like output format, location etc.
 * @author gpcmol
 *
 */
public class ReportParametersBean {

	/**
	 * An instance of logger associated with the test framework.
	 */
	protected static final Logger LOG = LoggerFactory.getLogger(ReportParametersBean.class);

	/**
	 * Output formats
	 */
	private EXPORT_FORMAT[] outputFormats;
	
	/**
	 * Report types
	 */
	private REPORT_TYPE[] reportTypes;

	/**
	 * Output location
	 */
	private String outputLocation;
	
	/**
	 * Package names. Not implemented yet (feature: add report filter on package name)
	 */
	private List<String> packageNames = null;

	/**
	 * Constructor if there is no command line parameters
	 * @param outputFormats
	 * @param reportTypes
	 * @param outputLocation
	 */
	public ReportParametersBean(EXPORT_FORMAT[] outputFormats,
			REPORT_TYPE[] reportTypes, String outputLocation) {
		LOG.info("Processing reports with annotations outputFormats="
				+ outputFormats + "reportTypes = " + reportTypes
				+ " outputLocation=" + outputLocation);
		this.outputFormats = outputFormats;
		this.reportTypes = reportTypes;
		this.outputLocation = outputLocation;
	}

	// constructor that processes the command line parameters
	/**
	 * Process command line parameters -Dreports.generate : generates reports
	 * -Dreports.format=pdf : report output is pdf, (optional, default=pdf).
	 * Comma separated, valid value is pdf,xls
	 * -Dreports.type=default : report type is main report, (optional, default=default).
	 * Comma separated, valid value is default,method_duration
	 * -Dreports.location=classpath:org/easetech/easytest/output : (optional,
	 * default="" current folder). (e.g. file:c:\\temp is supported as well)
	 * @param string 
	 */
	public ReportParametersBean(String reportsFormat, String outputLocation, String packages, String reportTypes) {
		LOG.info("Processing reports with command line parameters reports.generate=true reports.format="
				+ reportsFormat + "reports.type=" + reportTypes + "reports.location=" + outputLocation + " packages=" + packages);
		this.outputFormats = initializeOutputFormats(reportsFormat);
		
		this.reportTypes = initializeReportTypes(reportTypes);

		this.outputLocation = outputLocation != null ? outputLocation : "";

		// parse package names
		if (packages != null) {
			this.packageNames = new ArrayList<String>();
			String[] packagesArray = packages.split(",");
			for (String packageName : packagesArray) {
				this.packageNames.add(packageName.trim());
			}
		}
	}

	/**
	 * Parse the report format outputs to enums
	 * @param reportsFormat
	 * @return export formats
	 */
	private EXPORT_FORMAT[] initializeOutputFormats(String reportsFormat) {
		// parsing the comma separated output formats
		List<EXPORT_FORMAT> formatResults = new ArrayList<Report.EXPORT_FORMAT>();
		if (reportsFormat != null) {
			String[] formats = reportsFormat.split(",");
			for (String format : formats) {
				try {
					formatResults.add(EXPORT_FORMAT.valueOf(format.toUpperCase().trim()));
				} catch (Exception e) {
					LOG.error("Report format " + format + " not supported", e);
				}
			}
		}

		if (formatResults.isEmpty()) {
			formatResults.add(EXPORT_FORMAT.PDF); // adding PDF as default if
													// empty
			LOG.info("Outputting to PDF as default format");
		}
		return formatResults.toArray(new EXPORT_FORMAT[formatResults.size()]);
	}

	/**
	 * Parse the report types (main report, method duration) to enums
	 * @param reportTypes
	 * @return report types
	 */
	private REPORT_TYPE[] initializeReportTypes(String reportTypes) {
		// parsing the comma separated report types
		List<REPORT_TYPE> reportTypesResults = new ArrayList<Report.REPORT_TYPE>();
		if (reportTypes != null) {
			String[] types = reportTypes.split(",");
			for (String type : types) {
				try {
					reportTypesResults.add(REPORT_TYPE.valueOf(type.toUpperCase().trim()));
				} catch (Exception e) {
					LOG.error("Report format " + type + " not supported", e);
				}
			}
		}

		if (reportTypesResults.isEmpty()) {
			reportTypesResults.add(REPORT_TYPE.DEFAULT); // adding DEFAULT (main report) as default if
													// empty
			LOG.info("Report type set to DEFAULT as default report type");
		}
		return reportTypesResults.toArray(new REPORT_TYPE[reportTypesResults.size()]);
	}

	public EXPORT_FORMAT[] getOutputFormats() {
		return outputFormats;
	}
	
	public REPORT_TYPE[] getReportTypes() {
		return reportTypes;
	}

	public String getOutputLocation() {
		return outputLocation;
	}

	public List<String> getPackageNames() {
		return packageNames;
	}
}
