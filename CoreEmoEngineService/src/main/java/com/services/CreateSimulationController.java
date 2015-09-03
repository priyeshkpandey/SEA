package com.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;

import com.services.dao.SimulationDAO;
import com.services.entities.Simulation;

@RestController
@RequestMapping("/create/simulation")
public class CreateSimulationController extends BaseController {

	@Autowired
	ApplicationContext context;

	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody Simulation createSimulation(@RequestBody Simulation sim) {

		SimulationDAO simDAO = context.getBean(SimulationDAO.class);

		simDAO.save(sim);

		Simulation createdSim = simDAO.getSimulationByUserAndSimId(
				sim.getUserId(), sim.getSimId());
		
		return createdSim;

	}

}
