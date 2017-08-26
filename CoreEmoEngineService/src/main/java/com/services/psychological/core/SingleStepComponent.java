package com.services.psychological.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.BeansException;
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
	private String currentInteractKey;

	private ArrayList<String> keyRowInserted = new ArrayList<String>();
	private HashMap<String, ArrayList<String>> varsToTableMetaData = new HashMap<String, ArrayList<String>>();
	private HashMap<String, ArrayList<Object>> interactionAttitudes = new HashMap<String, ArrayList<Object>>();
	
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

		for (VariableMapping varMap : varMappings) {
			varName = varMap.getVariableId();

			ArrayList<String> tmpTableMetaData = new ArrayList<String>();

			tmpTableMetaData.add(varMap.getTableName());
			tmpTableMetaData.add(varMap.getColName());
			tmpTableMetaData.add(varMap.getCondCols());
			tmpTableMetaData.add(varMap.getIsInteractionInvolved().toString());

			varsToTableMetaData.put(varName, tmpTableMetaData);
		}

		// TODO insertion, update, agent's emotion and interactions
		
		Iterator<String> iterModels = models.keySet().iterator();
		Map<Long, Long[]> eventObjectMapForIterations = new HashMap<Long, Long[]>();

		while (iterModels.hasNext()) {
			
			try {
				Long[] eventObjectArray = new Long[2];
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
			} catch (BeansException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// ***** Start interactions and set table values *****
		setInteractions();
		
		List<EventTab> events = eventDAO.getEventsForAgentBySimIdUserIdAndIter(constVars.getSimId(), constVars.getUserID(), currIter, agentId);
		//Invoking Event and Agent related emotions
		for (EventTab event : events) {
			individualAgent.setAgentID(agentId);
			individualAgent.setConstVars(constVars);
			individualAgent.setEventID(event.getEventId());
			individualAgent.setObjectID(-1l);
			System.out.println("AgentId --> " + agentId + ", TargetEvent --> " + targetEvent + ", TargetObject --> " + targetObject); 
			
			individualAgent.individualEmotionsInvocation();
		}
		
		List<ObjectTab> objects = objectDAO.getObjectsForAgentByIterUserAndSimId(currIter, agentId, constVars.getUserID(), constVars.getSimId());
		//Invoke object related emotions
		for (ObjectTab object : objects) {
			individualAgent.setAgentID(agentId);
			individualAgent.setConstVars(constVars);
			individualAgent.setEventID(-1l);
			individualAgent.setObjectID(object.getObjectId());
			System.out.println("AgentId --> " + agentId + ", TargetEvent --> " + targetEvent + ", TargetObject --> " + targetObject); 
			
			individualAgent.individualEmotionsInvocation();
		}

	}

	private void insertTableValues(String keyVal) throws BeansException, Exception {
		String var = keyVal.split(",")[0];
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
				agentNetworkDAO = (AgentNetworkDAO)context.getBean(AgentNetworkDAO.class);
				agentNetwork = agentNetworkDAO.getNetworkByIterAgentsEventUserIdAndSimId(currIter, sourceAgent, targetAgent, targetEvent, constVars.getUserId(), constVars.getSimId());
				if (null == agentNetwork) {
					agentNetwork = new AgentNetwork();
				}
				setEntityFieldsAndSave(agentNetwork, agentNetworkDAO,
						condColsArray, new AgentNetworkSpecs(agentNetwork),
						keyVal, colName);
			} else if (tableName.equals("agent_tab")) {
				agentDAO = (AgentTabDAO)context.getBean(AgentTabDAO.class);
				agent = agentDAO.getAgentByUserIdSimIdAgentIdAndIter(constVars.getUserID(), constVars.getSimId(), sourceAgent, currIter);
				if (null == agent) {
					agent = new AgentTab();	
				}
				setEntityFieldsAndSave(agent, agentDAO, condColsArray,
						new AgentTabSpecs(agent), keyVal, colName);
			} else if (tableName.equals("emo_attitudes")) {
				emoAttsDAO = (EmotionalAttitudesDAO)context.getBean(EmotionalAttitudesDAO.class);
				emoAtts = emoAttsDAO.getEmoAttByIterAgentEmotionsUserIdAndSimId(sourceAgent, constVars.getUserID(), currIter, constVars.getSimId(), targetEmotion);
				if (null == emoAtts) {
					emoAtts = new EmotionalAttitudes();	
					emoAtts.setOccurredCnt(0l);
					emoAtts.setOccurrenceCnt(0l); 
				}
				setEntityFieldsAndSave(emoAtts, emoAttsDAO, condColsArray,
						new EmotionalAttitudesSpecs(emoAtts), keyVal, colName);
			} else if (tableName.equals("event_tab")) {
				eventDAO = (EventTabDAO)context.getBean(EventTabDAO.class);
				event = eventDAO.getEventsBySimIdUserIdIterEventAndAgent(constVars.getSimId(), constVars.getUserID(), currIter, targetEvent, sourceAgent);
				if (null == event) {
					event = new EventTab();	
				}
				setEntityFieldsAndSave(event, eventDAO, condColsArray,
						new EventTabSpecs(event), keyVal, colName);
			} else if (tableName.equals("interaction_attitudes")) {
				interactAttsDAO = (InteractionAttitudesDAO)context.getBean(InteractionAttitudesDAO.class);
				interactAtts = interactAttsDAO.getUniqueInteractionAttitudes(currIter, sourceAgent, targetAgent, targetVariable, thirdPerson, 
						targetEvent, targetObject, var, constVars.getUserID(), constVars.getSimId());
				if (null == interactAtts) {
					interactAtts = new InteractionAttitudes();	
				}
				setEntityFieldsAndSave(interactAtts, interactAttsDAO,
						condColsArray, new InteractionAttitudesSpecs(
								interactAtts), keyVal, colName);
			} else if (tableName.equals("object_tab")) {
				objectDAO = (ObjectTabDAO)context.getBean(ObjectTabDAO.class);
				object = objectDAO.getObjectByIterObjectIdAgentUserAndSimId(currIter, targetObject, sourceAgent, constVars.getUserID(), constVars.getSimId());
				if (null == object) {
					object = new ObjectTab();	
				}
				setEntityFieldsAndSave(object, objectDAO, condColsArray,
						new ObjectTabSpecs(object), keyVal, colName);
			}

		}
	}

	private <T, V> void setEntityFieldsAndSave(T entity, V dao,
			String[] condColsArray, Specification<T> spec, String keyVal,
			String column) throws ClassNotFoundException,
			NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, InstantiationException {
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
		
		entityReflect.invokeSetterMethodByColumnName("simulation_id", entity, constVars.getSimId());

		List<T> existingList = entityReflect.getEntityBySpec(dao, entity, spec);
		if (existingList.isEmpty()) {
			entityReflect.invokeJpaSave(dao, entity);
		}

		
			Object varValue = null;
			
			if (!keyVal.split(",")[6].equalsIgnoreCase("1")) {
				
				String returnType = (String) models.get(keyVal).get("return_type");

				if ((Boolean) models.get(keyVal).get("is_function")) {
					String classPath = (String) models.get(keyVal)
							.get("class_path");
					String methodName = (String) models.get(keyVal).get(
							"method_name");

					Class classObject = Class.forName(classPath);
					Object classInstance = classObject.newInstance();

					Method mthd = classObject.getMethod(methodName, Long.class);
				    varValue = mthd.invoke(classInstance, currIter);
					

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
							(Long) varValue);
				} else if (returnType.equalsIgnoreCase("DOUBLE")) {
					entityReflect.invokeSetterMethodByColumnName(column, entity,
							(Double) varValue);
				}
			} else {
				varValue = entityReflect.invokeGetterMethodByColumnName(column, entity);
				String interactValue = (String)interactionAttitudes.get(currentInteractKey).get(7);
				
				try {
					Long interactValueLong = Long.parseLong(interactValue);
					varValue = (Long)varValue + interactValueLong;
					entityReflect.invokeSetterMethodByColumnName(column, entity, (Long) varValue);
				} catch (NumberFormatException nfe) {
					Double interactValueDouble = Double.parseDouble(interactValue);
					varValue = (Double)varValue + interactValueDouble;
					entityReflect.invokeSetterMethodByColumnName(column, entity, (Double) varValue);
				}
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
					interactVals.add(interactAtts.getInfluenceValue());

					
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
							Double influence = (Double) interactionAttitudes
									.get(interactKey).get(1);
							Double influenceFilter = (Double) interactionAttitudes
									.get(interactKey).get(2);
							Double influenceThreshold = (Double) interactionAttitudes
									.get(interactKey).get(4);
							Double influenceIntensity = influenceThreshold
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
								currentInteractKey = interactKey;

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
