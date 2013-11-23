package org.easetech.easytest.reports.utils;

import java.awt.Color;
import java.util.List;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

/**
 * Class for rendering customized colors for the purpose of PieChart
 * @author gpcmol
 *
 */
public class PieChartColorRendererHelper {
	private Color[] color;

	/**
	 * Constructor
	 * @param color colors array
	 */
	public PieChartColorRendererHelper(Color[] color) {
		this.color = color;
	}

	/**
	 * Sets the color for dataset (ordered on index)
	 * @param plot
	 * @param dataset
	 */
	@SuppressWarnings("rawtypes")
	public void setColor(PiePlot plot, DefaultPieDataset dataset) {
		@SuppressWarnings("unchecked")
		List<Comparable> keys = dataset.getKeys();

		for (int i = 0; i < keys.size(); i++) {
			plot.setSectionPaint(keys.get(i), this.color[i]);
		}
	}
}
