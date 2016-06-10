package com.services.psychological.core;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.services.dao.ResourceModelDAO;
import com.services.dao.VariableMappingDAO;
import com.services.entities.ResourceModel;
import com.services.entities.VariableMapping;

@Component
public class SingleStepComponent {

	HashMap<String, HashMap<String, Object>> models = new HashMap<String, HashMap<String, Object>>();

	private ConstantVariables constVars;
	private Long simulationId;
	private String userID;
	private Long agentId;
	private Long currIter;
	private Long sourceAgent;
	private Long thirdPerson;
	private Long targetAgent;
	private Long targetEvent;
	private Long targetObject;
	private String targetEmotion;
	private String targetVariable;

	private ArrayList<String> keyRowInserted = new ArrayList<String>();
	private HashMap<String, ArrayList<String>> varsToTableMetaData = new HashMap<String, ArrayList<String>>();
	private HashMap<String, ArrayList<Object>> interactionAttitudes = new HashMap<String, ArrayList<Object>>();
	private HashMap<String, Long> interactionPrecedences = new HashMap<String, Long>();
	private int lastIndex;

	@Autowired
	ApplicationContext context;

	public SingleStepComponent(Long iter, Long agent, Long thrdPerson,
			ConstantVariables constVars) {
		simulationId = constVars.getSimId();
		agentId = agent;
		currIter = iter;
		thirdPerson = thrdPerson;
		this.userID = constVars.getUserId();
		this.constVars = constVars;
	}

