package com.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import psychologicalCore.ConstantVariables;

import com.services.dao.ResourceModelDAO;
import com.services.dao.SimulationDAO;
import com.services.entities.ResourceModel;
import com.services.entities.Simulation;

@RestController
@RequestMapping("/initiate/simultion")
public class InitiateSimulation extends BaseController {

	@Autowired
	ApplicationContext context;

	@RequestMapping(method = RequestMethod.POST)
	public void initiateSimulation(
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "simId") Long simId,
			@RequestParam(value = "avgN") Integer avgN,
			@RequestParam(value = "mode") String mode) {

	}

	@Async
	public void initSimulation(String userId, Long simId, Integer avgN,
			String mode) {
		SimulationDAO simDAO = context.getBean(SimulationDAO.class);
		Simulation simToRun = simDAO.getSimulationByUserAndSimId(userId, simId);

		ConstantVariables.simulationId = simToRun.getSimId();
		ConstantVariables.userID = simToRun.getUserId();

		while ((simToRun.getCurrIter() <= simToRun.getWorkingIter())
				&& (simToRun.getCurrIter() <= simToRun.getMaxIter())) {

			Long noOfAgents = simToRun.getNoOfAgents();

			for (int agentId = 1; agentId <= noOfAgents; agentId++) {
				if (mode.equalsIgnoreCase("pop")) {
					Double noOfND = ((1 - Math.random())*(avgN * noOfAgents))
									/(avgN * noOfAgents);
					Integer noOfN = noOfND.intValue();
					
					
					
				} else if (mode.equalsIgnoreCase("team")) {

				}
			}

			simToRun = simDAO.getSimulationByUserAndSimId(userId, simId);
		}

	}

}
