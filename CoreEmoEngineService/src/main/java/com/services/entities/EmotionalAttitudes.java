package com.services.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@SuppressWarnings("serial")
@Entity
@Table(name="emo_attitudes")
public class EmotionalAttitudes implements Serializable {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	Long id;
	
	@Column(name="iteration_no")
	Long iterNo;
	
	@Column(name="agent_id")
	Long agentId;
	
	@Column(name="emotions")
	String emotions;
	
	@Column(name="probability")
	Double probability;
	
	@Column(name="precedence")
	Long precedence;
	
	@Column(name="threshold")
	Double threshold;
	
	@Column(name="weight")
	Double weight;
	
	@Column(name="priority")
	Long priority;
	
	@Column(name="emotion_factor")
	Double emoFactor;
	
	@Column(name="occurrence_count")
	Long occurrenceCnt;
	
	@Column(name="occurred_count")
	Long occurredCnt;
	
	@Column(name="user_id")
	String userId;
	
	@Column(name="simulation_id")
	Long simId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIterNo() {
		return iterNo;
	}

	public void setIterNo(Long iterNo) {
		this.iterNo = iterNo;
	}

	public Long getAgentId() {
		return agentId;
	}

	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}

	public String getEmotions() {
		return emotions;
	}

	public void setEmotions(String emotions) {
		this.emotions = emotions;
	}

	public Double getProbability() {
		return probability;
	}

	public void setProbability(Double probability) {
		this.probability = probability;
	}

	public Long getPrecedence() {
		return precedence;
	}

	public void setPrecedence(Long precedence) {
		this.precedence = precedence;
	}

	public Double getThreshold() {
		return threshold;
	}

	public void setThreshold(Double threshold) {
		this.threshold = threshold;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Long getPriority() {
		return priority;
	}

	public void setPriority(Long priority) {
		this.priority = priority;
	}

	public Double getEmoFactor() {
		return emoFactor;
	}

	public void setEmoFactor(Double emoFactor) {
		this.emoFactor = emoFactor;
	}

	public Long getOccurrenceCnt() {
		return occurrenceCnt;
	}

	public void setOccurrenceCnt(Long occurrenceCnt) {
		this.occurrenceCnt = occurrenceCnt;
	}

	public Long getOccurredCnt() {
		return occurredCnt;
	}

	public void setOccurredCnt(Long occurredCnt) {
		this.occurredCnt = occurredCnt;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Long getSimId() {
		return simId;
	}

	public void setSimId(Long simId) {
		this.simId = simId;
	}
	
	

}
