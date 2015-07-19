package emoDAO;

import java.util.List;

import emoCoreServiceObjects.ObservedEmotions;

public interface ObservedEmotionsDAO {
	
	public List<ObservedEmotions> getObsEmosByUserSimId(String userId, Long simId);

}
