package com.services.entities;

public class InitiateSimulationReqBody {
	
	
	String userId;
	Long simId;
	Integer personOfInterest;
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
	public Integer getPersonOfInterest() {
		return personOfInterest;
	}
	public void setPersonOfInterest(Integer personOfInterest) {
		this.personOfInterest = personOfInterest;
	}
	

}
