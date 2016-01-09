package com.services.psychological.core;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ConstantVariables {

	private String dbURL;
	private String dbName;
	private String dbUserName;
	private String dbPassword;
	private String userID;
	private Long simulationId;
	
	@Autowired
	@Qualifier("psycheConfig")
	private Properties psycheConfig;
	
	private  String dbConnectString;
	private  String dbDriver;
	
	public ConstantVariables(String userID, Long simulationId)
	{
		this.userID = userID;
		this.simulationId = simulationId;
		dbURL = psycheConfig.getProperty("database_url");
		dbName = psycheConfig.getProperty("database_name");
		dbUserName = psycheConfig.getProperty("db_user_name");
		dbPassword = psycheConfig.getProperty("db_user_password");
		dbConnectString = dbURL + dbName;
		dbDriver = psycheConfig.getProperty("db_driver");
	}
	
	public String getDBConnection(){return dbConnectString;}
	public String getDBUserName(){return dbUserName;}
	public String getDBPassword(){return dbPassword;}
	public String getDBDriver(){return dbDriver;}
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
