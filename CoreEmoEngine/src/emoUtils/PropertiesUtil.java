package emoUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class PropertiesUtil {
	
	static {
		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "DEBUG");
	}
	
	static Logger LOGGER = LoggerFactory.getLogger(PropertiesUtil.class);
	
	public static Properties CONFIG;
	
	public static boolean loadConfig() {
		try {

			String fileSeparator = System.getProperty("file.separator");
			FileInputStream propFileStream = new FileInputStream(new File(
					"resources" + fileSeparator
							+ "psychologicalContext.properties"));
			CONFIG = new Properties();
			CONFIG.load(propFileStream);
		} catch (IOException ioe) {
			LOGGER.error("IO Exception occurred while reading config.properties: "
					+ ioe.getStackTrace());
			return false;
		} catch (Exception e) {
			LOGGER.error("Exception occurred while reading config.properties: "
					+ e.getStackTrace());
			return false;
		}

		return true;

	}

	
}
