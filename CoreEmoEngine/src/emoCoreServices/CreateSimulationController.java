package emoCoreServices;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;

import emoCoreServiceObjects.Simulation;
import emoDAO.SimulationDAO;

@RestController
@RequestMapping("/create/simulation")
public class CreateSimulationController extends BaseController {

	@RequestMapping(method = RequestMethod.POST)
	public void createSimulation(@RequestBody Simulation sim) {
		
		if(loadConfig())
		{	

			AbstractApplicationContext context = new ClassPathXmlApplicationContext(
				config.getProperty("spring_xml"));
		
		SimulationDAO simDAO = context.getBean(SimulationDAO.class);
		
		simDAO.saveSimulation(sim);
		
		context.close();
		}
	}

}
