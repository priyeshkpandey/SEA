package com.services.psychological.core;

import org.springframework.stereotype.Component;

@Component
public class ModelVariables {

	private String userID;
	private Long simulationId;
	
	public ModelVariables()
	{

	}
	
	
	
	public String getUserID() {
		return userID;
	}



	public void setUserID(String userID) {
		this.userID = userID;
	}



	public Long getSimulationId() {
		return simulationId;
	}



	public void setSimulationId(Long simulationId) {
		this.simulationId = simulationId;
	}

	
	public String getUserId(){return userID;}
	public Long getSimId(){return simulationId;}

	
	public  String queryModelResources() { return "SELECT * FROM model_resources"
			+ " WHERE simulation_id = ? AND" + " agent_id = ? AND  user_id = '"
			+ userID + "'";}
	
	public  String querySimDataBySimID() {return "SELECT * FROM simulation_data_tab WHERE simulation_id = ?"
			+ " AND user_id = '" + userID + "'";}
	public  String queryEmoAttByAgIDIter(){return "SELECT * FROM emo_attitudes WHERE iteration_no = ?"
			+ " AND agent_id = ? AND user_id = '"
			+ userID
			+ "' AND simulation_id = " + simulationId;}
	public  String updSimDataWrkItrBySimID() {return "UPDATE simulation_data_tab SET working_iteration = ?"
			+ " WHERE (simulation_id = ? AND user_id = '"
			+ userID
			+ "' AND simulation_id = "
			+ simulationId + ")";}
	public  String updEmoAttOccurCount(){return "UPDATE emo_attitudes SET occurrence_count = ?, occurred_count = ?"
			+ " WHERE emotions = ?"
			+ " AND iteration_no = ?"
			+ " AND agent_id = ?"
			+ " AND user_id = '"
			+ userID
			+ "'"
			+ " AND simulation_id = "
			+ simulationId;}
	public  String updAgentPerceptions(){return "UPDATE agent_tab SET is_event = ?, is_agent = ?"
			+ ", is_object = ? WHERE agent_id = ?"
			+ " AND iteration_no = ?"
			+ " AND user_id = '"
			+ userID
			+ "'"
			+ " AND simulation_id = " + simulationId;}
	public  String queryAgentTable(){return "SELECT * FROM agent_tab WHERE iteration_no = ?"
			+ " AND agent_id = ? AND user_id = '"
			+ userID
			+ "'" + " AND simulation_id = " + simulationId;}
	public  String queryEventTab(){return "SELECT * FROM event_tab WHERE iteration_no = ?"
			+ " AND event_id = ? AND agent_id = ? AND user_id = '"
			+ userID
			+ "'"
			+ " AND simulation_id = "
			+ simulationId;}
	public  String queryAgentNeighbours(){return "SELECT * FROM agent_network WHERE iteration_no = ?"
			+ " AND agent_id1 = ? AND event_involved = ? AND user_id = '"
			+ userID
			+ "'"
			+ " AND simulation_id = "
			+ simulationId;}
	public  String queryAgentNetwork(){return "SELECT * FROM agent_network WHERE iteration_no = ?"
			+ " AND agent_id1 = ? AND agent_id2 = ? AND user_id = '"
			+ userID
			+ "'"
			+ " AND simulation_id = "
			+ simulationId;}
	public  String queryPrevState(){return "SELECT * FROM previous_state WHERE iteration_no = ? AND event_id = ? AND agent_id = ? AND emotion = '?'"
			+ " AND user_id = '"
			+ userID
			+ "'"
			+ " AND simulation_id = " + simulationId;}
	public  String queryObjectTab(){return "SELECT * FROM object_tab WHERE iteration_no = ? AND object_id = ? AND agent_id = ? AND user_id = '"
			+ userID
			+ "'"
			+ " AND simulation_id = "
			+ simulationId;}
	public  String insObservedEmos(){return "INSERT INTO observed_emotions (iteration_no,agent_id,target_event,target_neighbour,target_object,emotion,emotion_potential,emotion_intensity,user_id,simulation_id) VALUES(?, ?, ?, ?, ?, ?, ?, ?, '"
			+ userID
			+ "', "
			+ simulationId
			+ ")";}
	public  String insPrevState(){return "INSERT INTO previous_state VALUES(?, ?, ?, ?, ?, ?, '"
			+ userID
			+ "', "
			+ simulationId
			+ ")";}
	public  String queryVarMap(){return "SELECT * FROM variable_mappings WHERE variable_id = ?";}
	public  String queryInteractModels(){return "SELECT * FROM interaction_attitudes WHERE iteration_no = ? AND agent_id1 = ? AND user_id = '"
			+ userID
			+ "'"
			+ " AND simulation_id = "
			+ simulationId;}

	

}
