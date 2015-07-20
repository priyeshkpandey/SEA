package emoCoreServices;

import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import emoCoreServiceObjects.Simulation;
import emoDAO.SimulationDAO;

@RestController
@RequestMapping("/get/simulations")
public class GetSimulations extends BaseController {

	@RequestMapping(method=RequestMethod.GET)
	public @ResponseBody List<Simulation> getSimulations(
			@RequestParam(value = "userId") String userId) {
		
		if(loadConfig())
		{	

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				config.getProperty("spring_xml"));
		
		SimulationDAO simDAO = context.getBean(SimulationDAO.class);
		
		List<Simulation> response = simDAO.getSimulationsByUser(userId);
		context.close();
		
		return response;
		}
		
		return null;

	}

}
