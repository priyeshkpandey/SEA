package com.services.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.services.entities.ResourceModel;

public interface ResourceModelDAO extends JpaRepository<ResourceModel, Long>{
	
	//@Query("")
	//public void saveModel(ResourceModel resModel);
	
	@Query("from ResourceModel rm where rm.userId = :userId and rm.simId = :simId")
	public List<ResourceModel> getModelsBySimUserId(@Param("userId") String userId, 
			@Param("simId") Long simId);

}
