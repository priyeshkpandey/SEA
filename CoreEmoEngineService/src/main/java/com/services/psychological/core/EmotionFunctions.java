package com.services.psychological.core;

public class EmotionFunctions {
	
	// Methods for Emotion Potential calculations
	
	// ***** Joy/Distress *****
	
	public static double calcJoyPotential(double joyFactor, double desirability, double globalVars)
	{
		double joyPotential = 0;
		joyPotential = joyFactor*(((desirability + globalVars)+2)/4.0);
		
		return joyPotential;
	}

	
	public static double calcDistressPotential(double distressFactor, double desirability, double globalVars)
	{
		double distressPotential = 0;
		distressPotential = distressFactor*(((desirability + globalVars)+2)/4.0);
		
		return distressPotential;
	}
	
	// ***** Joy/Distress *****
	
	
	// ***** Fortunes of others emotions *****
	
	public static double calcHappyForPotential(double happyForFactor, double desireSelf, double desireOther,
			double deservingness, double liking, double globalVars)
	{
		double happyForPotential = 0;
		happyForPotential = happyForFactor*(((desireSelf+desireOther+deservingness+liking+globalVars)+5)/10.0);
		
		return happyForPotential;
	}
	
	
	public static double calcSorryForPotential(double sorryForFactor, double desireSelf, double desireOther,
			double deservingness, double liking, double globalVars)
	{
		double sorryForPotential = 0;
		sorryForPotential = sorryForFactor*(((desireSelf+desireOther-deservingness+liking+globalVars)+5)/10.0);
		
		return sorryForPotential;
	}
	
	
	public static double calcResentmentPotential(double resentmentFactor, double desireSelf, double desireOther,
			double deservingness, double liking, double globalVars)
	{
		double resentmentPotential = 0;
		resentmentPotential = resentmentFactor*(((desireSelf+desireOther-deservingness-liking+globalVars)+5)/10.0);
		
		return resentmentPotential;
	}
	
	
	public static double calcGloatingPotential(double gloatingFactor, double desireSelf, double desireOther,
			double deservingness, double liking, double globalVars)
	{
		double gloatingPotential = 0;
		gloatingPotential = gloatingFactor*(((desireSelf+desireOther+deservingness-liking+globalVars)+5)/10.0);
		
		return gloatingPotential;
	}
	
	// ***** Fortunes of others emotions *****
	
	// ***** Hope/Fear (likelihood emotions) *****
	
	public static double calcHopePotential(double hopeFactor, double desirability, double likelihood,
			double globalVars)
	{
		double hopePotential = 0;
		hopePotential = hopeFactor*(((desirability+likelihood+globalVars)+3)/6.0);
		
		return hopePotential;
	}
	
	
	public static double calcFearPotential(double fearFactor, double desirability, double likelihood,
			double globalVars)
	{
		double fearPotential = 0;
		fearPotential = fearFactor*(((desirability+likelihood+globalVars)+3)/6.0);
		
		return fearPotential;
	}
	
	// ***** Hope/Fear (likelihood emotions) *****
	
	
	// ***** Believed Confirmation/Disconfirmation emotions *****
	public static double calcSatisfactionPotential(double satisfactionFactor, double prevHopePotential,
			double effort, double realization, double globalVars)
	{
		double satisfactionPotential = 0;
		satisfactionPotential = satisfactionFactor*((((prevHopePotential/10)+effort+realization+globalVars)+4)/8.0);
		
		return satisfactionPotential;
	}
	
	
	public static double calcFearsConfPotential(double fearsConfFactor, double prevFearPotential,
			double effort, double realization, double globalVars)
	{
		double fearsConfPotential = 0;
		fearsConfPotential = fearsConfFactor*((((prevFearPotential/10)+effort+realization+globalVars)+4)/8.0);
		
		return fearsConfPotential;
	}
	
	
	public static double calcReliefPotential(double reliefFactor, double prevFearPotential,
			double effort, double realization, double globalVars)
	{
		double reliefPotential = 0;
		reliefPotential = reliefFactor*((((prevFearPotential/10)+effort+realization+globalVars)+4)/8.0);
		
		return reliefPotential;
	}
	
	
	public static double calcDisappointmentPotential(double disappointmentFactor, double prevHopePotential,
			double effort, double realization, double globalVars)
	{
		double disappointmentPotential = 0;
		disappointmentPotential = disappointmentFactor*((((prevHopePotential/10)+effort+realization+globalVars)+4)/8.0);
		
		return disappointmentPotential;
	}
	
	// ***** Believed Confirmation/Disconfirmation emotions *****
	
	
	// ***** Agent based emotions *****
	
