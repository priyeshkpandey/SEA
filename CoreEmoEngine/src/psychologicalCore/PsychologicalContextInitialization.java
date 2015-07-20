package psychologicalCore;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import emoCoreServices.BaseController;
import emoUtils.PropertiesUtil;

public class PsychologicalContextInitialization {
	
	private Properties config;
	private static boolean isInitialized = false;
	
static Logger LOGGER = LoggerFactory.getLogger(PsychologicalContextInitialization.class);
	
	static {
		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "DEBUG");
	}
	
	public void initialize()
	{		
		
			if(loadConfig())
			{
				ConstantVariables.dbURL = config.getProperty("database_url");
				ConstantVariables.dbDriver = config.getProperty("db_driver");
				ConstantVariables.dbName = config.getProperty("database_name");
				ConstantVariables.dbUserName = config.getProperty("db_user_name");
				ConstantVariables.dbPassword = config.getProperty("db_user_password");
				isInitialized = true;
			}
			
		
		
	}
	
	public static boolean isInitialized()
	{
		return isInitialized;
	}
	
	public String getProperty(String key)
	{
		return config.getProperty(key);
	}
	
	public boolean loadConfig() {
		try {

			String fileSeparator = System.getProperty("file.separator");
			FileInputStream propFileStream = new FileInputStream(new File(
					"resources" + fileSeparator
							+ "psychologicalContext.properties"));
			config = new Properties();
			config.load(propFileStream);
		} catch (IOException ioe) {
			LOGGER.error("IO Exception occurred while reading psychologicalContext.properties: "
					+ ioe.getStackTrace());
			return false;
		} catch (Exception e) {
			LOGGER.error("Exception occurred while reading psychologicalContext.properties: "
					+ e.getStackTrace());
			return false;
		}

		return true;

	}

}
