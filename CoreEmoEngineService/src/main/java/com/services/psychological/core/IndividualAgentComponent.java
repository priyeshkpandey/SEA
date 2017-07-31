package com.services.psychological.core;

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
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.services.dao.AgentNetworkDAO;
import com.services.dao.AgentTabDAO;
import com.services.dao.EmotionalAttitudesDAO;
import com.services.dao.EventTabDAO;
import com.services.dao.ObjectTabDAO;
import com.services.dao.ObservedEmotionsDAO;
import com.services.dao.PreviousStateDAO;
import com.services.dao.SimulationDAO;
import com.services.entities.AgentNetwork;
import com.services.entities.AgentTab;
import com.services.entities.EmotionalAttitudes;
import com.services.entities.EventTab;
import com.services.entities.ObjectTab;
import com.services.entities.ObservedEmotions;
import com.services.entities.PreviousState;
import com.services.entities.Simulation;

@Component
public class IndividualAgentComponent {

	private Long agentID; // used but not assigned a value
	private Long eventID; // used but not assigned a value
	private Long objectID; // used but not assigned a value
	private boolean isAGoal;
	private boolean isIGoal;
	private boolean isRGoal;
	private boolean isEvent;
	private boolean isAgent;
	private boolean isObject;
	private int action;
	private double AGoalValue;
	private double IGoalValue;
	private double RGoalValue;
	private double agentGlobalVars;
	private double eventGlobalVars;
	private double objectGlobalVars;
	private double desireSelf;
	private double likelihood;
	private double prevHopePotential;
	private double prevFearPotential;
	private double effort;
	private double realization;
	private double praiseworthiness;
	private double appealingness;
	private double familiarity;
	private Double desireOther;
	private Double deservingness;
	private Double likingOther;
	private Double agentBelief;
	Double expDiff;
	Double cogUnit;
	private HashMap<Long, HashMap<String, Object>> neighbours = new HashMap<Long, HashMap<String, Object>>();
	private Long simulationId;
	private Long proposedIter;
	private Long workingIter;
	private Long maxIter;
	private Long currIter;
	private Long noOfEmosToInvoke;
	private Long tempWorkingCount = 0l;
	private ArrayList<String> triggeredEmos;
	private HashMap<String, Long> triggeredEmosPrecedence = new HashMap<String, Long>();
	private HashMap<String, HashMap<String, Object>> emoAttitudes = new HashMap<String, HashMap<String, Object>>();
	private TreeMap<Long, String> sortedEmoOccurCount = new TreeMap<Long, String>();
	private SimulationDAO simulationDAO;
	private Simulation simulation;
	private EmotionalAttitudesDAO emotionalAttitudesDAO;
	private List<EmotionalAttitudes> emotionalAttitudes;
	private Set<String> emoAttKeySet;
	private AgentTabDAO agentTabDAO;
	private AgentTab agentTab;
	private EventTabDAO eventTabDAO;
	private EventTab event;
	private ObjectTabDAO objectDAO;
	private ObjectTab object;
	private ObservedEmotionsDAO observedEmosDAO;
	private PreviousStateDAO prevStateDAO;
	private List<PreviousState> prevStates;
	
	Long emosOccur = 0l;
	Long calcOccur = 0l;
	Long invokedEmosCount = 0l;

	private ModelVariables constVars;

	@Autowired
	@Qualifier("psycheConfig")
	private Properties psycheConfig;

	@Autowired
	ApplicationContext context;

	public IndividualAgentComponent() {
		isAgent = false;
		isEvent = false;
		isObject = false;
	}
	
	public Long getAgentID() {
		return agentID;
	}

	public void setAgentID(Long agentID) {
		this.agentID = agentID;
	}

	public Long getEventID() {
		return eventID;
	}

	public void setEventID(Long eventID) {
		this.eventID = eventID;
	}

	public Long getObjectID() {
		return objectID;
	}

	public void setObjectID(Long objectID) {
		this.objectID = objectID;
	}

	public Long getSimulationId() {
		return simulationId;
	}

	public void setSimulationId(Long simulationId) {
		this.simulationId = simulationId;
	}



	public ModelVariables getConstVars() {
		return constVars;
	}

	public void setConstVars(ModelVariables constVars) {
		this.constVars = constVars;
	}
	
	private ArrayList<String> getTriggeredEmos() {
		return triggeredEmos;
	}
	
	private Double getOthersDesirability() {
		return desireOther;
	}
	
	private Double getDeservingness() {
		return deservingness;
	}
	
	private Double getOthersLiking() {
		return likingOther;
	}
	
	private Boolean isNeighbourEmotionTarget(Long neighbour) {
		return (Boolean) neighbours.get(neighbour).get("emotion_target");
	}

	public void individualEmotionsInvocation()
	{
		
		agentPerception();
		
		ArrayList<String> trigEmos = getTriggeredEmos();
		
		Iterator<String> emoIter = trigEmos.iterator();
		
		while(emoIter.hasNext())
		{
			System.out.println("Invoke Emotion: "+emoIter.next());
		}
		
		collectDataAndInvoke();
		
		
	}


	public void agentPerception() {
		triggeredEmos = new ArrayList<String>();
		initializeSimulation();
		setCurrIter();
		setIterationValues();
		executeOneIteration();

	}
	
	private void initializeSimulation() {
		simulationDAO = context.getBean(SimulationDAO.class);
		simulation = simulationDAO.getSimulationByUserAndSimId(constVars.getUserId(), constVars.getSimId());
	}
	
