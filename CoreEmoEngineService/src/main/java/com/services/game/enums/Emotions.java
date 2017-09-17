package com.services.game.enums;

import java.util.List;
import java.util.Random;

public enum Emotions {

	JOY("joy"), DISTRESS("distress"), HAPPY_FOR("happy-for"), SORRY_FOR("sorry-for"), RESENTMENT("resentment"), GLOATING("gloating"),
	HOPE("hope"), FEAR("fear"), SATISFACTION("satisfaction"), FEARS_CONFIRMED("fears-confirmed"), RELIEF("relief"), DISAPPOINTMENT("disappointment"), 
	PRIDE("pride"), SELF_REPROACH("self-reproach"), APPRECIATION("appreciation"), REPROACH("reproach"), GRATITUDE("gratitude"), ANGER("anger"), 
	GRATIFICATION("gratification"), REMORSE("remorse"), LIKING("liking"), DISLIKING("disliking");
	
	private String emotion;
	private static Random random = new Random();
	
	Emotions(String emotion) {
		this.emotion = emotion;
	}
	
	public String getEmotion() { return emotion; }
	
	public static String getRandomEmotionExcept(List<String> emotions) {
		String returnedEmotion = emotions.get(0);
		while(emotions.contains(returnedEmotion)) {
			returnedEmotion = Emotions.values()[random.nextInt(Emotions.values().length)].name();
			if (!emotions.contains(returnedEmotion)) {
				return returnedEmotion;
			}
		}
		return null;
	}
	
}
