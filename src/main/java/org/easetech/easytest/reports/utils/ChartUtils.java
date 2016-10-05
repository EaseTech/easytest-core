package org.easetech.easytest.reports.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import org.easetech.easytest.reports.data.Duration;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

/**
 * This class contains static helper method to produce images and datasources
 * for the reports
 * 
 * @author gpcmol
 * 
 */
public class ChartUtils {
    
    private ChartUtils() {
        
    }

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
		return chart.createBufferedImage(width, height);
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
	
    /**
     * Create a data structure that holds the min, max and avg duration
     * This is used for a bar chart that represends the test/service method duration in time
     * @param inputDataset list of duration beans
     * @param labelSeries1 min
     * @param labelSeries2 max
     * @param labelSeries3 avg
     * @return category data set to be used as input for jfree chart
     */
    public static CategoryDataset createDatasetDuration(List<Duration> inputDataset, String labelSeries1, String labelSeries2, String labelSeries3) {
    	DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        for (Duration duration : inputDataset) {
        	dataset.addValue(duration.getMin(), labelSeries1, duration.getMethod());
        	dataset.addValue(duration.getMax(), labelSeries2, duration.getMethod());
        	dataset.addValue(duration.getAvg(), labelSeries3, duration.getMethod());			
		}
    	
    	return dataset;
    }
    
    /**
     * Create a data structure that hold the amount of test executions
     * This is used for a bar chart that represends the test/service method duration in time and amount of method/service executions
     * @param inputDataset list of duration beans
     * @param countLabel amount of method/service executions
     * @return category data set to be used as input for jfree chart
     */
    public static CategoryDataset createDatasetCountLine(List<Duration> inputDataset, String countLabel) {
    	DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Duration duration : inputDataset) {
        	dataset.addValue(duration.getCount(), countLabel, duration.getMethod());
		}
        
        return dataset;    	
    }
	
	/**
	 * This method creates a chart that has vertical bars and a line graph though it.
	 * @param chartTitle title of chart
	 * @param barDataset the bar series data
	 * @param lineDataset the line series dasa
	 * @return chart with min/max/avg and # of method/service executions
	 */
	public static JFreeChart getDualAxisChart(String chartTitle, CategoryDataset barDataset, CategoryDataset lineDataset) {
        String categoryLabel = "Legenda";
        String axisLabel = "Time (ms)";

        // create the chart...
        final JFreeChart chart = ChartFactory.createBarChart(
        	chartTitle,			// chart title
        	categoryLabel,               // domain axis label
        	axisLabel,                  // range axis label
        	barDataset,                 // data
            PlotOrientation.VERTICAL,
            true,                     // include legend
            false,                     // tooltips?
            false                     // URL generator?  Not required...
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chart.setBackgroundPaint(Color.white);

        // get a reference to the plot for further customisation...
        final CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(0xEE, 0xEE, 0xFF));
        plot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);

        plot.setDataset(1, lineDataset);
        plot.mapDatasetToRangeAxis(1, 1);

        final CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
        final ValueAxis axis2 = new NumberAxis("# executions");
        axis2.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        plot.setRangeAxis(1, axis2);

        final LineAndShapeRenderer renderer2 = new LineAndShapeRenderer();
        plot.setRenderer(1, renderer2);
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.REVERSE);

        return chart;
	}

}
