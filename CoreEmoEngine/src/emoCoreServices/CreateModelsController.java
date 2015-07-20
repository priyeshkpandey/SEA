package emoCoreServices;

import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;

import emoCoreServiceObjects.ResourceModel;
import emoDAO.ResourceModelDAO;

@RestController
@RequestMapping("/create/models")
public class CreateModelsController extends BaseController {
	
	@RequestMapping(method = RequestMethod.POST)
	public void createModels(@RequestBody List<ResourceModel> modelList)
	{
		if(loadConfig())
		{

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				config.getProperty("spring_xml"));
		
		ResourceModelDAO modelDAO = context.getBean(ResourceModelDAO.class);
		
		for(ResourceModel model:modelList)
		{
			modelDAO.saveModel(model);
		}
		
		context.close();
		}
	}

}
