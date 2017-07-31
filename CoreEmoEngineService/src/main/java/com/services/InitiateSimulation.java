package com.services;

import java.util.List;

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
import com.services.psychological.core.SingleStepComponent;

@RestController
public class InitiateSimulation extends BaseController {

	@Autowired
	ApplicationContext context;
	
	@Autowired
	ModelVariables constVars;
	@Autowired
	SingleStepComponent singleStep;

	@RequestMapping(value = "/initiate/simultion", method = RequestMethod.POST)
	public HttpStatus initiateSimulation(
			@RequestBody InitiateSimulationReqBody initSimReqBody) {
		
		String userId = initSimReqBody.getUserId();
		Long simId = initSimReqBody.getSimId();
		Long personOfInterest = initSimReqBody.getPersonOfInterest();
		List<Long> agents = initSimReqBody.getAgents();
		
		initSimulation(userId, simId, personOfInterest, agents);
		
		return HttpStatus.ACCEPTED;

	}

	@Async
	public void initSimulation(String userId, Long simId,
			Long personOfInterest, List<Long> agents) {
		SimulationDAO simDAO = context.getBean(SimulationDAO.class);
		Simulation simToRun = simDAO.getSimulationByUserAndSimId(userId, simId);

		constVars.setUserID(userId);
		constVars.setSimulationId(simId);

		Long currIter = 0l;
		while ((simToRun.getCurrIter() <= simToRun.getWorkingIter())
				&& (simToRun.getCurrIter() <= simToRun.getMaxIter())) {

			for (Long agentId : agents) {
				singleStep.setAgentId(agentId);
				singleStep.setConstVars(constVars);
				singleStep.setCurrIter(simToRun.getCurrIter());
				singleStep.setSimulationId(simId);
				singleStep.setThirdPerson(personOfInterest);
				singleStep.setUserID(userId);
				
				singleStep.initModelsAndExecuteSingleStep();
			}

			simToRun = simDAO.getSimulationByUserAndSimId(userId, simId);
			simToRun.setCurrIter(++currIter);
			simDAO.saveAndFlush(simToRun);
			
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
		
		Boolean isSimulationFinished = sim.getIsDone();
		
		if(isSimulationFinished) {
			sim.setIsDone(false);
			simDAO.saveAndFlush(sim);
		}
		
		return isSimulationFinished;
		
	}
	

}
