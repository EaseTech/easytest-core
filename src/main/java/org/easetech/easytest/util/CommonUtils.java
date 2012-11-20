package org.easetech.easytest.util;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains common utils
 * 
 * @author gpcmol
 * 
 */
public class CommonUtils {

	private static final Logger LOG = LoggerFactory.getLogger(CommonUtils.class);

	private static final String FILE_SEPARATOR = String.valueOf(File.separatorChar);

	/**
	 * Rounds a value with number of decimals
	 * 
	 * @param valueToRound
	 * @param numberOfDecimalPlaces
	 * @return rounded double
	 */
	public static Double getRounded(double valueToRound, int numberOfDecimalPlaces) {
		BigDecimal bigDecimal = new BigDecimal(valueToRound).setScale(numberOfDecimalPlaces, RoundingMode.HALF_UP);
		return bigDecimal.doubleValue();
	}

	/**
	 * Create directory
	 * 
	 * @param destinationFolder
	 * @return a string representing the path to the output folder.
	 */
	public static String createDefaultOutputFolder(String destinationFolder) {
		if (destinationFolder == null || destinationFolder.equals("")) {
			destinationFolder = System.getProperty("user.dir") + File.separatorChar + "target" + File.separatorChar
					+ "reports";
		}
		return createFolder(destinationFolder);
	}
	
	public static String getCurrentFolder() {
		File file = new File("");
		String absolutePath = file.getAbsolutePath();
		return absolutePath;
	}
	
	/**
	 * Creates directory. If absolute location is empty, pick current folder
	 * @param absoluteLocation
	 * @return directory
	 */
	public static String createFolder(String absoluteLocation) {
		if (absoluteLocation == null) {
			return null;
		}
		File file = new File(absoluteLocation);
		if (!file.isDirectory()) {
			try {
				FileUtils.forceMkdir(file);
			} catch (IOException e) {
				LOG.error("Error creating directory " + absoluteLocation + " (" + e.getMessage() + ")");
			}
		}
		return absoluteLocation;
	}

	/**
	 * Format date with pattern ddMMyyyyHHmmss
	 * 
	 * @param date
	 * @return string with formatted date
	 */
	public static String getFormattedDate(Date date) {
		DateFormat instance = new SimpleDateFormat("ddMMyyyyHHmmss");
		return instance.format(date);
	}

	/**
	 * Returns absulute path of either the classpath of file location
	 * 
	 * @param location
	 * @return absolute location
	 */
	public static String getAbsoluteLocation(String location) {
		String path = null;
		
		if (location.equals("")) {
			return CommonUtils.getCurrentFolder();
		}

		if (location.startsWith("file:")) {
			path = location.substring(location.indexOf(":") + 1, location.length());
		} else if (location.startsWith("classpath:")) {
			path = location.substring(location.indexOf(":") + 1, location.length());
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			URL url = classLoader.getResource(".");
			try {
				File file = new File(url.toURI());
				String tempPath = file.toString();
				if (!tempPath.endsWith(FILE_SEPARATOR)) {
					tempPath += FILE_SEPARATOR;
				}
				path = path.replace("/", FILE_SEPARATOR);
				path = tempPath + path;
			} catch (URISyntaxException e) {
				LOG.error("URI exception ", e);
			}
		} else {
			LOG.error("Report output location " + location + " not found");
		}
		return path;
	}

}
