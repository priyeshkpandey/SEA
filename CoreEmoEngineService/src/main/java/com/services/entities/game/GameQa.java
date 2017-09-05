package com.services.entities.game;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name="game_qa")
public class GameQa implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	Long id;
	
	@Column(name="simulation_id")
	Long simId;
	
	@Column(name="user_id")
	String userId;
	
	@Column(name="question")
	String question;
	
	@Column(name="option_one")
	String optionOne;
	
	@Column(name="option_two")
	String optionTwo;
	
	@Column(name="option_three")
	String optionThree;
	
	@Column(name="option_four")
	String optionFour;
	
	@Column(name="option_none")
	String optionNone;
	
	@Column(name="answer")
	String answer;

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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getOptionOne() {
		return optionOne;
	}

	public void setOptionOne(String optionOne) {
		this.optionOne = optionOne;
	}

	public String getOptionTwo() {
		return optionTwo;
	}

	public void setOptionTwo(String optionTwo) {
		this.optionTwo = optionTwo;
	}

	public String getOptionThree() {
		return optionThree;
	}

	public void setOptionThree(String optionThree) {
		this.optionThree = optionThree;
	}

	public String getOptionFour() {
		return optionFour;
	}

	public void setOptionFour(String optionFour) {
		this.optionFour = optionFour;
	}

	public String getOptionNone() {
		return optionNone;
	}

	public void setOptionNone(String optionNone) {
		this.optionNone = optionNone;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}
	
	
	
}
