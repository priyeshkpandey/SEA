package psychologicalCore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PsychologicalContextInitialization {
	
	private Properties configs = new Properties();
	private String configsFilePath = "psychologicalConext.properties";
	private static boolean isInitialized = false;
	
	public void initialize()
	{		
		try {
			InputStream inStreamProperties = getClass().getClassLoader().getResourceAsStream(configsFilePath);
			configs.load(inStreamProperties);
			ConstantVariables.dbURL = configs.getProperty("database_url");
			ConstantVariables.dbDriver = configs.getProperty("db_driver");
			ConstantVariables.dbName = configs.getProperty("database_name");
			ConstantVariables.dbUserName = configs.getProperty("db_user_name");
			ConstantVariables.dbPassword = configs.getProperty("db_user_password");
			isInitialized = true;
		}
		catch(FileNotFoundException fnfe)
		{
			System.out.println("Properties file not found!");
			isInitialized =  false;
		}
		catch (IOException e) {
			isInitialized =  false;
		}
		catch (Exception e) {
			isInitialized = false;
		}
		
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
