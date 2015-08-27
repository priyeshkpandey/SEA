package com.services.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.services.entities.ObservedEmotions;

public interface ObservedEmotionsDAO extends JpaRepository <ObservedEmotions, Long> {
	
	@Query("from ObservedEmotions oe where oe.userId = :userId and oe.simId = :simId")
	public List<ObservedEmotions> getObsEmosByUserSimId(
			@Param("userId") String userId, 
			@Param("simId") Long simId);

}
