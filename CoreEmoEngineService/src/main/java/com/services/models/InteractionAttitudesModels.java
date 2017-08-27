package com.services.models;

import java.util.Random;

public class InteractionAttitudesModels {

	private Random random = new Random();
	
	public String getDoubleValueAsString(Long currIter) {
		return Double.valueOf(random.nextDouble()).toString();
	}
	
	public String getLongValueAsString(Long currIter) {
	    return Long.valueOf(random.nextInt(10)).toString();	
	}
	
	public Long getZeroOrOne(Long currIter) {
		return Long.valueOf(random.nextInt(2)); 
	}
	
}
