package emoDAO;

import java.util.List;

import emoCoreServiceObjects.ResourceModel;

public interface ResourceModelDAO {
	
	public void saveModel(ResourceModel resModel);
	public List<ResourceModel> getModelsBySimUserId(String userId, Long simId);

}
