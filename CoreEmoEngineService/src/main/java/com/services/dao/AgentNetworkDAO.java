package com.services.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.services.entities.AgentNetwork;

public interface AgentNetworkDAO extends JpaRepository<AgentNetwork, Long>, JpaSpecificationExecutor<AgentNetwork> {

	@Query("from AgentNetwork an where an.simId = :simId AND an.userId = :userId AND an.iterNo = :iterNo AND an.agentId1 = :agentId1 AND an.eventInvolved = :eventInvolved")
	public List<AgentNetwork> getAgentNeighbours(@Param("simId") Long simId,
			@Param("userId") String userId, @Param("iterNo") Long iterNo,
			@Param("agentId1") Long agentId1,
			@Param("eventInvolved") Long eventInvolved);
	
    @Query("from AgentNetwork an where an.simId = :simId AND an.userId = :userId AND an.iterNo = :iterNo AND an.agentId1 = :agentId1 AND an.agentId2 = :agentId2 "
    		+ "AND an.eventInvolved = :eventInvolved")
	public AgentNetwork getNetworkByIterAgentsEventUserIdAndSimId(@Param("iterNo") Long iterNo, @Param("agentId1") Long agentId1, @Param("agentId2") Long agentId2,
			@Param("eventInvolved") Long eventInvolved, @Param("userId") String userId, @Param("simId") Long simId);

}
