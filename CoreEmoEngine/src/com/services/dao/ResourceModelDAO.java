package com.services.dao;

import java.util.List;

import com.services.entities.ResourceModel;

public interface ResourceModelDAO {
	
	public void saveModel(ResourceModel resModel);
	public List<ResourceModel> getModelsBySimUserId(String userId, Long simId);

}
