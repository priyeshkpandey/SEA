package psychologicalCore;

public class ConstantVariables {

	public static String dbURL;
	public static String dbName;
	public static String dbUserName;
	public static String dbPassword;
	public static String userID;
	public static Long simulationId;

	public static String dbConnectString = dbURL + dbName;
	public static String dbDriver;
	public static String queryModelResources = "SELECT * FROM model_resources"
			+ " WHERE simulation_id = ? AND" + " agent_id = ? AND  user_id = '"
			+ ConstantVariables.userID + "'";
	public static String querySimDataBySimID = "SELECT * FROM simulation_data_tab WHERE simulation_id = ?"
			+ " AND user_id = '" + ConstantVariables.userID + "'";
	public static String queryEmoAttByAgIDIter = "SELECT * FROM emo_attitudes WHERE iteration_no = ?"
			+ " AND agent_id = ? AND user_id = '"
			+ ConstantVariables.userID
			+ "' AND simulation_id = " + ConstantVariables.simulationId;
	public static String updSimDataWrkItrBySimID = "UPDATE simulation_data_tab SET working_iteration = ?"
			+ " WHERE (simulation_id = ? AND user_id = '"
			+ ConstantVariables.userID
			+ "' AND simulation_id = "
			+ ConstantVariables.simulationId + ")";
	public static String updEmoAttOccurCount = "UPDATE emo_attitudes SET occurrence_count = ?, occurred_count = ?"
			+ " WHERE emotions = ?"
			+ " AND iteration_no = ?"
			+ " AND agent_id = ?"
			+ " AND user_id = '"
			+ ConstantVariables.userID
			+ "'"
			+ " AND simulation_id = "
			+ ConstantVariables.simulationId;
	public static String updAgentPerceptions = "UPDATE agent_tab SET is_event = ?, is_agent = ?"
			+ ", is_object = ? WHERE agent_id = ?"
			+ " AND iteration_no = ?"
			+ " AND user_id = '"
			+ ConstantVariables.userID
			+ "'"
			+ " AND simulation_id = " + ConstantVariables.simulationId;
	public static String queryAgentTable = "SELECT * FROM agent_tab WHERE iteration_no = ?"
			+ " AND agent_id = ? AND user_id = '"
			+ ConstantVariables.userID
			+ "'" + " AND simulation_id = " + ConstantVariables.simulationId;
	public static String queryEventTab = "SELECT * FROM event_tab WHERE iteration_no = ?"
			+ " AND event_id = ? AND agent_id = ? AND user_id = '"
			+ ConstantVariables.userID
			+ "'"
			+ " AND simulation_id = "
			+ ConstantVariables.simulationId;
	public static String queryAgentNeighbours = "SELECT * FROM agent_network WHERE iteration_no = ?"
			+ " AND agent_id1 = ? AND event_involved = ? AND user_id = '"
			+ ConstantVariables.userID
			+ "'"
			+ " AND simulation_id = "
			+ ConstantVariables.simulationId;
	public static String queryAgentNetwork = "SELECT * FROM agent_network WHERE iteration_no = ?"
			+ " AND agent_id1 = ? AND agent_id2 = ? AND user_id = '"
			+ ConstantVariables.userID
			+ "'"
			+ " AND simulation_id = "
			+ ConstantVariables.simulationId;
	public static String queryPrevState = "SELECT * FROM previous_state WHERE iteration_no = ? AND event_id = ? AND agent_id = ? AND emotion = '?'"
			+ " AND user_id = '"
			+ ConstantVariables.userID
			+ "'"
			+ " AND simulation_id = " + ConstantVariables.simulationId;
	public static String queryObjectTab = "SELECT * FROM object_tab WHERE iteration_no = ? AND object_id = ? AND agent_id = ? AND user_id = '"
			+ ConstantVariables.userID
			+ "'"
			+ " AND simulation_id = "
			+ ConstantVariables.simulationId;
	public static String insObservedEmos = "INSERT INTO observed_emotions (iteration_no,agent_id,target_event,target_neighbour,target_object,emotion,emotion_potential,emotion_intensity,user_id,simulation_id) VALUES(?, ?, ?, ?, ?, ?, ?, ?, '"
			+ ConstantVariables.userID
			+ "', "
			+ ConstantVariables.simulationId
			+ ")";
	public static String insPrevState = "INSERT INTO previous_state VALUES(?, ?, ?, ?, ?, ?, '"
			+ ConstantVariables.userID
			+ "', "
			+ ConstantVariables.simulationId
			+ ")";
	public static String queryVarMap = "SELECT * FROM variable_mappings WHERE variable_id = ?";
	public static String queryInteractModels = "SELECT * FROM interaction_attitudes WHERE iteration_no = ? AND agent_id1 = ? AND user_id = '"
			+ ConstantVariables.userID
			+ "'"
			+ " AND simulation_id = "
			+ ConstantVariables.simulationId;

	public static final String EVENT_EMOTIONS = "joy,distress,happy-for,sorry-for,resentment,gloating,"
			+ "hope,fear,satisfaction,fears-confirmed,relief,disappointment";
	public static final String AGENT_EMOTIONS = "pride,self-reproach,appreciation,reproach";
	public static final String EVENT_AGENT_EMOTIONS = "gratitude,anger,gratification,remorse";
	public static final String OBJECT_EMOTIONS = "liking,disliking";
	public static final String NEIGHBOUR_TABLES = "agent_network,interaction_attitudes";
	public static final String UNCHANGED_IN_INTERACTION = "interaction_attitudes";

}
