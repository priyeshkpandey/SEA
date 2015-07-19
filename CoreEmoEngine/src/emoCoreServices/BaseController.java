package emoCoreServices;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import emoUtils.PropertiesUtil;

public class BaseController {
	
	public Properties config;
	
	static Logger LOGGER = LoggerFactory.getLogger(BaseController.class);
	
	public void initConfig()
	{
		if(PropertiesUtil.loadConfig())
		{
			config = PropertiesUtil.CONFIG;
		}
		else
		{
			LOGGER.error("Config file not loaded.");
		}
	}

}
