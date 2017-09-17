package com.services.game.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.services.dao.ObservedEmotionsDAO;
import com.services.dao.game.GamePlayerStatementsDAO;
import com.services.dao.game.GameQaDAO;
import com.services.dao.game.GameQaStateDAO;
import com.services.dao.game.GameStatementsDAO;
import com.services.entities.ObservedEmotions;
import com.services.entities.game.GamePlayerStatements;
import com.services.entities.game.GameQa;
import com.services.entities.game.GameQaState;
import com.services.entities.game.GameStatements;
import com.services.entities.game.QuestionToPost;
import com.services.entities.game.StatementToPost;
import com.services.game.enums.AgentNames;
import com.services.game.enums.Emotions;

@Component
public class GameEngine {
	
	@Autowired
	ApplicationContext context;
	
	private Random random = new Random();

	public void generateGameForPlayer(Long simId, String userId, String playerId) {
		GameStatementsDAO gameStatementDAO = (GameStatementsDAO)context.getBean(GameStatementsDAO.class);
		List<GameStatements> gameStatements = gameStatementDAO.getStatementForGameByUserId(simId, userId);
		List<GamePlayerStatements> gamePlayerStatements = new ArrayList<GamePlayerStatements>();
		for(GameStatements gameStatement : gameStatements) {
			GamePlayerStatements gamePlayerStatement = new GamePlayerStatements();
			gamePlayerStatement.setAgentId(gameStatement.getAgentId());
			gamePlayerStatement.setIsAlreadyPresented(0l);
			gamePlayerStatement.setIterNo(gameStatement.getIterNo());
			gamePlayerStatement.setPlayerId(playerId);
			gamePlayerStatement.setSimId(simId);
			gamePlayerStatement.setStatementId(gameStatement.getId());
			gamePlayerStatement.setUserId(userId);
			
			gamePlayerStatements.add(gamePlayerStatement);
		}
		
		GamePlayerStatementsDAO gamePlayerStatementsDAO = (GamePlayerStatementsDAO)context.getBean(GamePlayerStatementsDAO.class);
		gamePlayerStatementsDAO.save(gamePlayerStatements);
		
	}
	
	
	public void generateQuestionsForGame(Long simId, String userId) {
		ObservedEmotionsDAO observedEmotionsDAO = (ObservedEmotionsDAO)context.getBean(ObservedEmotionsDAO.class);
		List<ObservedEmotions> observedEmotions = observedEmotionsDAO.getObsEmosByUserSimId(userId, simId); 
		List<GameQa> gameQuestions = new ArrayList<GameQa>();
		
		for(ObservedEmotions observedEmotion : observedEmotions) {
			
			if (observedEmotion.getEmoIntensity() > 0) {
				
				GameQa gameQa = buildQuestion("The emotion felt by agent " + AgentNames.getAgentNameById(observedEmotion.getAgentId()) + " during the period " + observedEmotion.getIterNo() + "was : ",
						observedEmotion, simId, userId);
				gameQuestions.add(gameQa);
				
			} else if (observedEmotion.getEmoPot() > 0) {
				GameQa gameQa = buildQuestion("The potential to feel emotion by agent " + AgentNames.getAgentNameById(observedEmotion.getAgentId()) + " during the period " + observedEmotion.getIterNo() + "was : ",
						observedEmotion, simId, userId);
				gameQuestions.add(gameQa);
			}
			
		}
		
		GameQaDAO gameQaDAO = (GameQaDAO)context.getBean(GameQaDAO.class);
		gameQaDAO.save(gameQuestions);
		
	}
	
	private GameQa buildQuestion(String question, ObservedEmotions observedEmotion, Long simId, String userId) {
		GameQa gameQa = new GameQa();
		gameQa.setQuestion(question);
		String correctAnswer = observedEmotion.getEmotion();
		gameQa.setAnswer(correctAnswer);
		
		int correctOption = random.nextInt(4);
		
		List<String> options = new ArrayList<String>();
		options.add(correctAnswer);
		
		String currOption = correctAnswer;
		if (0 == correctOption) {
			gameQa.setOptionOne(correctAnswer);
			currOption = Emotions.getRandomEmotionExcept(options);
			gameQa.setOptionTwo(currOption);
			options.add(currOption);
			currOption = Emotions.getRandomEmotionExcept(options);
			gameQa.setOptionThree(currOption);
			options.add(currOption);
			currOption = Emotions.getRandomEmotionExcept(options);
			gameQa.setOptionFour(currOption);
			
		} else if (1 == correctOption) {
			gameQa.setOptionTwo(correctAnswer);
			currOption = Emotions.getRandomEmotionExcept(options);
			gameQa.setOptionOne(currOption);
			options.add(currOption);
			currOption = Emotions.getRandomEmotionExcept(options);
			gameQa.setOptionThree(currOption);
			options.add(currOption);
			currOption = Emotions.getRandomEmotionExcept(options);
			gameQa.setOptionFour(currOption);
			
		} else if (2 == correctOption) {
			gameQa.setOptionThree(correctAnswer);
			currOption = Emotions.getRandomEmotionExcept(options);
			gameQa.setOptionOne(currOption);
			options.add(currOption);
			currOption = Emotions.getRandomEmotionExcept(options);
			gameQa.setOptionTwo(currOption);
			options.add(currOption);
			currOption = Emotions.getRandomEmotionExcept(options);
			gameQa.setOptionFour(currOption);
			
		} else if (3 == correctOption) {
			gameQa.setOptionFour(correctAnswer);
			currOption = Emotions.getRandomEmotionExcept(options);
			gameQa.setOptionOne(currOption);
			options.add(currOption);
			currOption = Emotions.getRandomEmotionExcept(options);
			gameQa.setOptionTwo(currOption);
			options.add(currOption);
			currOption = Emotions.getRandomEmotionExcept(options);
			gameQa.setOptionThree(currOption);
			
		}
		gameQa.setSimId(simId);
		gameQa.setUserId(userId);
		
		return gameQa;
	}
	
