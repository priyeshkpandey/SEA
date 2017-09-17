package com.services.psychological.core;

import org.springframework.stereotype.Component;

@Component
public class ModelVariables {

	private String userID;
	private Long simulationId;
	
	public ModelVariables()
	{

	}

	public void setUserId(String userID) {
		this.userID = userID;
	}

	public void setSimId(Long simulationId) {
		this.simulationId = simulationId;
	}

	
	public String getUserId(){return userID;}
	public Long getSimId(){return simulationId;}

}
