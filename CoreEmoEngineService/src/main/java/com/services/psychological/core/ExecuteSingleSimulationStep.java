package com.services.psychological.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

public class ExecuteSingleSimulationStep {

	HashMap<String, HashMap<String, Object>> models = new HashMap<String, HashMap<String, Object>>();

	private ModelVariables constVars;
	private Long simulationId;
	private String userID;
	private Integer agentId;
	private Long currIter;
	private Integer sourceAgent;
	private Integer thirdPerson;
	private Integer targetAgent;
	private Integer targetEvent;
	private Integer targetObject;
	private String targetEmotion;
	private String targetVariable;

	private ArrayList<String> keyRowInserted = new ArrayList<String>();
	private HashMap<String, ArrayList<String>> varsToTableMetaData = new HashMap<String, ArrayList<String>>();
	private HashMap<String, ArrayList<Object>> interactionAttitudes = new HashMap<String, ArrayList<Object>>();
	private HashMap<String, Integer> interactionPrecedences = new HashMap<String, Integer>();
	private int lastIndex;
	Connection conn;

	public ExecuteSingleSimulationStep(Long currIter, Integer agent,
			Integer thirdPerson, ModelVariables constVars) {
		simulationId = constVars.getSimId();
		agentId = agent;
		this.currIter = currIter;
		this.thirdPerson = thirdPerson;
		this.userID = constVars.getUserId();
		this.constVars = constVars;
	}

