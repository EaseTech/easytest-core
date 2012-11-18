package org.easetech.easytest.reports.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

/**
 * This class contains static helper method to produce images and datasources
 * for the reports
 * 
 * @author gpcmol
 * 
 */
public class ChartUtils {

	/**
	 * Returns a BufferedImage of JFreeChart object
	 * 
	 * @param chart
	 *            JFreeChart object
	 * @param width
	 *            width
	 * @param height
	 *            height
	 * @return BufferedImage
	 */
	public static BufferedImage getBufferedImageChartImage(JFreeChart chart, int width, int height) {
		BufferedImage createBufferedImage = chart.createBufferedImage(width, height);
		return createBufferedImage;
	}

	/**
	 * Returns JFree chart for dataset
	 * 
	 * @param title
	 * @param dataset
	 * @return JFreeChart
	 */
	public static JFreeChart getPieChart(String title, DefaultPieDataset dataset) {
		JFreeChart pieChart = ChartFactory.createPieChart(title, dataset, true, // legend
				false, // tooltips
				false // URLs
				);

		PiePlot plot = (PiePlot) pieChart.getPlot();
		plot.setLabelGenerator(null);

		// green=passed, red=failed, grey=exception
		Color[] colors = { Color.GREEN, Color.RED, Color.GRAY };
		PieChartColorRendererHelper renderer = new PieChartColorRendererHelper(colors);
		renderer.setColor(plot, dataset);

		return pieChart;
	}

	/**
	 * Returns a PieDataset
	 * 
	 * @param datasetValues
	 * @return DefaultPieDataset
	 */
	public static DefaultPieDataset getPieChartDataset(Map<String, Double> datasetValues) {
		DefaultPieDataset dataset = new DefaultPieDataset();

		for (String datasetValueKey : datasetValues.keySet()) {
			dataset.setValue(datasetValueKey, datasetValues.get(datasetValueKey));
		}

		return dataset;
	}

}
