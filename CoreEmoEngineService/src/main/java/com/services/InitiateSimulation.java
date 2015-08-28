package com.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import psychologicalCore.ConstantVariables;
import psychologicalCore.ExecuteSingleSimulationStep;

import com.services.dao.SimulationDAO;
import com.services.entities.InitiateSimulationReqBody;
import com.services.entities.Simulation;

@RestController
public class InitiateSimulation extends BaseController {

	@Autowired
	ApplicationContext context;

	@RequestMapping(value = "/initiate/simultion", method = RequestMethod.POST)
	public HttpStatus initiateSimulation(
			@RequestBody InitiateSimulationReqBody initSimReqBody) {
		
		String userId = initSimReqBody.getUserId();
		Long simId = initSimReqBody.getSimId();
		Integer personOfInterest = initSimReqBody.getPersonOfInterest();
		
		initSimulation(userId, simId, personOfInterest);
		
		return HttpStatus.ACCEPTED;

	}

	@Async
	public void initSimulation(String userId, Long simId,
			Integer personOfInterest) {
		SimulationDAO simDAO = context.getBean(SimulationDAO.class);
		Simulation simToRun = simDAO.getSimulationByUserAndSimId(userId, simId);

		ConstantVariables.simulationId = simToRun.getSimId();
		ConstantVariables.userID = simToRun.getUserId();

		while ((simToRun.getCurrIter() <= simToRun.getWorkingIter())
				&& (simToRun.getCurrIter() <= simToRun.getMaxIter())) {

			Long noOfAgents = simToRun.getNoOfAgents();

			for (int agentId = 1; agentId <= noOfAgents; agentId++) {

				new ExecuteSingleSimulationStep(simId, simToRun.getCurrIter(),
						agentId, personOfInterest).initModels();
			}

			
		}

	}
	
	
	

}