	public void initModels() {
		
		 

		try {
			Class.forName(constVars.getDBDriver()).newInstance();
			conn = DriverManager.getConnection(
					constVars.getDBConnection(),
					constVars.getDBUserName(), constVars.getDBPassword());

			PreparedStatement psModels = conn
					.prepareStatement(constVars.queryModelResources());
			psModels.setLong(1, simulationId);
			psModels.setInt(2, agentId);
			ResultSet rsModels = psModels.executeQuery();

			if (!models.isEmpty()) {
				models.clear();
			}

			ArrayList<String> varsToModel = new ArrayList<String>();
			while (rsModels.next()) {

				HashMap<String, Object> valueList = new HashMap<String, Object>();

				varsToModel.add(rsModels.getString("variable_to_model"));

				String keyVal = rsModels.getString("variable_to_model") + ","
						+ rsModels.getInt("target_agent") + ","
						+ rsModels.getInt("target_event") + ","
						+ rsModels.getInt("target_object") + ","
						+ rsModels.getInt("target_emotion") + ","
						+ rsModels.getInt("target_variable") + ","
						+ rsModels.getBoolean("is_interaction") + ","
						+ rsModels.getInt("source_agent");

				valueList
						.put("is_function", rsModels.getBoolean("is_function"));
				valueList.put("class_path", rsModels.getString("class_path"));
				valueList.put("method_name", rsModels.getString("method_name"));
				valueList.put("return_type", rsModels.getString("return_type"));
				valueList.put("file_path", rsModels.getString("file_path"));
				valueList.put("tab_name", rsModels.getString("tab_name"));

				models.put(keyVal, valueList);
			}

			rsModels.close();
			psModels.close();

			Iterator<String> iterVars = varsToModel.iterator();
			String varName = null;

			StringBuffer queryVarMapForTables = new StringBuffer(
					"SELECT * FROM variable_mappings WHERE variable_id IN (");

			while (iterVars.hasNext()) {
				String var = iterVars.next();
				queryVarMapForTables.append("'" + var + "', ");
			}

			lastIndex = queryVarMapForTables.length();
			queryVarMapForTables.delete(lastIndex - 2, lastIndex).append(")");

			Statement stmt = conn.createStatement();

			ResultSet rsVarMap = stmt.executeQuery(queryVarMapForTables
					.toString());

			while (rsVarMap.next()) {
				varName = rsVarMap.getString("variable_id");

				ArrayList<String> tmpTableMetaData = new ArrayList<String>();

				tmpTableMetaData.add(rsVarMap.getString("table_name"));
				tmpTableMetaData.add(rsVarMap.getString("column_name"));
				tmpTableMetaData.add(rsVarMap.getString("condition_columns"));
				tmpTableMetaData.add(rsVarMap
						.getString("is_interaction_involved"));

				varsToTableMetaData.put(varName, tmpTableMetaData);

			}

			stmt.close();
			rsVarMap.close();

			// ***** Start insertions in the tables *****

			Iterator<String> iterModels = models.keySet().iterator();

			while (iterModels.hasNext()) {
				String keyVal = iterModels.next();

				String isInteraction = keyVal.split(",")[6];
				insertTableValues(keyVal);

				if (isInteraction.equalsIgnoreCase("0")) {
					setTableValues(keyVal);
				}

			}

			// ***** Individual Agent's Emotions *****

			new IndividualAgent(simulationId, agentId, targetEvent,
					targetObject, constVars).individualEmotionsInvocation();

			// ***** Start interactions and set table values *****

			setInteractions();

			conn.close();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null && (!conn.isClosed())) {
					conn.close();
				}
			} catch (Exception e) {
				System.err.println("SQL connection cannot be closed!");
			}
		}
	}

	private void insertTableValues(String keyVal) throws SQLException {
		String var = keyVal.split(",")[0];
		targetAgent = Integer.parseInt(keyVal.split(",")[1]);
		targetEvent = Integer.parseInt(keyVal.split(",")[2]);
		targetObject = Integer.parseInt(keyVal.split(",")[3]);
		targetEmotion = keyVal.split(",")[4];
		targetVariable = keyVal.split(",")[5];
		sourceAgent = Integer.parseInt(keyVal.split(",")[7]);

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

	private void setTableValues(String modelKey) throws ClassNotFoundException,
			NoSuchMethodException, InvocationTargetException,
			IllegalAccessException, SQLException {

		String var = modelKey.split(",")[0];
		String isInteraction = modelKey.split(",")[6];

		targetAgent = Integer.parseInt(modelKey.split(",")[1]);
		targetEvent = Integer.parseInt(modelKey.split(",")[2]);
		targetObject = Integer.parseInt(modelKey.split(",")[3]);
		targetEmotion = modelKey.split(",")[4];
		targetVariable = modelKey.split(",")[5];
		sourceAgent = Integer.parseInt(modelKey.split(",")[7]);

		String tableName = varsToTableMetaData.get(var).get(0);
		String colName = varsToTableMetaData.get(var).get(1);
		String condCols = varsToTableMetaData.get(var).get(2);

		String[] condColsArray = condCols.split(",");
		int noOfCondCols = condColsArray.length;

		StringBuffer updQuery = new StringBuffer("UPDATE " + tableName
				+ " SET " + colName + " = ");
		Object varValue = null;

		String returnType = (String) models.get(modelKey).get("return_type");

		if ((Boolean) models.get(modelKey).get("is_method")) {
			String classPath = (String) models.get(modelKey).get("class_path");
			String methodName = (String) models.get(modelKey)
					.get("method_name");

			Class classObject = Class.forName(classPath);
			Class[] paramTypes = new Class[1];

			paramTypes[0] = Integer.class;

			Method mthd = classObject.getMethod(methodName, paramTypes);
			varValue = mthd.invoke(classObject, currIter);

		} else {
			RestTemplate restTemplate = new RestTemplate();

			String apiPath = (String) models.get(modelKey).get("method_name");
			ModelResponseExtractor modelExtractor = new ModelResponseExtractor();

			Map<String, Object> urlParams = new HashMap<String, Object>();
			urlParams.put("simId", constVars.getSimId());
			urlParams.put("userId", constVars.getUserId());
			urlParams.put("iter", currIter);

			ModelValue modValResponse = restTemplate.execute(apiPath,
					HttpMethod.GET, null, modelExtractor, urlParams);
			
			returnType = modValResponse.getType();
			varValue = modValResponse.getValue();

		}

		if (returnType.equalsIgnoreCase("INT")) {
			updQuery.append((Integer) varValue);
		} else if (returnType.equalsIgnoreCase("DOUBLE")) {
			updQuery.append((Double) varValue);
		}

		updQuery.append(" WHERE ");

		for (int i = 0; i < noOfCondCols; i++) {

			if (condColsArray[i].equalsIgnoreCase("iteration_no")) {

				updQuery.append(condColsArray[i] + " = " + currIter + " AND ");

			} else if (condColsArray[i].equalsIgnoreCase("agent_id")
					|| condColsArray[i].equalsIgnoreCase("agent_id1")) {

				updQuery.append(condColsArray[i] + " = " + sourceAgent
						+ " AND ");

			} else if (condColsArray[i].equalsIgnoreCase("agent_id2")) {

				updQuery.append(condColsArray[i] + " = " + targetAgent
						+ " AND ");

			} else if (condColsArray[i].equalsIgnoreCase("event_id")
					|| condColsArray[i].equalsIgnoreCase("event_involved")
					|| condColsArray[i].equalsIgnoreCase("target_event")) {

				updQuery.append(condColsArray[i] + " = " + targetEvent
						+ " AND ");

			} else if (condColsArray[i].equalsIgnoreCase("emotions")
					|| condColsArray[i].equalsIgnoreCase("target_emotion")) {

				updQuery.append(condColsArray[i] + " = " + "'" + targetEmotion
						+ "'" + " AND ");

			} else if (condColsArray[i].equalsIgnoreCase("object_id")
					|| condColsArray[i].equalsIgnoreCase("target_object")) {

				updQuery.append(condColsArray[i] + " = " + targetObject
						+ " AND ");

			} else if (condColsArray[i].equalsIgnoreCase("user_id")) {

				updQuery.append(condColsArray[i] + " = " + "'"
						+ constVars.getUserId() + "'" + " AND ");

			} else if (condColsArray[i].equalsIgnoreCase("variable")) {

				updQuery.append(condColsArray[i] + " = " + "'" + targetVariable
						+ "'" + " AND ");

			} else if (condColsArray[i].equalsIgnoreCase("third_person")) {

				updQuery.append(condColsArray[i] + " = " + thirdPerson
						+ " AND ");

			}

		}

		int lastIndex = updQuery.length();

		updQuery.delete(lastIndex - 5, lastIndex);

		Statement updStmt = conn.createStatement();

		int noOfRowsUpdated = updStmt.executeUpdate(updQuery.toString());

		if (noOfRowsUpdated > 0) {
			System.out.println("Updated Table: " + tableName + ", Column: "
					+ colName + " for values: " + modelKey
					+ ". No of rows updated: " + noOfRowsUpdated);
		}

	}

	private void setInteractions() {
		try {

			PreparedStatement psQueryInteract = conn
					.prepareStatement(constVars.queryInteractModels());
			psQueryInteract.setLong(1, currIter);
			psQueryInteract.setInt(2, agentId);

			ResultSet rsQueryInteract = psQueryInteract.executeQuery();

			while (rsQueryInteract.next()) {
				String var = rsQueryInteract.getString("variable");

				if (isInteractVarModelExists(var)) {
					String interactKey = rsQueryInteract.getInt("iteration_no")
							+ "," + rsQueryInteract.getInt("agent_id1") + ","
							+ rsQueryInteract.getInt("agent_id2") + ","
							+ rsQueryInteract.getString("variable") + ","
							+ rsQueryInteract.getInt("third_person") + ","
							+ rsQueryInteract.getInt("target_event") + ","
							+ rsQueryInteract.getInt("target_object") + ","
							+ rsQueryInteract.getString("target_emotion");

					Integer precedence = rsQueryInteract.getInt("precedence");

					ArrayList<Object> interactVals = new ArrayList<Object>();
					interactVals.add(rsQueryInteract
							.getDouble("interaction_probability"));
					interactVals.add(rsQueryInteract.getDouble("influence"));
					interactVals.add(rsQueryInteract
							.getDouble("influence_filter"));
					interactVals.add(rsQueryInteract
							.getDouble("influence_likelihood"));
					interactVals.add(rsQueryInteract
							.getDouble("influence_threshold"));
					interactVals.add(precedence);
					interactVals.add(rsQueryInteract
							.getBoolean("is_influence_persists"));

					interactionPrecedences.put(interactKey, precedence);
					interactionAttitudes.put(interactKey, interactVals);

				}

			}

			rsQueryInteract.close();
			psQueryInteract.close();

			LinkedHashMap<String, Integer> sortedInteractionPrecedences = getInteractionsSortedByPrecedence();

			Iterator<String> sortedInteractionsKeys = sortedInteractionPrecedences
					.keySet().iterator();

			Random probGenerator = new Random();
			while (sortedInteractionsKeys.hasNext()) {
				String interactKey = sortedInteractionsKeys.next();

				Boolean isInfluencePersists = (Boolean) interactionAttitudes
						.get(interactKey).get(6);

				if (isInfluencePersists) {

					int interactRange = (int) (1 / (Double) interactionAttitudes
							.get(interactKey).get(0));

					boolean isProbable = (probGenerator.nextInt(interactRange) == (int) (interactRange / 2));

					if (isProbable) {
						int influenceRange = (int) (1 / (Double) interactionAttitudes
								.get(interactKey).get(6));
						boolean isInfluence = (probGenerator
								.nextInt(influenceRange) == (int) (influenceRange / 2));

						if (isInfluence) {
							double influence = (Double) interactionAttitudes
									.get(interactKey).get(1);
							double influenceFilter = (Double) interactionAttitudes
									.get(interactKey).get(2);
							double influenceThreshold = (Double) interactionAttitudes
									.get(interactKey).get(4);
							double influenceIntensity = influenceThreshold
									- (influence - influenceFilter);

							if (influenceIntensity > 0) {

								String modelKey = interactKey.split(",")[3]
										+ "," + interactKey.split(",")[4] + ","
										+ interactKey.split(",")[5] + ","
										+ interactKey.split(",")[6] + ","
										+ interactKey.split(",")[7] + ",,"// target
																			// variable
																			// should
																			// be
																			// null
																			// for
																			// interactions
										+ "1" // for interactions,
												// is_interaction is equal to
												// '1'
										+ interactKey.split(",")[2];

								setTableValues(modelKey);

							}
						}
					}
				}
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null && (!conn.isClosed())) {
					conn.close();
				}
			} catch (Exception e) {
				System.err.println("SQL connection cannot be closed!");
			}
		}
	}

	public LinkedHashMap<String, Integer> getInteractionsSortedByPrecedence() {
		List<Entry<String, Integer>> sortedList = new LinkedList<Entry<String, Integer>>(
				interactionPrecedences.entrySet());
		Collections.sort(sortedList, new Comparator<Entry<String, Integer>>() {
			public int compare(Entry<String, Integer> o1,
					Entry<String, Integer> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}

		});

		LinkedHashMap<String, Integer> sortedByPrecedence = new LinkedHashMap<String, Integer>();
		for (Entry<String, Integer> sortedEntries : sortedList) {
			sortedByPrecedence.put(sortedEntries.getKey(),
					sortedEntries.getValue());
		}
		return sortedByPrecedence;
	}

	private boolean isInteractVarModelExists(String interactVar) {

		boolean isExists = false;

		Iterator<String> iterModelKeys = models.keySet().iterator();

		while (iterModelKeys.hasNext()) {
			String modelKey = iterModelKeys.next();

			if (modelKey.split(",")[0].equalsIgnoreCase(interactVar)
					&& modelKey.split(",")[6].equalsIgnoreCase("1")) {
				isExists = true;
				break;
			}
		}

		return isExists;
	}
}
