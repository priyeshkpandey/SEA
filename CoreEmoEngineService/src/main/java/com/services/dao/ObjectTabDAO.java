package com.services.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.services.entities.ObjectTab;

public interface ObjectTabDAO extends JpaRepository<ObjectTab, Long>, JpaSpecificationExecutor<ObjectTab>{

	@Query("from ObjectTab ot where ot.iterNo = :iterNo AND ot.objectId = :objectId AND ot.agentId = :agentId AND ot.userId = :userId AND ot.simId = :simId")
	public ObjectTab getObjectByIterObjectIdAgentUserAndSimId(
			@Param("iterNo") Long iterNo, @Param("objectId") Long objectId,
			@Param("agentId") Long agentId, @Param("userId") String userId,
			@Param("simId") Long simId);

	@Query("from ObjectTab ot where ot.iterNo = :iterNo AND ot.agentId = :agentId AND ot.userId = :userId AND ot.simId = :simId")
	public List<ObjectTab> getObjectsForAgentByIterUserAndSimId(
			@Param("iterNo") Long iterNo, @Param("agentId") Long agentId, 
			@Param("userId") String userId, @Param("simId") Long simId);
}