	private void setCurrIter() {
		currIter = simulation.getCurrIter();
	}
	
	private void setIterationValues() {
		maxIter = simulation.getMaxIter();
		proposedIter = simulation.getProposedIter();
		workingIter = simulation.getWorkingIter();
	}
	
	private void executeOneIteration() {
		if (currIter <= workingIter) {
			initializeEmotionalAttitudes();
			setAllEmotionalAttitudes();
			emoAttKeySet = emoAttitudes.keySet();
			initializeAgentTab();
			setNoOfEmosToInvoke(emoAttKeySet.size());
			setPerceptionAttributes();
			updateInvokedCountsForEmotions();
			updateWorkingCountForSimulation();
			setSimulationAttributes();
			saveSimulation();
		} else {
			System.out.println("Simulation completes.");
		}	
	}
	
	private void initializeEmotionalAttitudes() {
		emotionalAttitudesDAO = context
				.getBean(EmotionalAttitudesDAO.class);
		emotionalAttitudes = emotionalAttitudesDAO
				.getEmoAttByAgentIdUserIdIterAndSimId(agentID,
						constVars.getUserId(), currIter,
						constVars.getSimId());
	}
	
	private void setAllEmotionalAttitudes() {
		for (EmotionalAttitudes emoAttSingle : emotionalAttitudes) {
			String emo = emoAttSingle.getEmotions();
			HashMap<String, Object> emoAtt = new HashMap<String, Object>();
			emoAtt.put("probability", emoAttSingle.getProbability());
			emoAtt.put("precedence", emoAttSingle.getPrecedence());
			emoAtt.put("threshold", emoAttSingle.getThreshold());
			emoAtt.put("weight", emoAttSingle.getWeight());
			emoAtt.put("priority", emoAttSingle.getPriority());
			emoAtt.put("emotion_factor", emoAttSingle.getEmoFactor());
			emoAtt.put("occurrence_count", emoAttSingle.getOccurrenceCnt());
			emoAtt.put("occurred_count", emoAttSingle.getOccurredCnt());
			emoAttitudes.put(emo, emoAtt);
		}
	}
	
	private void initializeAgentTab() {
		agentTabDAO = context.getBean(AgentTabDAO.class);
		agentTab = agentTabDAO.getAgentByUserIdSimIdAgentIdAndIter(
				constVars.getUserId(), constVars.getSimId(), agentID,
				currIter);
	}
	
	private void setNoOfEmosToInvoke(int noOfEmos) {
		noOfEmosToInvoke = agentTab.getNumOfEmosToInvoke();
		if (noOfEmosToInvoke == null) {
			noOfEmosToInvoke = Math.round(noOfEmos * Math.random());
		}
	}
	
	private void setPerceptionAttributes() {
		Iterator<String> emoIter = emoAttKeySet.iterator();
		while (emoIter.hasNext()) {
			String emotion = emoIter.next();
			if (psycheConfig.getProperty("EVENT_EMOTIONS").contains(
					emotion.trim().toLowerCase())) {
				isEvent = true;
			}

			if (psycheConfig.getProperty("AGENT_EMOTIONS").contains(
					emotion.trim().toLowerCase())) {
				isAgent = true;
			}

			if (psycheConfig.getProperty("OBJECT_EMOTIONS").contains(
					emotion.trim().toLowerCase())) {
				isObject = true;
			}

			if (psycheConfig.getProperty("EVENT_AGENT_EMOTIONS").contains(
					emotion.trim().toLowerCase())) {
				isEvent = true;
				isAgent = true;
			}
			
			setEmotionsOccurrenceCount(emotion);
		}	
	}
	
	private void setEmotionsOccurrenceCount(String emotion) {
		Double emoProb = (Double) emoAttitudes.get(emotion).get(
				"probability");
		Long emoCount = (Long) Math.round(workingIter * emoProb);
		tempWorkingCount = 0l;
		tempWorkingCount += emoCount;
		emoAttitudes.get(emotion).put("occurrence_count", emoCount);

		if (sortedEmoOccurCount.containsKey(emoCount)) {
			String prevEmo = sortedEmoOccurCount.get(emoCount);
			sortedEmoOccurCount.put(emoCount, prevEmo + "," + emotion);
		} else {
			sortedEmoOccurCount.put(emoCount, emotion);
		}
	}
	
	private void updateInvokedCountsForEmotions() {
		Iterator<Long> sortedEmoIter = sortedEmoOccurCount
				.descendingKeySet().iterator();

		while (sortedEmoIter.hasNext()) {
			Long emoCnt = sortedEmoIter.next();
			String emotions = sortedEmoOccurCount.get(emoCnt);
			if (invokedEmosCount < noOfEmosToInvoke) {
				if (emotions.contains(",")) {
					int incCount = emotions.split(",").length;
					if (incCount > (noOfEmosToInvoke - invokedEmosCount - 1)) {
						updateEmotionCountsForMultipleAtSameOccurrence(emotions, noOfEmosToInvoke - invokedEmosCount);
						break;
					} else {
						updateEmotionCountsForMultipleAtSameOccurrence(emotions, (long)incCount);
						invokedEmosCount += (incCount - 1);
					}
				} else {
					updateEmotionCounts(emotions);
					invokedEmosCount++;
				}
			} else {
				if (emotions.contains(",")) {
					int noCombEmos = emotions.split(",").length;

					for (int i = 0; i < noCombEmos; i++) {
						String tmpEmo = emotions.split(",")[i];
						updateEndingCounts(tmpEmo);
					}

				} else {
					updateEndingCounts(emotions);
				}
			}
		}
	}
	
