package org.easetech.easytest.reports.data;

import java.util.ArrayList;
import java.util.List;

import org.easetech.easytest.annotation.Report;
import org.easetech.easytest.annotation.Report.EXPORT_FORMAT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This pojo holds the report parameters, like output format, location etc.
 * This information can be given by the @Report annotation or the command
 * line parameters otherwise
 * 
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
	 * @param outputLocation
	 */
	public ReportParametersBean(EXPORT_FORMAT[] outputFormats, String outputLocation) {
		LOG.info("Processing reports with annotations outputFormats=" + outputFormats + " outputLocation="
				+ outputLocation);
		this.outputFormats = outputFormats;
		this.outputLocation = outputLocation;
	}

	// constructor that processes the command line parameters
	/**
	 * Process command line parameters -Dreports.generate : generates reports
	 * -Dreports.format=pdf : report output is pdf, (optional, default=pdf).
	 * Comma separated, valid value is pdf,xls
	 * -Dreports.location=classpath:org/easetech/easytest/output : (optional,
	 * default="" current folder). (e.g. file:c:\\temp is supported as well)
	 */
	public ReportParametersBean(String reportsFormat, String outputLocation, String packages) {
		LOG.info("Processing reports with command line parameters reports.generate=true reports.format="
				+ reportsFormat + " reports.location=" + outputLocation + " packages=" + packages);
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
		this.outputFormats = formatResults.toArray(new EXPORT_FORMAT[formatResults.size()]);

		this.outputLocation = outputLocation != null ? outputLocation : "";

		// parse package names, not yet implemented
		if (packages != null) {
			this.packageNames = new ArrayList<String>();
			String[] packagesArray = packages.split(",");
			for (String packageName : packagesArray) {
				this.packageNames.add(packageName.trim());
			}
		}
	}

	public EXPORT_FORMAT[] getOutputFormats() {
		return outputFormats;
	}

	public String getOutputLocation() {
		return outputLocation;
	}

	public List<String> getPackageNames() {
		return packageNames;
	}
}
