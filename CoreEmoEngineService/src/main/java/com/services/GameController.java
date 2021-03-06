package com.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.services.entities.game.GamePlayerRequest;
import com.services.entities.game.QuestionToPost;
import com.services.entities.game.StatementToPost;
import com.services.game.core.GameEngine;

@RestController
@RequestMapping("/game")
public class GameController {
	
	@Autowired
	GameEngine gameEngine;
	
	@RequestMapping(value = "/generate", method = RequestMethod.POST)
	public void generateGameForPlayer(@RequestBody GamePlayerRequest gamePlayerRequest) {
		gameEngine.generateGameForPlayer(gamePlayerRequest.getSimId(), gamePlayerRequest.getUserId(), gamePlayerRequest.getPlayerId());
	}
	
	@RequestMapping(value = "/generate/questions", method = RequestMethod.POST)
	public void generateQuestionsForGame(@RequestBody GamePlayerRequest gamePlayerRequest) {
		gameEngine.generateQuestionsForGame(gamePlayerRequest.getSimId(), gamePlayerRequest.getUserId());
	}
	
	@RequestMapping(value = "/post/statement", method = RequestMethod.POST)
	public @ResponseBody StatementToPost postStatementForPlayer(@RequestBody GamePlayerRequest gamePlayerRequest) {
		return gameEngine.postStatementForPlayer(gamePlayerRequest.getPlayerId(), gamePlayerRequest.getSimId(), gamePlayerRequest.getIterNo());
	}
	
	@RequestMapping(value = "/post/question", method = RequestMethod.POST)
	public @ResponseBody QuestionToPost postQuestionForPlayer(@RequestBody GamePlayerRequest gamePlayerRequest) throws Exception {
		return gameEngine.postQuestionForPlayer(gamePlayerRequest.getPlayerId(), gamePlayerRequest.getSimId());
	}
	
	@RequestMapping(value = "/answer", method = RequestMethod.POST)
	public @ResponseBody Boolean processSelectedAnswer(@RequestBody QuestionToPost questionWithAnswer) {
		return gameEngine.processSelectedAnswer(questionWithAnswer.getQaStateId(), questionWithAnswer.getSelectedAnswer());
	}
	
	@RequestMapping(value = "/score", method = RequestMethod.GET)
	public @ResponseBody Long getScoreForThePlayerByGame(@RequestParam(value = "playerId") String playerId, @RequestParam(value = "simId") Long simId) {
		return gameEngine.getScoreForThePlayerByGame(playerId, simId);
	}
	
	@RequestMapping(value = "/history/statements", method = RequestMethod.GET)
	public @ResponseBody List<StatementToPost> getStatementsHistoryForPlayerByGame(@RequestParam(value = "playerId") String playerId, @RequestParam(value = "simId") Long simId) {
		return gameEngine.getStatementsHistoryForPlayerByGame(playerId, simId);
	}
	
	@RequestMapping(value = "/history/questions", method = RequestMethod.GET)
	public @ResponseBody List<QuestionToPost> getQuestionsHistoryForPlayerByGame(@RequestParam(value = "playerId") String playerId, @RequestParam(value = "simId") Long simId) {
		return gameEngine.getQuestionsHistoryForPlayerByGame(playerId, simId);
	}

}
