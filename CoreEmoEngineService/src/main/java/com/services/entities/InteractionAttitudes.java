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
@Table(name="interaction_attitudes")
public class InteractionAttitudes implements Serializable{
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	Long id;
	
	@Column(name="iteration_no")
	Long iterNo;
	
	@Column(name="agent_id1")
	Long agentId1;
	
	@Column(name="agent_id2")
	Long agentId2;
	
	@Column(name="variable")
	String variable;
	
	@Column(name="third_person")
	Long thirdPerson;
	
	@Column(name="target_event")
	Long targetEvent;
	
	@Column(name="target_object")
	Long targetAgent;
	
	@Column(name="target_emotion")
	String targetEmotion;
	
	@Column(name="interaction_probability")
	Double interactProb;
	
	@Column(name="influence")
	Double influence;
	
	@Column(name="influence_filter")
	Double influenceFilter;
	
	@Column(name="influence_likelihood")
	Double influenceLikelihood;
	
	@Column(name="precedence")
	Long precedence;
	
	@Column(name="is_influence_persists")
	Boolean isInfluencePersists;
	
	@Column(name="influence_threshold")
	Double influenceThreshold;
	
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

	public Long getAgentId1() {
		return agentId1;
	}

	public void setAgentId1(Long agentId1) {
		this.agentId1 = agentId1;
	}

	public Long getAgentId2() {
		return agentId2;
	}

	public void setAgentId2(Long agentId2) {
		this.agentId2 = agentId2;
	}

	public String getVariable() {
		return variable;
	}

	public void setVariable(String variable) {
		this.variable = variable;
	}

	public Long getThirdPerson() {
		return thirdPerson;
	}

	public void setThirdPerson(Long thirdPerson) {
		this.thirdPerson = thirdPerson;
	}

	public Long getTargetEvent() {
		return targetEvent;
	}

	public void setTargetEvent(Long targetEvent) {
		this.targetEvent = targetEvent;
	}

	public Long getTargetAgent() {
		return targetAgent;
	}

	public void setTargetAgent(Long targetAgent) {
		this.targetAgent = targetAgent;
	}

	public String getTargetEmotion() {
		return targetEmotion;
	}

	public void setTargetEmotion(String targetEmotion) {
		this.targetEmotion = targetEmotion;
	}

	public Double getInteractProb() {
		return interactProb;
	}

	public void setInteractProb(Double interactProb) {
		this.interactProb = interactProb;
	}

	public Double getInfluence() {
		return influence;
	}

	public void setInfluence(Double influence) {
		this.influence = influence;
	}

	public Double getInfluenceFilter() {
		return influenceFilter;
	}

	public void setInfluenceFilter(Double influenceFilter) {
		this.influenceFilter = influenceFilter;
	}

	public Double getInfluenceLikelihood() {
		return influenceLikelihood;
	}

	public void setInfluenceLikelihood(Double influenceLikelihood) {
		this.influenceLikelihood = influenceLikelihood;
	}

	public Long getPrecedence() {
		return precedence;
	}

	public void setPrecedence(Long precedence) {
		this.precedence = precedence;
	}

	public Boolean getIsInfluencePersists() {
		return isInfluencePersists;
	}

	public void setIsInfluencePersists(Boolean isInfluencePersists) {
		this.isInfluencePersists = isInfluencePersists;
	}

	public Double getInfluenceThreshold() {
		return influenceThreshold;
	}

	public void setInfluenceThreshold(Double influenceThreshold) {
		this.influenceThreshold = influenceThreshold;
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
