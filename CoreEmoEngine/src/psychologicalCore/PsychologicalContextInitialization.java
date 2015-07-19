package psychologicalCore;


import java.util.Properties;

import emoUtils.PropertiesUtil;

public class PsychologicalContextInitialization {
	
	private Properties configs;
	private static boolean isInitialized = false;
	
	public void initialize()
	{		
		
			if(PropertiesUtil.loadConfig())
			{
				configs = PropertiesUtil.CONFIG;
			}
			ConstantVariables.dbURL = configs.getProperty("database_url");
			ConstantVariables.dbDriver = configs.getProperty("db_driver");
			ConstantVariables.dbName = configs.getProperty("database_name");
			ConstantVariables.dbUserName = configs.getProperty("db_user_name");
			ConstantVariables.dbPassword = configs.getProperty("db_user_password");
			isInitialized = true;
		
		
	}
	
	public static boolean isInitialized()
	{
		return isInitialized;
	}
	
	public String getProperty(String key)
	{
		return configs.getProperty(key);
	}

}
