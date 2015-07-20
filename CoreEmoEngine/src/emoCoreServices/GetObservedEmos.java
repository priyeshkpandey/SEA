package emoCoreServices;

import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;

import emoCoreServiceObjects.ObservedEmotions;
import emoDAO.ObservedEmotionsDAO;

@RestController
@RequestMapping("/get/emotions")
public class GetObservedEmos extends BaseController {

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody List<ObservedEmotions> getObservedEmos(
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "simId") Long simId) {

		if(loadConfig())
		{

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				config.getProperty("spring_xml"));

		ObservedEmotionsDAO obsEmosDAO = context
				.getBean(ObservedEmotionsDAO.class);

		List<ObservedEmotions> response = obsEmosDAO.getObsEmosByUserSimId(
				userId, simId);
		context.close();

		return response;
		}
		System.out.println("Config not loaded.");
		return null;

	}
}