	public StatementToPost postStatementForPlayer(String playerId, Long simId, Long iterNo) {
		GamePlayerStatementsDAO gamePlayerStatementsDAO = (GamePlayerStatementsDAO)context.getBean(GamePlayerStatementsDAO.class);
		List<GamePlayerStatements> gamePlayerStatements = gamePlayerStatementsDAO.getUnpresentedStatementsForPlayerByGameAndIter(playerId, simId, iterNo);
		int randomIndex = random.nextInt(gamePlayerStatements.size());
		GamePlayerStatements statementToPresent = gamePlayerStatements.get(randomIndex);
		
		GameStatementsDAO gameStatementDAO = (GameStatementsDAO)context.getBean(GameStatementsDAO.class);
		GameStatements gameStatement = gameStatementDAO.getOne(statementToPresent.getStatementId());
		
		StatementToPost statementToPost = new StatementToPost();
		statementToPost.setAgentId(statementToPresent.getAgentId());
		statementToPost.setIterNo(statementToPresent.getIterNo());
		statementToPost.setStatement(gameStatement.getStatement());
		statementToPost.setSimId(simId);
		statementToPost.setPlayerId(playerId); 
		statementToPost.setStatementId(statementToPresent.getStatementId());
		
		statementToPresent.setIsAlreadyPresented(1l); 
		gamePlayerStatementsDAO.saveAndFlush(statementToPresent);
		
		return statementToPost;
		
	}
	
	public QuestionToPost postQuestionForPlayer(String playerId, Long simId) {
		
		GameQaDAO gameQaDAO = (GameQaDAO)context.getBean(GameQaDAO.class);
		GameQaStateDAO gameStateDAO = (GameQaStateDAO)context.getBean(GameQaStateDAO.class);
		
		List<GameQa> gameQuestions = gameQaDAO.getGamesBySimId(simId);
		List<GameQaState> gameStateQuestions = gameStateDAO.getAskedQuestionsByPlayerAndSimId(playerId, simId);
		GameQa unaskedQuestion = getUnaskedQuestion(gameQuestions, gameStateQuestions);
		
		GamePlayerStatementsDAO gamePlayerStatementsDAO = (GamePlayerStatementsDAO)context.getBean(GamePlayerStatementsDAO.class);
		Long totalStatements = gamePlayerStatementsDAO.getCountOfAllStatementsForGameByPlayer(playerId, simId);
		Long totalPresentedStatements = gamePlayerStatementsDAO.getCountOfPresentedStatementsForGameByPlayer(playerId, simId);
		
	    long score = 6000 - (6000/totalStatements)*totalPresentedStatements;
		
		GameQaState questionState = new GameQaState();
		questionState.setCorrectAnswer(unaskedQuestion.getAnswer());
		questionState.setPlayerId(playerId);
		questionState.setQaId(unaskedQuestion.getId());
		questionState.setScoreForAnswer(score);
		questionState.setSimId(simId);
		questionState = gameStateDAO.saveAndFlush(questionState);
		String options = unaskedQuestion.getOptionOne() + "," + unaskedQuestion.getOptionTwo() + "," + unaskedQuestion.getOptionThree() + "," + unaskedQuestion.getOptionFour() + ","
				+ unaskedQuestion.getOptionNone();
		
		QuestionToPost questionToPost = new QuestionToPost();
		questionToPost.setQuestion(unaskedQuestion.getQuestion());
		questionToPost.setOptions(options);
		questionToPost.setQaStateId(questionState.getId());
		
		return questionToPost;
		
	}
	
	private GameQa getUnaskedQuestion(List<GameQa> gameQuestions, List<GameQaState> gameStateQuestions) {
		
		GameQa unaskedQuestion = null;
		do {
			unaskedQuestion = gameQuestions.get(random.nextInt(gameStateQuestions.size()));
		}while (isQuestionAlreadyAsked(unaskedQuestion, gameStateQuestions));
		
		return unaskedQuestion;
	}
	
	private Boolean isQuestionAlreadyAsked(GameQa gameQa, List<GameQaState> gameStateQuestions) {
		
		for(GameQaState askedQuestion : gameStateQuestions) {
			if (askedQuestion.getQaId() == gameQa.getId()) {
				return true;
			}
		}
		return false;
		
	}
	
	public Boolean processSelectedAnswer(Long qaStateId, String selectedAnswer) {
		GameQaStateDAO gameStateDAO = (GameQaStateDAO)context.getBean(GameQaStateDAO.class);
		GameQaState questionState = gameStateDAO.getOne(qaStateId);
		boolean result = questionState.getCorrectAnswer().equalsIgnoreCase(selectedAnswer);
		questionState.setSelectedAnswer(selectedAnswer);
		Long isAnswerCorrect = null;
		if(result) {
			isAnswerCorrect = 1l;
		} else {
			isAnswerCorrect = 0l;
		}
		questionState.setIsAnswerCorrect(isAnswerCorrect);
		gameStateDAO.saveAndFlush(questionState);
		return result;
	}
	
	public Long getScoreForThePlayerByGame(String playerId, Long simId) {
		GameQaStateDAO gameStateDAO = (GameQaStateDAO)context.getBean(GameQaStateDAO.class);
		return gameStateDAO.getScoreForThePlayerByGame(playerId, simId);
	}
	
}