	private void updateEndingCounts(String emotion) {
		calcOccur = getLastOccurrenceCountForEmotion(emotion);
		emosOccur = getLastOccurredCountForEmotion(emotion);
		updateEmosOccurrenceCount(emotion, calcOccur - emosOccur);
	}
	
	private void updateEmosOccurrenceCount(String emotion, Long count) {
		addEmotionToTriggeredEmotions(emotion);
		emoAttitudes.get(emotion).put(
				"occurrence_count", (count));
	}
	
	private void updateEmotionCountsForMultipleAtSameOccurrence(String emotions, Long loopCounter) {
		for (int i = 0; i < loopCounter; i++) {
			String tempEmo = emotions.split(",")[i];
			updateEmotionCounts(tempEmo);
		}
    }
	
	private void updateEmotionCounts(String emotion) {
		emosOccur++;
		calcOccur = getLastOccurrenceCountForEmotion(emotion);
		emosOccur += getLastOccurredCountForEmotion(emotion);
		updateOccurredCountForEmotion(emotion, emosOccur);

		if (calcOccur >= emosOccur) {
			updateEmosOccurrenceCount(emotion, calcOccur - emosOccur);
			updateTriggeredEmotionsPrecedence(emotion);
		} else {
			updateEmosOccurrenceCount(emotion, 0l);
		}
		calcOccur = 0l;
		emotion = null;
		emosOccur = 0l;
	}
	
	private Long getLastOccurrenceCountForEmotion(String emotion) {
		return (Long)emoAttitudes.get(emotion)
				.get("occurrence_count");
	}
	
	private Long getLastOccurredCountForEmotion(String emotion) {
		return (Long) emoAttitudes
				.get(emotion).get("occurred_count");
	}
	
	private void updateOccurredCountForEmotion(String emotion, Long count) {
		emoAttitudes.get(emotion).put("occurred_count",
				count);
	}
	
	private void updateTriggeredEmotionsPrecedence(String emotion) {
		triggeredEmosPrecedence.put(emotion,
				(Long) emoAttitudes.get(emotion)
						.get("precedence"));
	}
	
	private void addEmotionToTriggeredEmotions(String emotion) {
		triggeredEmos.add(emotion);
	}
	
	private void updateWorkingCountForSimulation() {
		System.out.println("Temp count --> " + tempWorkingCount);
		System.out.println("Max iterations --> " + maxIter); 
		if (tempWorkingCount < maxIter) {
			workingIter = tempWorkingCount;
		} else {
			workingIter = maxIter;
		}
	}
	
	private void setSimulationAttributes() {
		simulation.setCurrIter(++currIter); 
		simulation.setWorkingIter(workingIter);
	}
	
	private void saveSimulation() {
		simulationDAO.saveAndFlush(simulation);
	}
	
	
	
	public void collectDataAndInvoke() {
		populateAgentNeighboursAttribute();
		initializeEvents();
		initializeObservedEmosDAO();
		initializePreviousStateDAO();
		setGoalValues();
		invokeEmotions();
	}
	
	private void populateAgentNeighboursAttribute() {
		AgentNetworkDAO agentDAO = context.getBean(AgentNetworkDAO.class);
		List<AgentNetwork> neighboursList = agentDAO.getAgentNeighbours(
				constVars.getSimId(), constVars.getUserId(), currIter, agentID,
				eventID);
		for (AgentNetwork neighbour : neighboursList) {
			HashMap<String, Object> agentNeighbours = new HashMap<String, Object>();
			Long nAgentID = neighbour.getAgentId2();
			agentNeighbours.put("event_involved", neighbour.getEventInvolved());
			agentNeighbours.put("other_desirability",
					neighbour.getOtherDesirability());
			agentNeighbours.put("deservingness", neighbour.getDeservingness());
			agentNeighbours.put("cognitive_unit_strength",
					neighbour.getCogUnitStrength());
			agentNeighbours
					.put("expectation_deviation", neighbour.getExpDiff());
			agentNeighbours.put("other_liking", neighbour.getOtherLiking());
			agentNeighbours.put("praiseworthiness",
					neighbour.getPraiseworthiness());
			agentNeighbours.put("agent_sense_of_reality",
					neighbour.getAgentSenseOfReality());
			agentNeighbours.put("agent_psycho_proximity",
					neighbour.getAgentPsychoProximity());
			agentNeighbours.put("agent_unexpectedness",
					neighbour.getAgentUnexpect());
			agentNeighbours.put("agent_arousal", neighbour.getAgentArousal());
			agentNeighbours.put("emotion_target", neighbour.getEmoTarget());
			neighbours.put(nAgentID, agentNeighbours);
		}
	}
	
	private void initializeEvents() {
		if (eventID != -1) {
			eventTabDAO = context.getBean(EventTabDAO.class);
			event = eventTabDAO
					.getEventsBySimIdUserIdIterEventAndAgent(constVars.getSimId(),
							constVars.getUserId(), currIter, eventID, agentID);
			System.out.println("Retrieved event id --> " + event.getEventId()); 
		}
	}
	