	public void initModels() {

		ResourceModelDAO modelDAO = context.getBean(ResourceModelDAO.class);
		List<ResourceModel> modelsList = modelDAO.getModelsBySimUserIdAgentId(
				constVars.getUserId(), constVars.getSimId(), agentId);

		if (!models.isEmpty()) {
			models.clear();
		}

		ArrayList<String> varsToModel = new ArrayList<String>();

		for (ResourceModel model : modelsList) {
			HashMap<String, Object> valueList = new HashMap<String, Object>();

			varsToModel.add(model.getVarToModel());

			String keyVal = model.getVarToModel() + ","
					+ model.getTargetAgent() + "," + model.getTargetEvent()
					+ "," + model.getTargetObject() + ","
					+ model.getTargetEmotion() + "," + model.getTargetEmotion()
					+ "," + model.getIsInteraction() + ","
					+ model.getSourceAgent();

			valueList.put("is_function", model.getIsFunction());
			valueList.put("class_path", model.getClassPath());
			valueList.put("method_name", model.getMethodName());
			valueList.put("return_type", model.getReturnType());
			valueList.put("file_path", model.getFilePath());
			valueList.put("tab_name", model.getTabName());

			models.put(keyVal, valueList);
		}

		Iterator<String> iterVars = varsToModel.iterator();
		String varName = null;
		StringBuffer vars = new StringBuffer("");

		while (iterVars.hasNext()) {
			String var = iterVars.next();
			vars.append(var + ",");
		}

		lastIndex = vars.length();
		vars.delete(lastIndex - 1, lastIndex);

		VariableMappingDAO varMapDAO = context
				.getBean(VariableMappingDAO.class);
		List<VariableMapping> varMappings = varMapDAO
				.getMappingsForVariables(vars.toString());
		
		for(VariableMapping varMap:varMappings)
		{
			varName = varMap.getVariableId();

			ArrayList<String> tmpTableMetaData = new ArrayList<String>();

			tmpTableMetaData.add(varMap.getTableName());
			tmpTableMetaData.add(varMap.getColName());
			tmpTableMetaData.add(varMap.getCondCols());
			tmpTableMetaData.add(varMap.getIsInteractionInvolved().toString());

			varsToTableMetaData.put(varName, tmpTableMetaData);
		}
		
		//TODO insertion, update, agent's emotion and interactions
		
		

	}
	
	
	private void insertTableValues(String keyVal) throws SQLException {
		String var = keyVal.split(",")[0];
		targetAgent = Long.parseLong(keyVal.split(",")[1]);
		targetEvent = Long.parseLong(keyVal.split(",")[2]);
		targetObject = Long.parseLong(keyVal.split(",")[3]);
		targetEmotion = keyVal.split(",")[4];
		targetVariable = keyVal.split(",")[5];
		sourceAgent = Long.parseLong(keyVal.split(",")[7]);

		if (!keyRowInserted.contains(keyVal)) {
			String tableName = varsToTableMetaData.get(var).get(0);
			String condCols = varsToTableMetaData.get(var).get(2);

			StringBuffer insertQuery = new StringBuffer("INSERT INTO "
					+ tableName + "(");
			StringBuffer existentialQuery = new StringBuffer("SELECT * FROM "
					+ tableName + " WHERE ");

			String[] condColsArray = condCols.split(",");

			int noOfCondCols = condColsArray.length;

			for (int i = 0; i < noOfCondCols; i++) {
				insertQuery.append("" + condColsArray[i] + ", ");
			}

			lastIndex = insertQuery.length();
			insertQuery.delete(lastIndex - 2, lastIndex).append(") VALUES(");

			for (int i = 0; i < noOfCondCols; i++) {

				if (condColsArray[i].equalsIgnoreCase("iteration_no")) {
					insertQuery.append("" + currIter + ", ");
					existentialQuery.append(condColsArray[i] + " = " + currIter
							+ " AND");
				} else if (condColsArray[i].equalsIgnoreCase("agent_id")
						|| condColsArray[i].equalsIgnoreCase("agent_id1")) {
					insertQuery.append("" + agentId + ", ");
					existentialQuery.append(condColsArray[i] + " = "
							+ sourceAgent + " AND");
				} else if (condColsArray[i].equalsIgnoreCase("agent_id2")) {
					insertQuery.append("" + targetAgent + ", ");
					existentialQuery.append(condColsArray[i] + " = "
							+ targetAgent + " AND");
				} else if (condColsArray[i].equalsIgnoreCase("event_id")
						|| condColsArray[i].equalsIgnoreCase("event_involved")
						|| condColsArray[i].equalsIgnoreCase("target_event")) {
					insertQuery.append("" + targetEvent + ", ");
					existentialQuery.append(condColsArray[i] + " = "
							+ targetEvent + " AND");
				} else if (condColsArray[i].equalsIgnoreCase("emotions")
						|| condColsArray[i].equalsIgnoreCase("target_emotion")) {
					insertQuery.append("'" + targetEmotion + "', ");
					existentialQuery.append(condColsArray[i] + " = "
							+ targetEmotion + " AND");
				} else if (condColsArray[i].equalsIgnoreCase("object_id")
						|| condColsArray[i].equalsIgnoreCase("target_object")) {
					insertQuery.append("" + targetObject + ", ");
					existentialQuery.append(condColsArray[i] + " = "
							+ targetObject + " AND");
				} else if (condColsArray[i].equalsIgnoreCase("user_id")) {
					insertQuery.append("'" + constVars.getUserId() + "', ");
					existentialQuery.append(condColsArray[i] + " = "
							+ constVars.getUserId() + " AND");
				} else if (condColsArray[i].equalsIgnoreCase("variable")) {
					insertQuery.append("'" + targetVariable + "', ");
					existentialQuery.append(condColsArray[i] + " = "
							+ targetVariable + " AND");
				} else if (condColsArray[i].equalsIgnoreCase("third_person")) {
					insertQuery.append(thirdPerson + ", ");
					existentialQuery.append(condColsArray[i] + " = "
							+ thirdPerson + " AND");
				}

			}

			lastIndex = existentialQuery.length();
			existentialQuery.delete(lastIndex - 4, lastIndex);

			lastIndex = insertQuery.length();
			insertQuery.delete(lastIndex - 2, lastIndex).append(")");

			Statement existsStmt = conn.createStatement();
			boolean isRowExists = existsStmt.execute(existentialQuery
					.toString());
			existsStmt.close();

			if (!isRowExists) {
				Statement insStmt = conn.createStatement();

				int noOfRowsInserted = insStmt.executeUpdate(insertQuery
						.toString());

				if (noOfRowsInserted > 0) {
					System.out.println("Successfully inserted in " + tableName
							+ " keys " + keyVal + ". No of Rows "
							+ noOfRowsInserted);
					keyRowInserted.add(keyVal);
				}

				insStmt.close();
			} else {
				System.out
						.println("Row already exists, no insert fired. Select query: "
								+ existentialQuery.toString());
			}

		}
	}
	
	

}
