package org.easetech.easytest.reports.data;

import java.io.Serializable;
import java.util.List;

/**
 * Method duration report bean that hold the duration bean data and the graph
 * @author gpcmol
 *
 */
public class MethodDurationReportBean implements Serializable {
	
	private static final long serialVersionUID = 7045417311960242599L;

	/**
	 * The bean that hold the image data
	 */
	private ImageBean imageBean;
	
	/**
	 * This list of duration beans
	 */
	private List<Duration> durations;

	/**
	 * Constructor
	 */
	public MethodDurationReportBean() {
		// empty constructor
	}

	public ImageBean getImageBean() {
		return imageBean;
	}

	public void setImageBean(ImageBean imageBean) {
		this.imageBean = imageBean;
	}

	public List<Duration> getDurations() {
		return durations;
	}

	public void setDurations(List<Duration> durations) {
		this.durations = durations;
	}

	@Override
	public String toString() {
		return "MethodDurationReportBean [imageBean=" + imageBean
				+ ", durations=" + durations + "]";
	}
	
}