	private void initializeObservedEmosDAO() {
		observedEmosDAO = context
				.getBean(ObservedEmotionsDAO.class);
	}
	
	private void initializePreviousStateDAO() {
		prevStateDAO = context.getBean(PreviousStateDAO.class);
	}
	
	private void setGoalValues() {
		System.out.println("Agent Id --> " + agentTab.getAgentId()); 
		AGoalValue = agentTab.getaGoalValue();
		IGoalValue = agentTab.getiGoalValue();
		RGoalValue = agentTab.getrGoalValue();
	}
	
	private void invokeEmotions() {
		LinkedHashMap<String, Long> trigEmosByPrecedence = getEmosSortedByPrecedence();
		Iterator<String> iterEmosByPrecedence = trigEmosByPrecedence.keySet()
				.iterator();
		
		while (iterEmosByPrecedence.hasNext()) {
			String emoToBeInvoked = iterEmosByPrecedence.next();

			if ((eventID != -1) && (emoToBeInvoked.equalsIgnoreCase("joy")
					|| emoToBeInvoked.equalsIgnoreCase("distress"))) {
				invokeDesirabilityEmos(emoToBeInvoked);
			} else if ((eventID != -1) && (emoToBeInvoked.equalsIgnoreCase("happy-for")
					|| emoToBeInvoked.equalsIgnoreCase("sorry-for")
					|| emoToBeInvoked.equalsIgnoreCase("resentment")
					|| emoToBeInvoked.equalsIgnoreCase("gloating"))) {
				invokeFortunesOfOthersEmos(emoToBeInvoked);
			} else if ((eventID != -1) && (emoToBeInvoked.equalsIgnoreCase("hope")
					|| emoToBeInvoked.equalsIgnoreCase("fear"))) {
				invokeProspectEmos(emoToBeInvoked);
			} else if ((eventID != -1) && (currIter > 1)
					&& (emoToBeInvoked.equalsIgnoreCase("satisfaction")
							|| emoToBeInvoked
									.equalsIgnoreCase("fears-confirmed")
							|| emoToBeInvoked.equalsIgnoreCase("relief") || emoToBeInvoked
								.equalsIgnoreCase("disappointment"))) {
				invokeProspectConfirmationEmos(emoToBeInvoked);
			} else if (emoToBeInvoked.equalsIgnoreCase("pride")
					|| emoToBeInvoked.equalsIgnoreCase("self-reproach")) {
				invokeAgentCogUnitEmos(emoToBeInvoked);
			} else if (emoToBeInvoked.equalsIgnoreCase("appreciation")
					|| emoToBeInvoked.equalsIgnoreCase("reproach")) {
				invokeAgentNoCogUnitEmos(emoToBeInvoked);
			} else if ((eventID != -1) && (emoToBeInvoked.equalsIgnoreCase("gratitude")
					|| emoToBeInvoked.equalsIgnoreCase("anger"))) {
				invokeEventAgentNoCogUnitEmos(emoToBeInvoked);
			} else if ((eventID != -1) && (emoToBeInvoked.equalsIgnoreCase("gratification")
					|| emoToBeInvoked.equalsIgnoreCase("remorse"))) {
				invokeEventAgentCogUnitEmos(emoToBeInvoked);
			} else if ((objectID != -1) && (emoToBeInvoked.equalsIgnoreCase("liking")
					|| emoToBeInvoked.equalsIgnoreCase("disliking"))) {
				invokeObjectEmos(emoToBeInvoked);
			} else {
				System.err.println("Not a valid emotion for this iteration.");
			}

		}
	}
	
	private LinkedHashMap<String, Long> getEmosSortedByPrecedence() {
		List<Entry<String, Long>> sortedList = new LinkedList<Entry<String, Long>>(
				triggeredEmosPrecedence.entrySet());
		Collections.sort(sortedList, new Comparator<Entry<String, Long>>() {
			public int compare(Entry<String, Long> o1,
					Entry<String, Long> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}

		});

		LinkedHashMap<String, Long> sortedByPrecedence = new LinkedHashMap<String, Long>();
		for (Entry<String, Long> sortedEntries : sortedList) {
			sortedByPrecedence.put(sortedEntries.getKey(),
					sortedEntries.getValue());
		}
		return sortedByPrecedence;
	}
	
	
	
	
	
	
	
	

	private void invokeDesirabilityEmos(String emoToBeInvoked) {
		calculateEventDerivedAttributes();
		HashMap<String, Object> observedEmosValues = new HashMap<String, Object>();
		Double potential = 0.0;
		if (emoToBeInvoked.equalsIgnoreCase("joy") && (desireSelf > 0)) {
			potential = EmotionFunctions.calcJoyPotential(getEmotionFactor(emoToBeInvoked),
					Math.abs(desireSelf), eventGlobalVars);
		} else if (emoToBeInvoked.equalsIgnoreCase("distress")
				&& (desireSelf < 0)) {
			potential = EmotionFunctions.calcDistressPotential(
					getEmotionFactor(emoToBeInvoked), Math.abs(desireSelf), eventGlobalVars);	
		}	
		Double intensity = potential - getEmotionThreshold(emoToBeInvoked);
		observedEmosValues.put("emoToBeInvoked", emoToBeInvoked);
		observedEmosValues.put("potential", potential);
		observedEmosValues.put("intensity", intensity);
		observedEmosValues.put("targetEvent", eventID);
		saveObservedEmotion(observedEmosValues);
	}
	
