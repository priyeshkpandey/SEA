package com.services.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.services.entities.PreviousState;

public interface PreviousStateDAO extends JpaRepository<PreviousState, Long> {

	@Query("from PreviousState ps where ps.userId = :userId AND ps.simId = :simId AND ps.iterNo = :iterNo AND ps.agentId = :agentId AND ps.eventId = :eventId AND ps.emotion = :emotion")
	public List<PreviousState> getPrevStateByIterNoAgentEventEmotionUserAndSim(
			@Param("userId") String userId, @Param("simId") Long simId,
			@Param("iterNo") Long iterNo, @Param("agentId") Long agentId,
			@Param("eventId") Long eventId, @Param("emotion") String emotion);

}
