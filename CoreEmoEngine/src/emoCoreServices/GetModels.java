package emoCoreServices;

import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import emoCoreServiceObjects.ResourceModel;
import emoDAO.ResourceModelDAO;

@RestController
@RequestMapping("/get/models")
public class GetModels extends BaseController {

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody List<ResourceModel> getModels(
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "simId") Long simId) {
		initConfig();

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				config.getProperty("spring_xml"));

		ResourceModelDAO modelDAO = context.getBean(ResourceModelDAO.class);

		List<ResourceModel> response = modelDAO.getModelsBySimUserId(userId,
				simId);
		context.close();
		
		return response;
	}

}
