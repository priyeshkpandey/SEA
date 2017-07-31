package com.services.models;

import java.util.Random;

public class AgentModels {
	private Random random = new Random();
	
	public Double getSignedValueNormalDistribution(Long currIter) {
		//System.out.println("Returning double (normal distribution) from AgentModels");
	    return Math.abs(random.nextDouble())*Math.pow(-1, random.nextInt(2) + 1);
	}

}
