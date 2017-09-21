package com.services.entities.game;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

@SuppressWarnings("serial")
@Entity
@Table(name="game_qa_state")
@Proxy(lazy=false)
public class GameQaState implements Serializable {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	Long id;
	
	@Column(name="simulation_id")
	Long simId;
	
	@Column(name="player_id")
	String playerId;
	
	@Column(name="qa_id")
	Long qaId;
	
	@Column(name="selected_answer")
	String selectedAnswer;
	
	@Column(name="is_answer_correct")
	Long isAnswerCorrect;
	
	@Column(name="correct_answer")
	String correctAnswer;
	
	@Column(name="score_for_answer")
	Long scoreForAnswer;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSimId() {
		return simId;
	}

	public void setSimId(Long simId) {
		this.simId = simId;
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public Long getQaId() {
		return qaId;
	}

	public void setQaId(Long qaId) {
		this.qaId = qaId;
	}

	public String getSelectedAnswer() {
		return selectedAnswer;
	}

	public void setSelectedAnswer(String selectedAnswer) {
		this.selectedAnswer = selectedAnswer;
	}

	public Long getIsAnswerCorrect() {
		return isAnswerCorrect;
	}

	public void setIsAnswerCorrect(Long isAnswerCorrect) {
		this.isAnswerCorrect = isAnswerCorrect;
	}

	public String getCorrectAnswer() {
		return correctAnswer;
	}

	public void setCorrectAnswer(String correctAnswer) {
		this.correctAnswer = correctAnswer;
	}

	public Long getScoreForAnswer() {
		return scoreForAnswer;
	}

	public void setScoreForAnswer(Long scoreForAnswer) {
		this.scoreForAnswer = scoreForAnswer;
	}
	
}
