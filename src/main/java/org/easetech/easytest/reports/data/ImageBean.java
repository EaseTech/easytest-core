package org.easetech.easytest.reports.data;

import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * this class holds the image and description data
 * @author gpcmol
 *
 */
public class ImageBean implements Serializable {

	private static final long serialVersionUID = 4057076384657248894L;

	/**
	 * Chart of the totals
	 */
	private BufferedImage image;

	/**
	 * Name
	 */
	private String name;

	/**
	 * Description
	 */
	private String description;

	/**
	 * Constructor
	 * 
	 * @param totalsGraph
	 * @param name
	 * @param description
	 */
	public ImageBean(BufferedImage image, String name, String description) {
		super();
		this.image = image;
		this.name = name;
		this.description = description;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "ImageBean [image=" + image + ", name=" + name
				+ ", description=" + description + "]";
	}

}
