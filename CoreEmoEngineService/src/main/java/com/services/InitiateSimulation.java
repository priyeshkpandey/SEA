package com.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.services.dao.SimulationDAO;
import com.services.entities.InitiateSimulationReqBody;
import com.services.entities.Simulation;
import com.services.psychological.core.ModelVariables;
import com.services.psychological.core.ExecuteSingleSimulationStep;
import com.services.psychological.core.SingleStepComponent;

@RestController
public class InitiateSimulation extends BaseController {

	@Autowired
	ApplicationContext context;
	
	ModelVariables constVars;
	SingleStepComponent singleStep;

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

		//ConstantVariables constVars = new ConstantVariables(simToRun.getUserId(),
			//	simToRun.getSimId());

		while ((simToRun.getCurrIter() <= simToRun.getWorkingIter())
				&& (simToRun.getCurrIter() <= simToRun.getMaxIter())) {

			Long noOfAgents = simToRun.getNoOfAgents();

			for (int agentId = 1; agentId <= noOfAgents; agentId++) {

				new ExecuteSingleSimulationStep(simToRun.getCurrIter(),
						agentId, personOfInterest, constVars).initModels();
			}

			simToRun = simDAO.getSimulationByUserAndSimId(userId, simId);
			
		}
		
		simToRun.setIsDone(true);
		simDAO.save(simToRun);

	}
	
	@RequestMapping(value = "/sim/status", method = RequestMethod.GET)
	public Boolean getSimStatus(@RequestParam(value = "userId") String userId,
			@RequestParam(value = "simId") Long simId)
	{
		SimulationDAO simDAO = context.getBean(SimulationDAO.class);
		Simulation sim = simDAO.getSimulationByUserAndSimId(userId, simId);
		
		return sim.getIsDone();
		
	}
	

}
