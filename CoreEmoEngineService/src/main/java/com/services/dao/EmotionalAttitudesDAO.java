package com.services.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.services.entities.EmotionalAttitudes;

public interface EmotionalAttitudesDAO extends
		JpaRepository<EmotionalAttitudes, Long> {

	@Query("from EmotionalAttitudes ea where ea.agentId = :agentId AND ea.userId = :userId AND ea.iterNo = :iterNo AND ea.simId = :simId")
	public List<EmotionalAttitudes> getEmoAttByAgentIdUserIdIterAndSimId(
			@Param("agentId") Long agentId, @Param("userId") String userId,
			@Param("iterNo") Long iterNo, @Param("simId") Long simId);
	


}
