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
@Table(name="previous_state")
public class PreviousState implements Serializable{
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	Long id;
	
	@Column(name="iteration_no")
	Long iterNo;
	
	@Column(name="agent_id")
	Long agentId;
	
	@Column(name="event_id")
	Long eventId;
	
	@Column(name="emotion")
	String emotion;
	
	@Column(name="hope_potential")
	Double hopePotential;
	
	@Column(name="fear_potential")
	Double fearPotential;
	
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

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public String getEmotion() {
		return emotion;
	}

	public void setEmotion(String emotion) {
		this.emotion = emotion;
	}

	public Double getHopePotential() {
		return hopePotential;
	}

	public void setHopePotential(Double hopePotential) {
		this.hopePotential = hopePotential;
	}

	public Double getFearPotential() {
		return fearPotential;
	}

	public void setFearPotential(Double fearPotential) {
		this.fearPotential = fearPotential;
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
