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
@Table(name="simulation_data_tab")
public class Simulation implements Serializable{
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	Long id;
	
	@Column(name="simulation_id")
	Long simId;
	
	@Column(name="simulation_name")
	String simName;
	
	@Column(name="max_iteration")
	Long maxIter;
	
	@Column(name="current_iteration")
	Long currIter;
	
	@Column(name="user_id")
	String userId;
	
	@Column(name="proposed_iteration")
	Long proposedIter;
	
	@Column(name="working_iteration")
	Long workingIter;
	
	@Column(name="no_of_agents")
	Long noOfAgents;
	
	@Column(name="is_done")
	Boolean isDone;
	
	
	public Boolean getIsDone() {
		return isDone;
	}
	public void setIsDone(Boolean isDone) {
		this.isDone = isDone;
	}
	public Long getNoOfAgents() {
		return noOfAgents;
	}
	public void setNoOfAgents(Long noOfAgents) {
		this.noOfAgents = noOfAgents;
	}
	public Long getSimId() {
		return simId;
	}
	public void setSimId(Long simId) {
		this.simId = simId;
	}
	public String getSimName() {
		return simName;
	}
	public void setSimName(String simName) {
		this.simName = simName;
	}
	public Long getMaxIter() {
		return maxIter;
	}
	public void setMaxIter(Long maxIter) {
		this.maxIter = maxIter;
	}
	public Long getCurrIter() {
		return currIter;
	}
	public void setCurrIter(Long currIter) {
		this.currIter = currIter;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Long getProposedIter() {
		return proposedIter;
	}
	public void setProposedIter(Long proposedIter) {
		this.proposedIter = proposedIter;
	}
	public Long getWorkingIter() {
		return workingIter;
	}
	public void setWorkingIter(Long workingIter) {
		this.workingIter = workingIter;
	}
	
}
