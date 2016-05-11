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
@Table(name="event_tab")
public class EventTab implements Serializable{
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	Long id;
	
	@Column(name="iteration_no")
	Long iterNo;
	
	@Column(name="event_id")
	Long eventId;
	
	@Column(name="agent_id")
	Long agentId;
	
	@Column(name="agent_desirability")
	Double agentDesirability;
	
	@Column(name="event_sense_of_reality")
	Double eventSenseOfReality;
	
	@Column(name="event_psycho_proximity")
	Double eventPsychoProximity;
	
	@Column(name="event_unexpectedness")
	Double eventUnexpectedness;
	
	@Column(name="event_arousal")
	Double eventArousal;
	
	@Column(name="event_likelihood")
	Double eventLikelihood;
	
	@Column(name="event_effort")
	Double eventEffort;
	
	@Column(name="event_realization")
	Double eventRealization;
	
	@Column(name="event_prospect")
	Double eventProspect;
	
	@Column(name="agent_belief")
	Double agentBelief;
	
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

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public Long getAgentId() {
		return agentId;
	}

	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}

	public Double getAgentDesirability() {
		return agentDesirability;
	}

	public void setAgentDesirability(Double agentDesirability) {
		this.agentDesirability = agentDesirability;
	}

	public Double getEventSenseOfReality() {
		return eventSenseOfReality;
	}

	public void setEventSenseOfReality(Double eventSenseOfReality) {
		this.eventSenseOfReality = eventSenseOfReality;
	}

	public Double getEventPsychoProximity() {
		return eventPsychoProximity;
	}

	public void setEventPsychoProximity(Double eventPsychoProximity) {
		this.eventPsychoProximity = eventPsychoProximity;
	}

	public Double getEventUnexpectedness() {
		return eventUnexpectedness;
	}

	public void setEventUnexpectedness(Double eventUnexpectedness) {
		this.eventUnexpectedness = eventUnexpectedness;
	}

	public Double getEventArousal() {
		return eventArousal;
	}

	public void setEventArousal(Double eventArousal) {
		this.eventArousal = eventArousal;
	}

	public Double getEventLikelihood() {
		return eventLikelihood;
	}

	public void setEventLikelihood(Double eventLikelihood) {
		this.eventLikelihood = eventLikelihood;
	}

	public Double getEventEffort() {
		return eventEffort;
	}

	public void setEventEffort(Double eventEffort) {
		this.eventEffort = eventEffort;
	}

	public Double getEventRealization() {
		return eventRealization;
	}

	public void setEventRealization(Double eventRealization) {
		this.eventRealization = eventRealization;
	}

	public Double getEventProspect() {
		return eventProspect;
	}

	public void setEventProspect(Double eventProspect) {
		this.eventProspect = eventProspect;
	}

	public Double getAgentBelief() {
		return agentBelief;
	}

	public void setAgentBelief(Double agentBelief) {
		this.agentBelief = agentBelief;
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
