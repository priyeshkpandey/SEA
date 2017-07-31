package com.services.models;

import java.util.Random;

public class AgentNetworkModels {

	private Random random = new Random();
	
	public Double getSignedValueNormalDistribution(Long currIter) {
		//System.out.println("Returning double (normal distribution) from AgentNetworkModels");
	    return Math.abs(random.nextDouble())*Math.pow(-1, random.nextInt(2) + 1);
	}
	
	public Long getZeroOrOne(Long currIter) {
		//System.out.println("Returning zero or one from AgentNetworkModels");
		return Long.valueOf((random.nextInt(2)));
	}
}
