package com.services.entities;

public class InitiateSimulationReqBody {
	
	
	String userId;
	Long simId;
	Long personOfInterest;
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
	public Long getPersonOfInterest() {
		return personOfInterest;
	}
	public void setPersonOfInterest(Long personOfInterest) {
		this.personOfInterest = personOfInterest;
	}
	

}
