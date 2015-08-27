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
@Table(name="observed_emotions")
public class ObservedEmotions implements Serializable{
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	Long id;
	
	@Column(name="iteration_no")
	Long iterNo;
	
	
	@Column(name="agent_id")
	Long agentId;
	
	
	@Column(name="target_event")
	Long targetEvent;
	
	
	@Column(name="target_neighbour")
	Long targetNeighbour;
	
	
	@Column(name="target_object")
	Long targetObj;
	
	
	@Column(name="emotion")
	String emotion;
	
	@Column(name="emotion_potential")
	Double emoPot;
	
	@Column(name="emotion_intensity")
	Double emoIntensity;
	
	
	@Column(name="user_id")
	String userId;
	
	
	@Column(name="simulation_id")
	Long simId;

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

	public Long getTargetEvent() {
		return targetEvent;
	}

	public void setTargetEvent(Long targetEvent) {
		this.targetEvent = targetEvent;
	}

	public Long getTargetNeighbour() {
		return targetNeighbour;
	}

	public void setTargetNeighbour(Long targetNeighbour) {
		this.targetNeighbour = targetNeighbour;
	}

	public Long getTargetObj() {
		return targetObj;
	}

	public void setTargetObj(Long targetObj) {
		this.targetObj = targetObj;
	}

	public String getEmotion() {
		return emotion;
	}

	public void setEmotion(String emotion) {
		this.emotion = emotion;
	}

	public Double getEmoPot() {
		return emoPot;
	}

	public void setEmoPot(Double emoPot) {
		this.emoPot = emoPot;
	}

	public Double getEmoIntensity() {
		return emoIntensity;
	}

	public void setEmoIntensity(Double emoIntensity) {
		this.emoIntensity = emoIntensity;
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
