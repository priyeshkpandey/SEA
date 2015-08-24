package com.services.dao;

import java.util.List;

import com.services.entities.ObservedEmotions;

public interface ObservedEmotionsDAO {
	
	public List<ObservedEmotions> getObsEmosByUserSimId(String userId, Long simId);

}
