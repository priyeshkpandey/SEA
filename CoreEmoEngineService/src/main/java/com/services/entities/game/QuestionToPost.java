package com.services.entities.game;

public class QuestionToPost {
	
	Long qaStateId;
	String question;
	String options;
	String selectedAnswer;
	
	public Long getQaStateId() {
		return qaStateId;
	}
	public void setQaStateId(Long qaStateId) {
		this.qaStateId = qaStateId;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getOptions() {
		return options;
	}
	public void setOptions(String options) {
		this.options = options;
	}
	public String getSelectedAnswer() {
		return selectedAnswer;
	}
	public void setSelectedAnswer(String selectedAnswer) {
		this.selectedAnswer = selectedAnswer;
	}
	
}
