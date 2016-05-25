package org.easetech.easytest.reports.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import org.easetech.easytest.annotation.Report;
import org.easetech.easytest.annotation.Report.EXPORT_FORMAT;
import org.easetech.easytest.annotation.Report.REPORT_TYPE;
import org.easetech.easytest.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class to export the reports
 * 
 * @author gpcmol
 * 
 */
public class ReportExporter {
	private static final Logger LOG = LoggerFactory.getLogger(ReportExporter.class);

	/* The default main report */
	private static final String DEFAULT_REPORT_JRXML = "/reports/MainReport_pdf.jrxml";
	private static final String DEFAULT_REPORT_JRXML_XLS = "/reports/MainReport_xls.jrxml";
	private JasperReport defaultMainReport;
	private JasperReport defaultMainReport_xls;

	/* The method duration report */
	private static final String METHOD_DURATION_REPORT_JRXML = "/reports/MethodDurationReport_pdf.jrxml";
	private static final String METHOD_DURTION_REPORT_JRXML_XLS = "/reports/MethodDurationReport_xls.jrxml";
	private JasperReport methodDurationReport;
	private JasperReport methodDurationReport_xls;

	/**
	 * Constructor
	 * Compile the reports
	 */
	public ReportExporter() {
		try {
			defaultMainReport = getJasperReport(DEFAULT_REPORT_JRXML);
			defaultMainReport_xls = getJasperReport(DEFAULT_REPORT_JRXML_XLS);
			methodDurationReport = getJasperReport(METHOD_DURATION_REPORT_JRXML);
			methodDurationReport_xls = getJasperReport(METHOD_DURTION_REPORT_JRXML_XLS);
		} catch (JRException e) {
			System.out.println(e);
			LOG.error("Error compiling report", e);
		}
	}
	
	public void printReport(JRDataSource dataSource, Map<String, Object> jasperParameters,
			String destinationFolder, String name, EXPORT_FORMAT[] formats, REPORT_TYPE type) throws JRException, IOException {
		switch (type) {
			case DEFAULT:
				printDefaultReport(dataSource, jasperParameters, destinationFolder, name, formats);
				break;
			case METHOD_DURATION:
				printMethodDurationReport(dataSource, jasperParameters, destinationFolder, name, formats);
				break;
			default:
				break;
		}
	}
	
	/**
	 * Print the default report. This report is the main report
	 * @param dataSource
	 * @param jasperParameters
	 * @param destinationFolder
	 * @param name
	 * @param formats
	 * @throws JRException
	 * @throws IOException
	 */
	private void printDefaultReport(JRDataSource dataSource, Map<String, Object> jasperParameters,
			String destinationFolder, String name, EXPORT_FORMAT... formats) throws JRException, IOException {
		this.exportReport(dataSource, jasperParameters, destinationFolder, name, Report.REPORT_TYPE.DEFAULT, formats);
	}

	/**
	 * Print the method duration report. This report is the method duration report
	 * @param dataSource
	 * @param jasperParameters
	 * @param destinationFolder
	 * @param name
	 * @param formats
	 * @throws JRException
	 * @throws IOException
	 */
	private void printMethodDurationReport(JRDataSource dataSource, Map<String, Object> jasperParameters,
			String destinationFolder, String name, EXPORT_FORMAT... formats) throws JRException, IOException {
		this.exportReport(dataSource, jasperParameters, destinationFolder, name, Report.REPORT_TYPE.METHOD_DURATION, formats);
	}

	private void exportReport(JRDataSource dataSource, Map<String, Object> jasperParameters, String destinationFolder,
			String reportName, Report.REPORT_TYPE type, EXPORT_FORMAT... formats) throws JRException {
		destinationFolder = CommonUtils.createDefaultOutputFolder(destinationFolder);
		reportName += "_" + CommonUtils.getFormattedDate(new Date());
		
		for (EXPORT_FORMAT export_FORMAT : formats) {
			JasperReport jasperReport = this.getCompiledReport(type, export_FORMAT);
			JasperPrint jasperPrint = null;

			// datasource can't be reused, so it needs to be instantiated again
			Collection<?> data = ((JRBeanCollectionDataSource)dataSource).getData();
			JRBeanCollectionDataSource beanCollectionDataSource = 
					new JRBeanCollectionDataSource(data);

			switch (export_FORMAT) {
			case HTML:
				jasperPrint = getJasperPrint(jasperReport, beanCollectionDataSource, jasperParameters);
				exportHTML(destinationFolder, reportName, jasperPrint);
				break;
			case PDF:
				jasperPrint = getJasperPrint(jasperReport, beanCollectionDataSource, jasperParameters);
				exportPDF(destinationFolder, reportName, jasperPrint);
				break;
			case XLS:
				jasperPrint = getJasperPrint(jasperReport, beanCollectionDataSource, jasperParameters);
				exportXLS(destinationFolder, reportName, jasperPrint);
				break;
			default:
			}
		}
	}

	/**
	 * Select the right compiled report
	 * @param type type of report DEFAULT or METHOD_DURATION
	 * @param export_FORMAT format type PDF/HTML/XLS
	 * @return the compiled report
	 */
	private JasperReport getCompiledReport(Report.REPORT_TYPE type, EXPORT_FORMAT export_FORMAT) {
		if (type == Report.REPORT_TYPE.DEFAULT) {
			if (export_FORMAT == EXPORT_FORMAT.XLS) {
				return defaultMainReport_xls;
			} else {
				return defaultMainReport;
			}
		} else if (type == Report.REPORT_TYPE.METHOD_DURATION) {
			if (export_FORMAT == EXPORT_FORMAT.XLS) {
				return methodDurationReport_xls;
			} else {
				return methodDurationReport;
			}
		}
		return null;
	}
	
	private JasperPrint getJasperPrint(JasperReport jasperReport, JRDataSource dataSource,
			Map<String, Object> jasperParameters) throws JRException {
		return JasperFillManager.fillReport(jasperReport, jasperParameters, dataSource);
	}

	private JasperReport getJasperReport(String reportResource) throws JRException {
		InputStream defaultReportInputStream = ClassLoader.class.getResourceAsStream(reportResource);
		return JasperCompileManager.compileReport(defaultReportInputStream);
	}

	private void exportPDF(String destinationFolder, String reportName, JasperPrint jasperPrint) throws JRException {
		JasperExportManager.exportReportToPdfFile(jasperPrint, destinationFolder + File.separatorChar + reportName
				+ "." + EXPORT_FORMAT.PDF.toString().toLowerCase());
	}

	private void exportHTML(String destinationFolder, String reportName, JasperPrint jasperPrint) throws JRException {
		JRHtmlExporter exporter = new JRHtmlExporter();
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destinationFolder + File.separatorChar + reportName
				+ "." + EXPORT_FORMAT.HTML.toString().toLowerCase());
		exporter.setParameter(JRHtmlExporterParameter.IGNORE_PAGE_MARGINS, true);
		exporter.setParameter(JRHtmlExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, true);
		exporter.setParameter(JRHtmlExporterParameter.IS_WHITE_PAGE_BACKGROUND, false);
		exporter.exportReport();
	}

	private void exportXLS(String destinationFolder, String reportName, JasperPrint jasperPrint) throws JRException {
		JRXlsExporter exporter = new JRXlsExporter();
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destinationFolder + File.separatorChar + reportName
				+ "." + EXPORT_FORMAT.XLS.toString().toLowerCase());
		exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE);
		exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
		exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
		exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
		exporter.exportReport();
	}	

}
