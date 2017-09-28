package com.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.services.dao.SimulationDAO;
import com.services.entities.Simulation;
import com.services.entities.game.QuestionToPost;
import com.services.entities.game.StatementToPost;
import com.services.game.core.GameEngine;

@Controller
@RequestMapping("/game/view")
public class GameViewController {
	
	private static final String HOME_PAGE = "EqGameMain";
	private static final String QUESTION_PAGE = "QuestionPage";
	private static final String GENERATE_QUESTIONS = "GenerateQuestions";
	private static final String PLAY_GAME = "PlayGame";
	private static final String GENERATOR_USER_ID = "pr0001";
	
	@Autowired
	ApplicationContext context;
	
	@Autowired
	GameEngine gameEngine;
	
	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String openHomePage(ModelMap model) {
        SimulationDAO simDAO = context.getBean(SimulationDAO.class);
		List<Simulation> response = simDAO.getSimulationsByUser(GENERATOR_USER_ID);
		List<String> games = new ArrayList<String>();
		for (Simulation sim : response) {
			games.add(sim.getSimName());
		}
		model.addAttribute("games", games);
		return HOME_PAGE;
	}
	
	@RequestMapping(value = "/open", method = RequestMethod.POST)
	public String generateGameAndOpen(@RequestParam("playerId") String playerId, @RequestParam("simName") String simName, ModelMap model) {
		SimulationDAO simDAO = context.getBean(SimulationDAO.class);
		Simulation sim = simDAO.getSimulationByName(simName);
		gameEngine.generateGameForPlayer(sim.getSimId(), GENERATOR_USER_ID, playerId); 
		model.addAttribute("simName", simName);
		model.addAttribute("playerId", playerId);
		return GENERATE_QUESTIONS;
	}
	
	@RequestMapping(value = "/init", method = RequestMethod.POST)
	public String generateQuestions(@RequestParam("playerId") String playerId, @RequestParam("simName") String simName, ModelMap model) {
		SimulationDAO simDAO = context.getBean(SimulationDAO.class);
		Simulation sim = simDAO.getSimulationByName(simName);
		gameEngine.generateQuestionsForGame(sim.getSimId(), GENERATOR_USER_ID);
		Long simId = sim.getSimId();
		model.addAttribute("simName", simName);
		model.addAttribute("playerId", playerId);
		model.addAttribute("statements", getHistoryOfStatements(playerId, simId));
		model.addAttribute("simId", simId);
		model.addAttribute("maxIter", sim.getMaxIter());
		return PLAY_GAME;
	}
	
	
	@RequestMapping(value = "/play/{simName}/{playerId}", method = RequestMethod.GET)
	public String playGame(@PathVariable("playerId") String playerId, @PathVariable("simName") String simName, ModelMap model) {
		SimulationDAO simDAO = context.getBean(SimulationDAO.class);
		Simulation sim = simDAO.getSimulationByName(simName);
		Long simId = sim.getSimId();
		model.addAttribute("simName", simName);
		model.addAttribute("playerId", playerId);
		model.addAttribute("statements", getHistoryOfStatements(playerId, simId));
		model.addAttribute("simId", simId);
		model.addAttribute("maxIter", sim.getMaxIter());
		return PLAY_GAME;
	}
	
	private String getHistoryOfStatements(String playerId, Long simId) {
		StringBuilder statements = new StringBuilder();
		List<StatementToPost> postedStatements = gameEngine.getStatementsHistoryForPlayerByGame(playerId, simId);
		for (StatementToPost postedStatement : postedStatements) {
			String line = "Duration " + postedStatement.getIterNo() + ": " + postedStatement.getAgentId() + " said \"" + postedStatement.getStatement() + "\"\n"; 
			statements.append(line);
		}
		return statements.toString();
	}
	
	@RequestMapping(value = "/question", method = RequestMethod.POST)
	public String openQuestionPage(@RequestParam("playerId") String playerId, @RequestParam("simName") String simName, ModelMap model) {
		SimulationDAO simDAO = context.getBean(SimulationDAO.class);
		Simulation sim = simDAO.getSimulationByName(simName);
		QuestionToPost question = gameEngine.postQuestionForPlayer(playerId, sim.getSimId());
		String options = question.getOptions();
		model.addAttribute("simName", simName);
		model.addAttribute("playerId", playerId);
		model.addAttribute("question", question.getQuestion());
		model.addAttribute("qaStateId", question.getQaStateId());
		String optionOne = options.split(",")[0];
		String optionTwo = options.split(",")[1];
		String optionThree = options.split(",")[2];
		String optionFour = options.split(",")[3];
		model.addAttribute("optionOne", optionOne);
		model.addAttribute("optionTwo", optionTwo);
		model.addAttribute("optionThree", optionThree);
		model.addAttribute("optionFour", optionFour);
		return QUESTION_PAGE;
	}

}
