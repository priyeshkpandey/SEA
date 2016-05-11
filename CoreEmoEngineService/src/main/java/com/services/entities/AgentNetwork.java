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
@Table(name="agent_network")
public class AgentNetwork implements Serializable{
	
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
	
	@Column(name="event_involved")
	Long eventInvolved;
	
	@Column(name="other_desirability")
	Double otherDesirability;
	
	@Column(name="deservingness")
	Double deservingness;
	
	@Column(name="cognitive_unit_strength")
	Double cogUnitStrength;
	
	@Column(name="expectation_deviation")
	Double expDiff;
	
	@Column(name="other_liking")
	Double otherLiking;
	
	@Column(name="praiseworthiness")
	Double praiseworthiness;
	
	@Column(name="agent_sense_of_reality")
	Double agentSenseOfReality;
	
	@Column(name="agent_psycho_proximity")
	Double agentPsychoProximity;
	
	@Column(name="agent_unexpectedness")
	Double agentUnexpect;
	
	@Column(name="agent_arousal")
	Double agentArousal;
	
	@Column(name="emotion_target")
	Boolean emoTarget;
	
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

	public Long getEventInvolved() {
		return eventInvolved;
	}

	public void setEventInvolved(Long eventInvolved) {
		this.eventInvolved = eventInvolved;
	}

	public Double getOtherDesirability() {
		return otherDesirability;
	}

	public void setOtherDesirability(Double otherDesirability) {
		this.otherDesirability = otherDesirability;
	}

	public Double getDeservingness() {
		return deservingness;
	}

	public void setDeservingness(Double deservingness) {
		this.deservingness = deservingness;
	}

	public Double getCogUnitStrength() {
		return cogUnitStrength;
	}

	public void setCogUnitStrength(Double cogUnitStrength) {
		this.cogUnitStrength = cogUnitStrength;
	}

	public Double getExpDiff() {
		return expDiff;
	}

	public void setExpDiff(Double expDiff) {
		this.expDiff = expDiff;
	}

	public Double getOtherLiking() {
		return otherLiking;
	}

	public void setOtherLiking(Double otherLiking) {
		this.otherLiking = otherLiking;
	}

	public Double getPraiseworthiness() {
		return praiseworthiness;
	}

	public void setPraiseworthiness(Double praiseworthiness) {
		this.praiseworthiness = praiseworthiness;
	}

	public Double getAgentSenseOfReality() {
		return agentSenseOfReality;
	}

	public void setAgentSenseOfReality(Double agentSenseOfReality) {
		this.agentSenseOfReality = agentSenseOfReality;
	}

	public Double getAgentPsychoProximity() {
		return agentPsychoProximity;
	}

	public void setAgentPsychoProximity(Double agentPsychoProximity) {
		this.agentPsychoProximity = agentPsychoProximity;
	}

	public Double getAgentUnexpect() {
		return agentUnexpect;
	}

	public void setAgentUnexpect(Double agentUnexpect) {
		this.agentUnexpect = agentUnexpect;
	}

	public Double getAgentArousal() {
		return agentArousal;
	}

	public void setAgentArousal(Double agentArousal) {
		this.agentArousal = agentArousal;
	}

	public Boolean getEmoTarget() {
		return emoTarget;
	}

	public void setEmoTarget(Boolean emoTarget) {
		this.emoTarget = emoTarget;
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
