package com.services.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.services.entities.EventTab;

public interface EventTabDAO extends JpaRepository<EventTab, Long>, JpaSpecificationExecutor<EventTab>{

	@Query("from EventTab et where et.simId = :simId AND et.userId = :userId AND et.iterNo = :iterNo AND et.eventId = :eventId AND et.agentId = :agentId")
	public EventTab getEventsBySimIdUserIdIterEventAndAgent(
			@Param("simId") Long simId, @Param("userId") String userId,
			@Param("iterNo") Long iterNo, @Param("eventId") Long eventId,
			@Param("agentId") Long agentId);

}
