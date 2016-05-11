package com.services.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.services.entities.AgentTab;

public interface AgentTabDAO extends JpaRepository<AgentTab, Long> {

	@Query("from AgentTab at where at.userId = :userId AND at.simId = :simId AND at.agentId = :agentId AND at.iterNo = :iterNo")
	public AgentTab getAgentByUserIdSimIdAgentIdAndIter(
			@Param("userId") String userId, @Param("simId") Long simId,
			@Param("agentId") Long agentId, @Param("iterNo") Long iterNo);

}
