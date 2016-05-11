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
@Table(name="agent_tab")
public class AgentTab implements Serializable{
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	Long id;
	
	@Column(name="iteration_no")
	Long iterNo;
	
	@Column(name="agent_id")
	Long agentId;
	
	@Column(name="is_agoal")
	Boolean isAGoal;
	
	@Column(name="is_igoal")
	Boolean isIGoal;
	
	@Column(name="is_rgoal")
	Boolean isRGoal;
	
	@Column(name="is_event")
	Boolean isEvent;
	
	@Column(name="is_agent")
	Boolean isAgent;
	
	@Column(name="is_object")
	Boolean isObject;
	
	@Column(name="agoal_value")
	Double aGoalValue;
	
	@Column(name="igoal_value")
	Double iGoalValue;
	
	@Column(name="rgoal_value")
	Double rGoalValue;
	
	@Column(name="action")
	Long action;
	
	@Column(name="no_of_emos_to_invoke")
	Long numOfEmosToInvoke;
	
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

	public Boolean getIsAGoal() {
		return isAGoal;
	}

	public void setIsAGoal(Boolean isAGoal) {
		this.isAGoal = isAGoal;
	}

	public Boolean getIsIGoal() {
		return isIGoal;
	}

	public void setIsIGoal(Boolean isIGoal) {
		this.isIGoal = isIGoal;
	}

	public Boolean getIsRGoal() {
		return isRGoal;
	}

	public void setIsRGoal(Boolean isRGoal) {
		this.isRGoal = isRGoal;
	}

	public Boolean getIsEvent() {
		return isEvent;
	}

	public void setIsEvent(Boolean isEvent) {
		this.isEvent = isEvent;
	}

	public Boolean getIsAgent() {
		return isAgent;
	}

	public void setIsAgent(Boolean isAgent) {
		this.isAgent = isAgent;
	}

	public Boolean getIsObject() {
		return isObject;
	}

	public void setIsObject(Boolean isObject) {
		this.isObject = isObject;
	}

	public Double getaGoalValue() {
		return aGoalValue;
	}

	public void setaGoalValue(Double aGoalValue) {
		this.aGoalValue = aGoalValue;
	}

	public Double getiGoalValue() {
		return iGoalValue;
	}

	public void setiGoalValue(Double iGoalValue) {
		this.iGoalValue = iGoalValue;
	}

	public Double getrGoalValue() {
		return rGoalValue;
	}

	public void setrGoalValue(Double rGoalValue) {
		this.rGoalValue = rGoalValue;
	}

	public Long getAction() {
		return action;
	}

	public void setAction(Long action) {
		this.action = action;
	}

	public Long getNumOfEmosToInvoke() {
		return numOfEmosToInvoke;
	}

	public void setNumOfEmosToInvoke(Long numOfEmosToInvoke) {
		this.numOfEmosToInvoke = numOfEmosToInvoke;
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
