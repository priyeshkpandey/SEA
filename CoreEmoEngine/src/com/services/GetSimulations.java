package com.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.services.dao.SimulationDAO;
import com.services.entities.Simulation;

@RestController
@RequestMapping("/get/simulations")
public class GetSimulations extends BaseController {

	@Autowired
	ApplicationContext context;
	
	@RequestMapping(method=RequestMethod.GET)
	public @ResponseBody List<Simulation> getSimulations(
			@RequestParam(value = "userId") String userId) {
		
		if(loadConfig())
		{	
			
		SimulationDAO simDAO = context.getBean(SimulationDAO.class);
		
		List<Simulation> response = simDAO.getSimulationsByUser(userId);
		
		
		return response;
		}
		
		return null;

	}

}
