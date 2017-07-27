package com.services.psychological.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.services.dao.AgentNetworkDAO;
import com.services.dao.AgentTabDAO;
import com.services.dao.EmotionalAttitudesDAO;
import com.services.dao.EventTabDAO;
import com.services.dao.InteractionAttitudesDAO;
import com.services.dao.ObjectTabDAO;
import com.services.dao.ResourceModelDAO;
import com.services.dao.VariableMappingDAO;
import com.services.emo.utils.EntityReflection;
import com.services.entities.AgentNetwork;
import com.services.entities.AgentTab;
import com.services.entities.EmotionalAttitudes;
import com.services.entities.EventTab;
import com.services.entities.InteractionAttitudes;
import com.services.entities.ObjectTab;
import com.services.entities.ResourceModel;
import com.services.entities.VariableMapping;
import com.services.specs.AgentNetworkSpecs;
import com.services.specs.AgentTabSpecs;
import com.services.specs.EmotionalAttitudesSpecs;
import com.services.specs.EventTabSpecs;
import com.services.specs.InteractionAttitudesSpecs;
import com.services.specs.ObjectTabSpecs;

/**
 * @author priyeshpandey
 *
 */
@Component
public class SingleStepComponent {

	HashMap<String, HashMap<String, Object>> models = new HashMap<String, HashMap<String, Object>>();

	private ModelVariables constVars;
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
	
	private int lastIndex;

	@Autowired
	ApplicationContext context;
	@Autowired
	IndividualAgentComponent individualAgent;

	// Entities

	private AgentNetwork agentNetwork;
	private AgentTab agent;
	private EmotionalAttitudes emoAtts;
	private EventTab event;
	private InteractionAttitudes interactAtts;
	private ObjectTab object;

	// DAOs

	private AgentNetworkDAO agentNetworkDAO;
	private AgentTabDAO agentDAO;
	private EmotionalAttitudesDAO emoAttsDAO;
	private EventTabDAO eventDAO;
	private InteractionAttitudesDAO interactAttsDAO;
	private ObjectTabDAO objectDAO;

	public SingleStepComponent() {

	}

	
	
	public ModelVariables getConstVars() {
		return constVars;
	}



	public void setConstVars(ModelVariables constVars) {
		this.constVars = constVars;
	}



	public Long getSimulationId() {
		return simulationId;
	}



	public void setSimulationId(Long simulationId) {
		this.simulationId = simulationId;
	}



	public String getUserID() {
		return userID;
	}



	public void setUserID(String userID) {
		this.userID = userID;
	}



