package com.services.psychological.core;

import java.util.Random;

/*
 * *** Parameter of Integer type is mandatory that too only one 
 * *** The methods should have a return value
 * *** All method names should be unique
 * *** If you want to use the method, be sure to update the model_resources table

*/

public class CommonModels {
	private Random random = new Random();
	
	public int getRandomIntegerBetweenOneAndTen(Integer currIter)
	{
		System.out.println("Returning int from CommonModels");
		return (random.nextInt(10) + 1);
	}
	
	public double getValueNormalDistribution(Integer currIter) {
		System.out.println("Returning double (normal distribution) from CommonModels");
	    return random.nextGaussian();
	}

}
