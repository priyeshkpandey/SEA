package com.services;


import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class BaseController {

	public Properties config;

	static Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

	static {
		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "DEBUG");
	}

	public boolean loadConfig() {
		try {

			
			ClassLoader classLoader = Thread.currentThread()
					.getContextClassLoader();
			config = new Properties();
			System.out.println(classLoader);
			config.load(
					classLoader.getResourceAsStream("psychologicalContext.properties"));
		} catch (IOException ioe) {
			LOGGER.error("IO Exception occurred while reading psychologicalContext.properties: "
					+ ioe.getStackTrace());
			ioe.printStackTrace();
			return false;
		} catch (Exception e) {
			LOGGER.error("Exception occurred while reading psychologicalContext.properties: "
					+ e.getStackTrace());
			e.printStackTrace();
			return false;
		}

		return true;

	}

}
