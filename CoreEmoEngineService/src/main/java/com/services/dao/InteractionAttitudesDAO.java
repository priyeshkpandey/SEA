package com.services.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.services.entities.InteractionAttitudes;

public interface InteractionAttitudesDAO extends
		JpaRepository<InteractionAttitudes, Long>,
		JpaSpecificationExecutor<InteractionAttitudes> {

	@Query("from InteractionAttitudes ia where ia.iterNo = :iterNo AND ia.agentId1 = :agentId1 AND ia.userId = :userId AND ia.simId = :simId order by ia.precedence desc")
	public List<InteractionAttitudes> getAttitudesByIterAgentUserAndSimIdPrecedenceSorted(
			@Param("iterNo") Long iterNo, @Param("agentId1") Long agentId1,
			@Param("userId") String userId, @Param("simId") Long simId);

}
