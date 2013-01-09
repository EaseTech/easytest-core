package org.easetech.easytest.reports.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import org.easetech.easytest.annotation.Report.EXPORT_FORMAT;
import org.easetech.easytest.util.CommonUtils;

/**
 * Helper class to export the reports
 * 
 * @author gpcmol
 * 
 */
public class ReportExporter {

	//private static final String TOTALS_REPORT_JRXML = "/reports/TotalsReport.jrxml";
	private static final String DEFAULT_REPORT_JRXML = "/reports/MainReport_new.jrxml";
	private static final String DEFAULT_REPORT_JRXML_XLS = "/reports/MainReport_xls.jrxml";
	
	public void printDefaultReport(JRDataSource dataSource, Map<String, Object> jasperParameters,
			String destinationFolder, String name, EXPORT_FORMAT... formats) throws JRException, IOException {
		destinationFolder = CommonUtils.createDefaultOutputFolder(destinationFolder);

		String dateString = CommonUtils.getFormattedDate(new Date());
		exportReport(dataSource, jasperParameters, destinationFolder, name + "_" + dateString, formats);
	}

	private void exportReport(JRDataSource dataSource, Map<String, Object> jasperParameters, String destinationFolder,
			String reportName, EXPORT_FORMAT... formats) throws JRException {
		JasperReport jasperReport = null;
		JasperPrint jasperPrint = null;
		
		for (EXPORT_FORMAT export_FORMAT : formats) {
			switch (export_FORMAT) {
			case HTML:
				jasperReport = getJasperReport(DEFAULT_REPORT_JRXML);
				jasperPrint = getJasperPrint(jasperReport, dataSource, jasperParameters);
				exportHTML(destinationFolder, reportName, jasperPrint);
				break;
			case PDF:
				jasperReport = getJasperReport(DEFAULT_REPORT_JRXML);
				jasperPrint = getJasperPrint(jasperReport, dataSource, jasperParameters);
				exportPDF(destinationFolder, reportName, jasperPrint);
				break;
			case XLS:
				jasperReport = getJasperReport(DEFAULT_REPORT_JRXML_XLS);
				jasperPrint = getJasperPrint(jasperReport, dataSource, jasperParameters);
				exportXLS(destinationFolder, reportName, jasperPrint);
				break;
			default:
			}
		}
	}

	private JasperPrint getJasperPrint(JasperReport jasperReport, JRDataSource dataSource,
			Map<String, Object> jasperParameters) throws JRException {
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, jasperParameters, dataSource);
		return jasperPrint;
	}

	private JasperReport getJasperReport(String reportResource) throws JRException {
		InputStream defaultReportInputStream = ClassLoader.class.getResourceAsStream(reportResource);
		JasperReport jasperReport = JasperCompileManager.compileReport(defaultReportInputStream);
		return jasperReport;
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