	private void setAttributeValuesForNeighbour(Long neighbour) {
		desireOther = (Double) neighbours.get(neighbour)
				.get("other_desirability");
		deservingness = (Double) neighbours.get(neighbour)
				.get("deservingness");
		likingOther = (Double) neighbours.get(neighbour)
				.get("other_liking");
	}

	private void invokeFortunesOfOthersEmos(String emoToBeInvoked) {
		calculateEventDerivedAttributes();
		Double emoFactor = getEmotionFactor(emoToBeInvoked);
		Iterator<Long> iterNeighbours = neighbours.keySet().iterator();
		while (iterNeighbours.hasNext()) {
			Long agentNeighbour = iterNeighbours.next();
			if (isNeighbourEmotionTarget(agentNeighbour)) {
				setAttributeValuesForNeighbour(agentNeighbour);
				Double potential = 0.0;
				if (emoToBeInvoked.equalsIgnoreCase("happy-for")
						&& (desireSelf > 0) && (getOthersDesirability() > 0)) {
					potential = EmotionFunctions.calcHappyForPotential(emoFactor, Math.abs(desireSelf), Math.abs(getOthersDesirability()), getDeservingness(), getOthersLiking(), eventGlobalVars);	
				} else if (emoToBeInvoked.equalsIgnoreCase("sorry-for")
						&& (desireSelf < 0) && (getOthersDesirability() < 0)) {
					potential = EmotionFunctions.calcSorryForPotential(emoFactor, Math.abs(desireSelf), Math.abs(getOthersDesirability()), getDeservingness(), getOthersLiking(), eventGlobalVars);
				} else if (emoToBeInvoked.equalsIgnoreCase("resentment")
						&& (desireSelf < 0) && (getOthersDesirability() > 0)) {
					potential = EmotionFunctions.calcResentmentPotential(emoFactor, Math.abs(desireSelf), Math.abs(getOthersDesirability()), getDeservingness(), getOthersLiking(), eventGlobalVars);
				} else if (emoToBeInvoked.equalsIgnoreCase("gloating")
						&& (desireSelf > 0) && (getOthersDesirability() < 0)) {
					potential = EmotionFunctions.calcGloatingPotential(emoFactor, Math.abs(desireSelf), Math.abs(getOthersDesirability()), getDeservingness(), getOthersLiking(), eventGlobalVars);
				}
				Double intensity = potential - getEmotionThreshold(emoToBeInvoked);	
				HashMap<String, Object> observedEmosValues = new HashMap<String, Object>();
				observedEmosValues.put("emoToBeInvoked", emoToBeInvoked);
				observedEmosValues.put("potential", potential);
				observedEmosValues.put("intensity", intensity);
				observedEmosValues.put("targetEvent", eventID);
				saveObservedEmotion(observedEmosValues);
			}
		}
	}

	private void invokeProspectEmos(String emoToBeInvoked) {
		calculateEventDerivedAttributes();
		Double emoFactor = getEmotionFactor(emoToBeInvoked);
		HashMap<String, Object> observedEmosValues = new HashMap<String, Object>();
		HashMap<String, Object> previousStateValues = new HashMap<String, Object>();
		Double potential = 0.0;
		Double intensity = 0.0;		
		if (emoToBeInvoked.equalsIgnoreCase("hope") && (desireSelf > 0)
				&& (likelihood > 0)) {
			potential = EmotionFunctions.calcHopePotential(emoFactor, Math.abs(desireSelf), likelihood, eventGlobalVars);
			previousStateValues.put("hopePotential", potential);
		} else if(emoToBeInvoked.equalsIgnoreCase("fear")
				&& (desireSelf < 0)
				&& (likelihood > 0)) {
			potential = EmotionFunctions.calcFearPotential(emoFactor, Math.abs(desireSelf), likelihood, eventGlobalVars);
			previousStateValues.put("fearPotential", potential);
		}
		
		intensity = potential - getEmotionThreshold(emoToBeInvoked);
		observedEmosValues.put("emoToBeInvoked", emoToBeInvoked);
		observedEmosValues.put("potential", potential);
		observedEmosValues.put("intensity", intensity);
		observedEmosValues.put("targetEvent", eventID);
		saveObservedEmotion(observedEmosValues);
		previousStateValues.put("emoToBeInvoked", emoToBeInvoked);
		savePreviousState(previousStateValues);
	}
	
