package com.services;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.services.dao.SimulationDAO;
import com.services.dao.game.GamePlayerDAO;
import com.services.entities.Simulation;
import com.services.entities.game.GamePlayer;
import com.services.entities.game.QuestionToPost;
import com.services.entities.game.StatementToPost;
import com.services.game.core.GameEngine;

@Controller
@RequestMapping("/game/view")
public class GameViewController {
	
	private static final String LANDING_PAGE = "LandingPage";
	private static final String HOME_PAGE = "EqGameMain";
	private static final String QUESTION_PAGE = "QuestionPage";
	private static final String GENERATE_QUESTIONS = "GenerateQuestions";
	private static final String PLAY_GAME = "PlayGame";
	private static final String EXISTING_PLAYER = "ExistingPlayer";
	private static final String GENERATOR_USER_ID = "pr0001";
	
	@Autowired
	ApplicationContext context;
	
	@Autowired
	GameEngine gameEngine;
	
	@RequestMapping(value = "/landing", method = RequestMethod.GET)
	public String openLandingPage(ModelMap model) {
		String gameDescription = "You can improve your EQ (Emotional Quotient) by this game. You will see a scenario. You will not see complete scenario immediately."
				+ " You can see the scenario gradually. Many statements were made by each and every person. These statements were told during different time periods."
				+ " These statements reveal information about their emotional state. You can see whatever number of statements."
				+ " At any time you can attempt a question. The question would be about emotional state of the persons. The score for a correct answer will reduce by seeing more statements."
				+ " Welcome to the rigorous excercise of the emotions!";
		model.addAttribute("description", gameDescription);
		return LANDING_PAGE;
	}
	
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
		GamePlayerDAO gamePlayerDAO = context.getBean(GamePlayerDAO.class);
		GamePlayer gamePlayer = new GamePlayer();
		gamePlayer.setPlayerId(playerId); 
		gamePlayerDAO.save(gamePlayer);
		
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
	
	@RequestMapping(value = "/existing", method = RequestMethod.GET)
	public String existingPlayerPage(ModelMap model) {
		SimulationDAO simDAO = context.getBean(SimulationDAO.class);
		List<Simulation> response = simDAO.getSimulationsByUser(GENERATOR_USER_ID);
		List<String> games = new ArrayList<String>();
		for (Simulation sim : response) {
			games.add(sim.getSimName());
		}
		GamePlayerDAO gamePlayerDAO = context.getBean(GamePlayerDAO.class);
		List<GamePlayer> playersResponse = gamePlayerDAO.findAll();
		List<String> players = new ArrayList<String>();
		for (GamePlayer player : playersResponse) {
			players.add(player.getPlayerId());
		}
		model.addAttribute("players", players);
		model.addAttribute("games", games);
		return EXISTING_PLAYER;
	}
	
	@RequestMapping(value = "/play", method = RequestMethod.GET)
	public String playGame(@RequestParam("playerId") String playerId, @RequestParam("simName") String simName, ModelMap model) throws UnsupportedEncodingException {
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
	public String openQuestionPage(@RequestParam("playerId") String playerId, @RequestParam("simName") String simName, ModelMap model) throws Exception {
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