	// ***** Agent based with Cognitive Unit *****
	
	public static double calcPridePotential(double prideFactor, double praiseworthiness, double diffExpectation,
			double cogUnit, double globalVars)
	{
		double pridePotential = 0;
		pridePotential = prideFactor*(((praiseworthiness + diffExpectation + cogUnit + globalVars)+4)/8.0);
		
		return pridePotential;
	}
	
	
	public static double calcSelfReproachPotential(double selfReproachFactor, double praiseworthiness, 
			double diffExpectation, double cogUnit, double globalVars)
	{
		double selfReproachPotential = 0;
		selfReproachPotential = selfReproachFactor*(((praiseworthiness + diffExpectation + cogUnit + globalVars)+4)/8.0);
		
		return selfReproachPotential;
	}
	
	// ***** Agent based with Cognitive Unit *****
	
	
	// ***** Agent based without Cognitive Unit *****
	
	public static double calcAppreciationPotential(double appreciationFactor, double praiseworthiness, 
			double diffExpectation, double globalVars)
	{
		double appreciationPotential = 0;
		appreciationPotential = appreciationFactor*(((praiseworthiness + diffExpectation + globalVars)+3)/6.0);
		
		return appreciationPotential;
	}
	
	
	public static double calcReproachPotential(double reproachFactor, double praiseworthiness, 
			double diffExpectation, double globalVars)
	{
		double reproachPotential = 0;
		reproachPotential = reproachFactor*(((praiseworthiness + diffExpectation + globalVars)+3)/6.0);
		
		return reproachPotential;
	}
	
	// ***** Agent based without Cognitive Unit *****
	
	// ***** Agent based emotions *****
	
	
	
	// ***** Event and Agent based emotions *****
	// ***** Event and Agent based emotions without Cognitive Unit *****
	
	public static double calcGratitudePotential(double gratitudeFactor, double praiseworthiness,
			double desirability, double diffExpectation, double agentGlobalVars, 
			double eventGlobalVars)
	{
		double gratitudePotential = 0;
		gratitudePotential = gratitudeFactor*(((praiseworthiness + desirability + diffExpectation +
				agentGlobalVars + eventGlobalVars)+5)/10.0);
		
		return gratitudePotential;
	}
	
	
	public static double calcAngerPotential(double angerFactor, double praiseworthiness,
			double desirability, double diffExpectation, double agentGlobalVars, 
			double eventGlobalVars)
	{
		double angerPotential = 0;
		angerPotential = angerFactor*(((praiseworthiness + desirability + diffExpectation +
				agentGlobalVars + eventGlobalVars)+5)/10.0);
		
		return angerPotential;
	}
	
	// ***** Event and Agent based emotions without Cognitive Unit *****
	
	
	// ***** Event and Agent based emotions with Cognitive Unit *****
	
	public static double calcGratificationPotential(double gratificationFactor, double praiseworthiness,
			double desirability, double diffExpectation, double cogUnit, double agentGlobalVars, 
			double eventGlobalVars)
	{
		double gratificationPotential = 0;
		gratificationPotential = gratificationFactor*(((praiseworthiness + desirability + diffExpectation +
				cogUnit + agentGlobalVars + eventGlobalVars)+6)/12.0);
		
		return gratificationPotential;
	}
	
	
	public static double calcRemorsePotential(double remorseFactor, double praiseworthiness,
			double desirability, double diffExpectation, double cogUnit, double agentGlobalVars, 
			double eventGlobalVars)
	{
		double remorsePotential = 0;
		remorsePotential = remorseFactor*(((praiseworthiness + desirability + diffExpectation +
				cogUnit + agentGlobalVars + eventGlobalVars)+6)/12.0);
		
		return remorsePotential;
	}
	
	// ***** Event and Agent based emotions with Cognitive Unit *****	
	// ***** Event and Agent based emotions *****
	
	
	// ***** Object based emotions *****
	
	public static double calcLikingPotential(double likingFactor, double appealingness, double familiarity,
			double globalVars)
	{
		double likingPotential = 0;
		likingPotential = likingFactor*(((appealingness + familiarity + globalVars)+3)/6.0);
		
		return likingPotential;
	}
	
	
	public static double calcDislikingPotential(double dislikingFactor, double appealingness, double familiarity,
			double globalVars)
	{
		double dislikingPotential = 0;
		dislikingPotential = dislikingFactor*(((appealingness + familiarity + globalVars)+3)/6.0);
		
		return dislikingPotential;
	}
	
	// ***** Object based emotions *****
}