	private void invokeProspectConfirmationEmos(String emoToBeInvoked) {
		HashMap<String, Object> observedEmosValues = new HashMap<String, Object>();
		HashMap<String, Object> previousStateValues = new HashMap<String, Object>();
		calculateEventDerivedAttributes();
		setPreviousHopeAndFearPotentialByEmotion(emoToBeInvoked);
		Double emoFactor = (Double)emoAttitudes.get(emoToBeInvoked).get("emotion_factor");
		Double potential = 0.0;
		Double intensity = 0.0;
		
		if(emoToBeInvoked.equalsIgnoreCase("satisfaction")
				&& (getNumberOfPreviousStateRows() > 0)
				&& (desireSelf > 0)
				&& (agentBelief > 0))
		{
			potential = EmotionFunctions.calcSatisfactionPotential(emoFactor, prevHopePotential, effort, realization, eventGlobalVars);
		}
		else if(emoToBeInvoked.equalsIgnoreCase("fears-confirmed")
				&& (getNumberOfPreviousStateRows() > 0)
				&& (desireSelf < 0)
				&& (agentBelief > 0))
		{
			potential = EmotionFunctions.calcFearsConfPotential(emoFactor, prevFearPotential, effort, realization, eventGlobalVars);
		}
		else if(emoToBeInvoked.equalsIgnoreCase("relief")
				&& (getNumberOfPreviousStateRows() > 0)
				&& (desireSelf < 0)
				&& (agentBelief < 0))
		{
			potential = EmotionFunctions.calcReliefPotential(emoFactor, prevHopePotential, effort, realization, eventGlobalVars);
		}
		else if(emoToBeInvoked.equalsIgnoreCase("disappointment")
				&& (getNumberOfPreviousStateRows() > 0)
				&& (desireSelf > 0)
				&& (agentBelief < 0))
		{
			potential = EmotionFunctions.calcDisappointmentPotential(emoFactor, prevHopePotential, effort, realization, eventGlobalVars);
		}
		
		intensity = potential - getEmotionThreshold(emoToBeInvoked);
		observedEmosValues.put("emoToBeInvoked", emoToBeInvoked);
		observedEmosValues.put("potential", potential);
		observedEmosValues.put("intensity", intensity);
		observedEmosValues.put("targetEvent", eventID);
		saveObservedEmotion(observedEmosValues);
		
		previousStateValues.put("hopePotential", 0.0);
		previousStateValues.put("fearPotential", 0.0);
		previousStateValues.put("emoToBeInvoked", emoToBeInvoked);
		savePreviousState(previousStateValues);

	}
	
	private void invokeAgentCogUnitEmos(String emoToBeInvoked)
	{
		HashMap<String, Object> observedEmosValues = new HashMap<String, Object>();
		Double emoFactor = (Double)emoAttitudes.get(emoToBeInvoked).get("emotion_factor");
		
		Iterator<Long> iterNeighbours = neighbours.keySet().iterator();
		
		while(iterNeighbours.hasNext()) {
			Long agentNeighbour = iterNeighbours.next();
			Double potential = 0.0;
			Double intensity = 0.0;
			
			if((Boolean)neighbours.get(agentNeighbour).get("emotion_target")) {
				calculateAgentBasedAttributes(agentNeighbour);
				
				if(emoToBeInvoked.equalsIgnoreCase("pride")
						&& (praiseworthiness > 0)
						&& (cogUnit > 0)) {
					potential = (Double)EmotionFunctions.calcPridePotential(emoFactor, Math.abs(praiseworthiness), expDiff, cogUnit, agentGlobalVars);
				} else if(emoToBeInvoked.equalsIgnoreCase("self-reproach")
						&& (praiseworthiness < 0)
						&& (cogUnit > 0)) {
					potential = (Double)EmotionFunctions.calcSelfReproachPotential(emoFactor, Math.abs(praiseworthiness), expDiff, cogUnit, agentGlobalVars);
				}
				
				intensity = potential - getEmotionThreshold(emoToBeInvoked);
				observedEmosValues.put("neighbour", agentNeighbour);
				observedEmosValues.put("emoToBeInvoked", emoToBeInvoked);
				observedEmosValues.put("potential", potential);
				observedEmosValues.put("intensity", intensity);
				saveObservedEmotion(observedEmosValues);
			}
					
		}
	}
	
	private void invokeAgentNoCogUnitEmos(String emoToBeInvoked)
	{
		HashMap<String, Object> observedEmosValues = new HashMap<String, Object>();
		Double emoFactor = (Double)emoAttitudes.get(emoToBeInvoked).get("emotion_factor");
		
		Iterator<Long> iterNeighbours = neighbours.keySet().iterator();
		
		while(iterNeighbours.hasNext()) {
			Long agentNeighbour = iterNeighbours.next();
			
			if((Boolean)neighbours.get(agentNeighbour).get("emotion_target")) {
				calculateAgentBasedAttributes(agentNeighbour);
				Double potential = 0.0;
				Double intensity = 0.0;
				
				if(emoToBeInvoked.equalsIgnoreCase("appreciation")
						&& (praiseworthiness > 0)) {
					potential = (Double)EmotionFunctions.calcAppreciationPotential(emoFactor, Math.abs(praiseworthiness), expDiff, agentGlobalVars);
				}
				else if(emoToBeInvoked.equalsIgnoreCase("reproach")
						&& (praiseworthiness < 0)) {
					potential = (Double)EmotionFunctions.calcReproachPotential(emoFactor, Math.abs(praiseworthiness), expDiff, agentGlobalVars);
				}
				
				intensity = potential - getEmotionThreshold(emoToBeInvoked);
				observedEmosValues.put("neighbour", agentNeighbour);
				observedEmosValues.put("emoToBeInvoked", emoToBeInvoked);
				observedEmosValues.put("potential", potential);
				observedEmosValues.put("intensity", intensity);
				saveObservedEmotion(observedEmosValues);
				
			}
					
		}
				
	}
	