	public Long getAgentId() {
		return agentId;
	}



	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}



	public Long getCurrIter() {
		return currIter;
	}



	public void setCurrIter(Long currIter) {
		this.currIter = currIter;
	}



	public Long getThirdPerson() {
		return thirdPerson;
	}



	public void setThirdPerson(Long thirdPerson) {
		this.thirdPerson = thirdPerson;
	}



	public void initModelsAndExecuteSingleStep() {

		ResourceModelDAO modelDAO = context.getBean(ResourceModelDAO.class);
		List<ResourceModel> modelsList = modelDAO.getNoIteractModelsBySimUserIdAgentId(
				constVars.getUserId(), constVars.getSimId(), agentId);

		if (!models.isEmpty()) {
			models.clear();
		}
		
		System.out.println("Model List size: " + modelsList.size());

		ArrayList<String> varsToModel = new ArrayList<String>();

		for (ResourceModel model : modelsList) {
			HashMap<String, Object> valueList = new HashMap<String, Object>();

			varsToModel.add(model.getVarToModel());

			String keyVal = model.getVarToModel() + ","
					+ model.getTargetAgent() + "," + model.getTargetEvent()
					+ "," + model.getTargetObject() + ","
					+ model.getTargetEmotion() + "," + model.getTargetVariable()
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

		VariableMappingDAO varMapDAO = context
				.getBean(VariableMappingDAO.class);
		List<VariableMapping> varMappings = new ArrayList<VariableMapping>();
		
		while (iterVars.hasNext()) {
			String var = iterVars.next();
			varMappings.add(varMapDAO.getMappingByVariableId(var));
		}

		
		System.out.println("Size of variable mappings --> " + varMappings.size()); 

		for (VariableMapping varMap : varMappings) {
			varName = varMap.getVariableId();
			System.out.println("Variable in table --> !" + varName + "|");

			ArrayList<String> tmpTableMetaData = new ArrayList<String>();

			tmpTableMetaData.add(varMap.getTableName());
			tmpTableMetaData.add(varMap.getColName());
			tmpTableMetaData.add(varMap.getCondCols());
			tmpTableMetaData.add(varMap.getIsInteractionInvolved().toString());

			varsToTableMetaData.put(varName, tmpTableMetaData);
		}

		// TODO insertion, update, agent's emotion and interactions
		
		Iterator<String> iterModels = models.keySet().iterator();

		while (iterModels.hasNext()) {
			
			try {
				String keyVal = iterModels.next();
				insertTableValues(keyVal);
				
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// ***** Individual Agent's Emotions *****

					//IndividualAgentComponent individualAgent = new IndividualAgentComponent(simulationId, agentId, targetEvent,
						//	targetObject, constVars).individualEmotionsInvocation();
		individualAgent.setAgentID(agentId);
		individualAgent.setConstVars(constVars);
		individualAgent.setEventID(targetEvent);
		individualAgent.setObjectID(targetObject);
		individualAgent.setSimulationId(simulationId);
		
		individualAgent.individualEmotionsInvocation();

		// ***** Start interactions and set table values *****

					setInteractions();

	}

	private void insertTableValues(String keyVal) throws SQLException,
			ClassNotFoundException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		String var = keyVal.split(",")[0];
		System.out.println("Variable --> |" + var + "|"); 
		System.out.println("Value of targetAgent --> " + keyVal.split(",")[1]); 
		targetAgent = keyVal.split(",")[1] != null &&  !keyVal.split(",")[1].equals("null") ? Long.parseLong(keyVal.split(",")[1]) : -1;
		targetEvent = keyVal.split(",")[2] != null && !keyVal.split(",")[2].equals("null") ? Long.parseLong(keyVal.split(",")[2]) : -1;
		targetObject = keyVal.split(",")[3] != null && !keyVal.split(",")[3].equals("null") ? Long.parseLong(keyVal.split(",")[3]) : -1;
		targetEmotion = keyVal.split(",")[4];
		targetVariable = keyVal.split(",")[5];
		sourceAgent = keyVal.split(",")[7] != null && !keyVal.split(",")[7].equals("null") ? Long.parseLong(keyVal.split(",")[7]) : -1;

		if (!keyRowInserted.contains(keyVal)) {
			String tableName = varsToTableMetaData.get(var).get(0);
			String colName = varsToTableMetaData.get(var).get(1);
			String condCols = varsToTableMetaData.get(var).get(2);

			String[] condColsArray = condCols.split(",");

			if (tableName.equals("agent_network")) {
				agentNetwork = new AgentNetwork();
				agentNetworkDAO = context.getBean(AgentNetworkDAO.class);
				setEntityFieldsAndSave(agentNetwork, agentNetworkDAO,
						condColsArray, new AgentNetworkSpecs(agentNetwork),
						keyVal, colName);
			} else if (tableName.equals("agent_tab")) {
				agent = new AgentTab();
				agentDAO = context.getBean(AgentTabDAO.class);
				setEntityFieldsAndSave(agent, agentDAO, condColsArray,
						new AgentTabSpecs(agent), keyVal, colName);
			} else if (tableName.equals("emo_attitudes")) {
				emoAtts = new EmotionalAttitudes();
				emoAttsDAO = context.getBean(EmotionalAttitudesDAO.class);
				setEntityFieldsAndSave(emoAtts, emoAttsDAO, condColsArray,
						new EmotionalAttitudesSpecs(emoAtts), keyVal, colName);
			} else if (tableName.equals("event_tab")) {
				event = new EventTab();
				eventDAO = context.getBean(EventTabDAO.class);
				setEntityFieldsAndSave(event, eventDAO, condColsArray,
						new EventTabSpecs(event), keyVal, colName);
			} else if (tableName.equals("interaction_attitudes")) {
				interactAtts = new InteractionAttitudes();
				interactAttsDAO = context
						.getBean(InteractionAttitudesDAO.class);
				setEntityFieldsAndSave(interactAtts, interactAttsDAO,
						condColsArray, new InteractionAttitudesSpecs(
								interactAtts), keyVal, colName);
			} else if (tableName.equals("object_tab")) {
				object = new ObjectTab();
				objectDAO = context.getBean(ObjectTabDAO.class);
				setEntityFieldsAndSave(object, objectDAO, condColsArray,
						new ObjectTabSpecs(object), keyVal, colName);
			}

		}
	}

	private <T, V> void setEntityFieldsAndSave(T entity, V dao,
			String[] condColsArray, Specification<T> spec, String keyVal,
			String column) throws ClassNotFoundException,
			NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		int noOfCondCols = condColsArray.length;

		EntityReflection entityReflect = new EntityReflection();

		for (int i = 0; i < noOfCondCols; i++) {

			if (condColsArray[i].equalsIgnoreCase("iteration_no")) {

				entityReflect.invokeSetterMethodByColumnName(condColsArray[i],
						entity, currIter);

			} else if (condColsArray[i].equalsIgnoreCase("agent_id")
					|| condColsArray[i].equalsIgnoreCase("agent_id1")) {

				entityReflect.invokeSetterMethodByColumnName(condColsArray[i],
						entity, sourceAgent);

			} else if (condColsArray[i].equalsIgnoreCase("agent_id2")) {

				entityReflect.invokeSetterMethodByColumnName(condColsArray[i],
						entity, targetAgent);

			} else if (condColsArray[i].equalsIgnoreCase("event_id")
					|| condColsArray[i].equalsIgnoreCase("event_involved")
					|| condColsArray[i].equalsIgnoreCase("target_event")) {

				entityReflect.invokeSetterMethodByColumnName(condColsArray[i],
						entity, targetEvent);

			} else if (condColsArray[i].equalsIgnoreCase("emotions")
					|| condColsArray[i].equalsIgnoreCase("target_emotion")) {

				entityReflect.invokeSetterMethodByColumnName(condColsArray[i],
						entity, targetEmotion);

			} else if (condColsArray[i].equalsIgnoreCase("object_id")
					|| condColsArray[i].equalsIgnoreCase("target_object")) {

				entityReflect.invokeSetterMethodByColumnName(condColsArray[i],
						entity, targetObject);

			} else if (condColsArray[i].equalsIgnoreCase("user_id")) {

				entityReflect.invokeSetterMethodByColumnName(condColsArray[i],
						entity, constVars.getUserId());

			} else if (condColsArray[i].equalsIgnoreCase("variable")) {

				entityReflect.invokeSetterMethodByColumnName(condColsArray[i],
						entity, targetVariable);

			} else if (condColsArray[i].equalsIgnoreCase("third_person")) {

				entityReflect.invokeSetterMethodByColumnName(condColsArray[i],
						entity, thirdPerson);

			}

		}

		List<T> existingList = entityReflect.getEntityBySpec(dao, entity, spec);
		if (existingList.isEmpty()) {
			entityReflect.invokeJpaSave(dao, entity);
		}

		
			Object varValue = null;

			String returnType = (String) models.get(keyVal).get("return_type");

			if ((Boolean) models.get(keyVal).get("is_method")) {
				String classPath = (String) models.get(keyVal)
						.get("class_path");
				String methodName = (String) models.get(keyVal).get(
						"method_name");

				Class classObject = Class.forName(classPath);
				Class[] paramTypes = new Class[1];

				paramTypes[0] = Integer.class;

				Method mthd = classObject.getMethod(methodName, paramTypes);
				varValue = mthd.invoke(classObject, currIter);

			} else {
				RestTemplate restTemplate = new RestTemplate();

				String apiPath = (String) models.get(keyVal).get("method_name");
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
				entityReflect.invokeSetterMethodByColumnName(column, entity,
						(Integer) varValue);
			} else if (returnType.equalsIgnoreCase("DOUBLE")) {
				entityReflect.invokeSetterMethodByColumnName(column, entity,
						(Double) varValue);
			}

			existingList = entityReflect.getEntityBySpec(dao, entity, spec);
			if (existingList.isEmpty()) {
				entityReflect.invokeJpaSave(dao, entity);
			}
		

	}
	
	
	private void setInteractions() {
		try {

			InteractionAttitudesDAO interactDAO = context.getBean(InteractionAttitudesDAO.class);
			List<InteractionAttitudes> interactList = interactDAO.getAttitudesByIterAgentUserAndSimIdPrecedenceSorted(
					currIter, agentId, constVars.getUserId(), constVars.getSimId());

			for (InteractionAttitudes interactAtts:interactList) {
				String var = interactAtts.getVariable();

				
					String interactKey = interactAtts.getIterNo()
							+ "," + interactAtts.getAgentId1() + ","
							+ interactAtts.getAgentId2() + ","
							+ interactAtts.getVariable() + ","
							+ interactAtts.getThirdPerson() + ","
							+ interactAtts.getTargetEvent() + ","
							+ interactAtts.getTargetObject() + ","
							+ interactAtts.getTargetEmotion();

					Long precedence = interactAtts.getPrecedence();

					ArrayList<Object> interactVals = new ArrayList<Object>();
					interactVals.add(interactAtts.getInteractProb());
					interactVals.add(interactAtts.getInfluence());
					interactVals.add(interactAtts.getInfluenceFilter());
					interactVals.add(interactAtts.getInfluenceLikelihood());
					interactVals.add(interactAtts.getInfluenceThreshold());
					interactVals.add(precedence);
					interactVals.add(interactAtts.getIsInfluencePersists());

					
					interactionAttitudes.put(interactKey, interactVals);

				}
			

			Iterator<String> sortedInteractionsKeys = interactionAttitudes
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

								insertTableValues(modelKey);

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
			
		}
	}
	
	

}