	private void invokeEventAgentNoCogUnitEmos(String emoToBeInvoked)
	{
		setGoalValues();
		calculateEventDerivedAttributes();
		
		HashMap<String, Object> observedEmosValues = new HashMap<String, Object>();
		Double emoFactor = (Double)emoAttitudes.get(emoToBeInvoked).get("emotion_factor");
		
		Iterator<Long> iterNeighbours = neighbours.keySet().iterator();
		
		while(iterNeighbours.hasNext()) {
			Long agentNeighbour = iterNeighbours.next();
			
			if((Boolean)neighbours.get(agentNeighbour).get("emotion_target")) {
				calculateAgentBasedAttributes(agentNeighbour);
				Double potential = 0.0;
				Double intensity = 0.0;
				
				if(emoToBeInvoked.equalsIgnoreCase("gratitude")
						&& (desireSelf > 0)
						&& (praiseworthiness > 0)) {
					potential = (Double)EmotionFunctions.calcGratitudePotential(emoFactor, Math.abs(praiseworthiness), Math.abs(desireSelf), 
							expDiff, agentGlobalVars, eventGlobalVars);
				}
				else if(emoToBeInvoked.equalsIgnoreCase("anger")
						&& (desireSelf < 0)
						&& (praiseworthiness < 0)) {
					potential = (Double)EmotionFunctions.calcAngerPotential(emoFactor, Math.abs(praiseworthiness), Math.abs(desireSelf), 
							expDiff, agentGlobalVars, eventGlobalVars);
				}
				
				intensity = potential - getEmotionThreshold(emoToBeInvoked);
				observedEmosValues.put("targetEvent", eventID);
				observedEmosValues.put("neighbour", agentNeighbour);
				observedEmosValues.put("emoToBeInvoked", emoToBeInvoked);
				observedEmosValues.put("potential", potential);
				observedEmosValues.put("intensity", intensity);
				saveObservedEmotion(observedEmosValues);
			}
					
		}
		
	}
	
	private void invokeEventAgentCogUnitEmos(String emoToBeInvoked)
	{
		setGoalValues();
		calculateEventDerivedAttributes();
		
		HashMap<String, Object> observedEmosValues = new HashMap<String, Object>();
		Double emoFactor = (Double)emoAttitudes.get(emoToBeInvoked).get("emotion_factor");
		
		Iterator<Long> iterNeighbours = neighbours.keySet().iterator();
		
		while(iterNeighbours.hasNext()) {
			Long agentNeighbour = iterNeighbours.next();
			
			if((Boolean)neighbours.get(agentNeighbour).get("emotion_target")) {
				calculateAgentBasedAttributes(agentNeighbour);
				Double potential = 0.0;
				Double intensity = 0.0;
				
				if(emoToBeInvoked.equalsIgnoreCase("gratification")
						&& (desireSelf > 0)
						&& (praiseworthiness > 0)
						&& (cogUnit > 0)) {
					potential = (Double)EmotionFunctions.calcGratificationPotential(emoFactor, Math.abs(praiseworthiness), Math.abs(desireSelf), 
							expDiff, cogUnit, agentGlobalVars, eventGlobalVars);
				}
				else if(emoToBeInvoked.equalsIgnoreCase("remorse")
						&& (desireSelf < 0)
						&& (praiseworthiness < 0)
						&& (cogUnit > 0)) {
					potential = (Double)EmotionFunctions.calcRemorsePotential(emoFactor, Math.abs(praiseworthiness), Math.abs(desireSelf), 
							expDiff, cogUnit, agentGlobalVars, eventGlobalVars);
				}
				
				intensity = potential - getEmotionThreshold(emoToBeInvoked);
				observedEmosValues.put("targetEvent", eventID);
				observedEmosValues.put("neighbour", agentNeighbour);
				observedEmosValues.put("emoToBeInvoked", emoToBeInvoked);
				observedEmosValues.put("potential", potential);
				observedEmosValues.put("intensity", intensity);
				saveObservedEmotion(observedEmosValues);
			}
					
		}	
	}
	
	private void invokeObjectEmos(String emoToBeInvoked)
	{
		HashMap<String, Object> observedEmosValues = new HashMap<String, Object>();
		Double emoFactor = (Double)emoAttitudes.get(emoToBeInvoked).get("emotion_factor");
		initializeObject();
			
		Double potential = 0.0;
		Double intensity = 0.0;
			
			if(emoToBeInvoked.equalsIgnoreCase("liking")
					&& (appealingness > 0)) {
				potential = (Double)EmotionFunctions.calcLikingPotential(emoFactor, Math.abs(appealingness), familiarity, objectGlobalVars);
			}
			else if(emoToBeInvoked.equalsIgnoreCase("disliking")
					&& (appealingness < 0)) {
				potential = (Double)EmotionFunctions.calcDislikingPotential(emoFactor, Math.abs(appealingness), familiarity, objectGlobalVars);
			}	
			
			intensity = potential - getEmotionThreshold(emoToBeInvoked);
			observedEmosValues.put("targetObject", objectID);
			observedEmosValues.put("emoToBeInvoked", emoToBeInvoked);
			observedEmosValues.put("potential", potential);
			observedEmosValues.put("intensity", intensity);
			saveObservedEmotion(observedEmosValues);
		
	}
	
	
	
	
	private void calculateEventDerivedAttributes() {
		Double agentDesire = 0.0;

		Double eSenseOfReality = 0.0;
		Double ePsychoProximity = 0.0;
		Double eUnexpectedness = 0.0;
		Double eArousal = 0.0;
		System.out.println("Event id in calculateEventDerivedAttributes --> " + event.getEventId()); 

			agentDesire = event.getAgentDesirability();

			// Get the components of event global variables

			eSenseOfReality = event.getEventSenseOfReality();
			ePsychoProximity = event.getEventPsychoProximity();
			eUnexpectedness = event.getEventUnexpectedness();
			eArousal = event.getEventArousal();
			
			likelihood = event.getEventLikelihood();
			effort = event.getEventEffort();
			realization = event.getEventRealization();
			agentBelief = event.getAgentBelief();

		desireSelf = ((AGoalValue + IGoalValue + RGoalValue + agentDesire) + 4) / 8.0;
		eventGlobalVars = ((eSenseOfReality + ePsychoProximity
				+ eUnexpectedness + eArousal) + 4) / 8.0;
	}
	
	private void setPreviousHopeAndFearPotentialByEmotion(String emoToBeInvoked) {
		String prevEmotion = "hope";
		if (emoToBeInvoked.equalsIgnoreCase("fears-confirmed")
				|| emoToBeInvoked.equalsIgnoreCase("relief")) {
			prevEmotion = "fear";
		}
		prevStates = prevStateDAO
				.getPrevStateByIterNoAgentEventEmotionUserAndSim(
						constVars.getUserId(), constVars.getSimId(),
						(currIter - 1), agentID, eventID, prevEmotion);
		for(PreviousState prevState:prevStates)
		{
			prevHopePotential = prevState.getHopePotential();
			prevFearPotential = prevState.getFearPotential();
		}
	}
	
	private int getNumberOfPreviousStateRows() {
		return prevStates.size();
	}
	
	private void calculateAgentBasedAttributes(Long agentNeighbour) {
		praiseworthiness = (Double)neighbours.get(agentNeighbour).get("praiseworthiness");
		expDiff = (Double)neighbours.get(agentNeighbour).get("expectation_deviation");
		cogUnit = (Double)neighbours.get(agentNeighbour).get("cognitive_unit_strength");
		
		// Get Agent Global Variables
		Double aSenseOfReality = (Double)neighbours.get(agentNeighbour).get("agent_sense_of_reality");
		Double aPsychoProximity = (Double)neighbours.get(agentNeighbour).get("agent_psycho_proximity");
		Double aUnexpectedness = (Double)neighbours.get(agentNeighbour).get("agent_unexpectedness");
		Double aArousal = (Double)neighbours.get(agentNeighbour).get("agent_arousal");
		
		agentGlobalVars = ((aSenseOfReality + aPsychoProximity + aUnexpectedness + aArousal)+4)/8.0;
	}
	
	private void initializeObject() {
		if (objectID != -1) {
			objectDAO = context.getBean(ObjectTabDAO.class);
			object = objectDAO.getObjectByIterObjectIdAgentUserAndSimId(
					currIter, objectID, agentID, constVars.getUserId(), constVars.getSimId());
			
			Double oSenseOfReality = 0.0;
			Double oPsychoProximity = 0.0;
			Double oUnexpectedness = 0.0;
			Double oArousal = 0.0;
			
			
				familiarity = object.getObjFamiliarity();
				appealingness = object.getObjAppealingness();
				
				// Get components of object global variables
				
				oSenseOfReality = object.getObjSenseOfReality();
				oPsychoProximity = object.getObjPsychoProximity();
				oUnexpectedness = object.getObjUnexpectedness();
				oArousal = object.getObjArousal();
				
				objectGlobalVars = ((oSenseOfReality + oPsychoProximity + oUnexpectedness + oArousal)+4)/8.0;
		}
	}
	
	private Double getEmotionFactor(String emoToBeInvoked) {
		return (Double) emoAttitudes.get(emoToBeInvoked).get(
				"emotion_factor");
	}
	
	private Double getEmotionThreshold(String emoToBeInvoked) {
		return (Double) emoAttitudes.get(
				emoToBeInvoked).get("threshold");
	}
	
	private void saveObservedEmotion(Map<String, Object> observedEmotionValues) {
		ObservedEmotions obsEmos = new ObservedEmotions();
		obsEmos.setIterNo(currIter);
		obsEmos.setAgentId(agentID);
		obsEmos.setTargetEvent(Long.parseLong((String)observedEmotionValues.get("targetEvent")));
		obsEmos.setTargetNeighbour(Long.parseLong((String)observedEmotionValues.get("neighbour")));
		obsEmos.setTargetObj(Long.parseLong((String)observedEmotionValues.get("targetObject")));
		obsEmos.setEmotion((String)observedEmotionValues.get("emoToBeInvoked"));
		obsEmos.setEmoPot(Double.parseDouble((String)observedEmotionValues.get("potential")));
		obsEmos.setEmoIntensity(Double.parseDouble((String)observedEmotionValues.get("intensity")));
		obsEmos.setUserId(constVars.getUserId());
		obsEmos.setSimId(constVars.getSimId());
		observedEmosDAO.saveAndFlush(obsEmos);
	}
	
	private void savePreviousState(Map<String, Object> previousStateValues) {
		PreviousState prevState = new PreviousState();
		prevState.setEventId(eventID);
		prevState.setAgentId(agentID);
		prevState.setIterNo(currIter);
		prevState.setEmotion((String)previousStateValues.get("emoToBeInvoked"));
		prevState.setHopePotential(Double.parseDouble((String)previousStateValues.get("hopePotential")));
		prevState.setFearPotential(Double.parseDouble((String)previousStateValues.get("fearPotential")));
		prevState.setUserId(constVars.getUserId());
		prevState.setSimId(constVars.getSimId());
		prevStateDAO.saveAndFlush(prevState);
	}
	
}
