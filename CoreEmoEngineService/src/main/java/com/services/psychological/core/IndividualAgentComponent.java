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
	Long tempWorkingCount = 0l;
	private ArrayList<String> triggeredEmos;
	private HashMap<String, Integer> triggeredEmosPrecedence = new HashMap<String, Integer>();
	private HashMap<String, HashMap<String, Object>> emoAttitudes = new HashMap<String, HashMap<String, Object>>();
	private TreeMap<Integer, String> sortedEmoOccurCount = new TreeMap<Integer, String>();
	private SimulationDAO simulationDAO;
	private Simulation simulation;
	private EmotionalAttitudesDAO emotionalAttitudesDAO;
	private List<EmotionalAttitudes> emotionalAttitudes;
	private Set<String> emoAttKeySet;
	private AgentTabDAO agentTabDAO;
	private AgentTab agentTab;
	private EventTabDAO eventTabDAO;
	private List<EventTab> events;
	private ObservedEmotionsDAO observedEmosDAO;
	private PreviousStateDAO prevStateDAO;
	private List<PreviousState> prevStates;
	
	int emosOccur = 0;
	int calcOccur = 0;
	int invokedEmosCount = 0;

	private ModelVariables constVars;

	@Autowired
	@Qualifier("psycheConfig")
	private Properties psycheConfig;

	@Autowired
	ApplicationContext context;

	public IndividualAgentComponent(Long agentId, Long eventId,
			Long objId, ModelVariables modelVars) {
		agentID = agentId;
		eventID = eventId;
		objectID = objId;
		simulationId = modelVars.getSimId();
		isAgent = false;
		isEvent = false;
		isObject = false;
		this.constVars = modelVars;

	}
	
	public void agentPerception() {
		initializeSimulation();
		setCurrIter();
		setIterationValues();
		executeOneIteration();

	}
	
	private void setIterationValues() {
		if (currIter == 1) {
			proposedIter = simulation.getProposedIter();
			workingIter = proposedIter;
			maxIter = simulation.getMaxIter();
		} else {
			workingIter = simulation.getWorkingIter();
		}
	}
	
	private void initializeSimulation() {
		simulationDAO = context.getBean(SimulationDAO.class);
		simulation = simulationDAO.getSimulationByUserAndSimId(constVars.getUserId(), constVars.getSimId());
	}
	
	private void setCurrIter() {
		currIter = simulation.getCurrIter();
	}
	
	private void executeOneIteration() {
		if (currIter <= workingIter) {
			initializeEmotionalAttitudes();
			setAllEmotionalAttitudes();
			emoAttKeySet = emoAttitudes.keySet();
			setNoOfEmosToInvoke(emoAttKeySet.size());
			initializeAgentTab();
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
			
			setEmotionsOccurrenceCount(emotion, tempWorkingCount);
		}	
	}
	
	private void setEmotionsOccurrenceCount(String emotion, Long tempWorkingCount) {
		Double emoProb = (Double) emoAttitudes.get(emotion).get(
				"probability");
		Integer emoCount = (int) Math.round(workingIter * emoProb);
		tempWorkingCount += emoCount;
		emoAttitudes.get(emotion).put("occurrence_count", emoCount);

		if (sortedEmoOccurCount.containsKey(emoCount)) {
			String prevEmo = sortedEmoOccurCount.get(emoCount);
			sortedEmoOccurCount.put(emoCount, prevEmo + "," + emotion);
		} else {
			sortedEmoOccurCount.put(emoCount, emotion);
		}
	}
	
	private void updateEmosOccurrenceCount(String emotion, Integer count) {
		triggeredEmos.add(emotion);
		emoAttitudes.get(emotion).put(
				"occurrence_count", (count));
	}
	
	private void addEmotionToTriggeredEmotions(String emotion) {
		triggeredEmos.add(emotion);
	}
	
	private void updateTriggeredEmotionsPrecedence(String emotion) {
		triggeredEmosPrecedence.put(emotion,
				(Integer) emoAttitudes.get(emotion)
						.get("precedence"));
	}
	
	private Integer getLastOccurrenceCountForEmotion(String emotion) {
		return (Integer) emoAttitudes.get(emotion)
				.get("occurrence_count");
	}
	
	private Integer getLastOccurredCountForEmotion(String emotion) {
		return (Integer) emoAttitudes
				.get(emotion).get("occurred_count");
	}
	
	private void updateOccurredCountForEmotion(String emotion, Integer count) {
		emoAttitudes.get(emotion).put("occurred_count",
				count);
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
			addEmotionToTriggeredEmotions(emotion);
			updateEmosOccurrenceCount(emotion, calcOccur - emosOccur);
			updateTriggeredEmotionsPrecedence(emotion);
		} else {
			updateEmosOccurrenceCount(emotion, 0);
		}
		calcOccur = 0;
		emotion = null;
		emosOccur = 0;
	}
	
	private void updateEndingCounts(String emotion) {
		calcOccur = getLastOccurrenceCountForEmotion(emotion);
		emosOccur = getLastOccurredCountForEmotion(emotion);
		updateEmosOccurrenceCount(emotion, calcOccur - emosOccur);
	}
	
	private void updateInvokedCountsForEmotions() {
		Iterator<Integer> sortedEmoIter = sortedEmoOccurCount
				.descendingKeySet().iterator();

		while (sortedEmoIter.hasNext()) {
			Integer emoCnt = sortedEmoIter.next();
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
	
	private void updateWorkingCountForSimulation() {
		if (tempWorkingCount < maxIter) {
			workingIter = tempWorkingCount;
		} else {
			workingIter = maxIter;
		}
	}
	
	private void setSimulationAttributes() {
		simulation.setWorkingIter(workingIter);
	}
	
	private void saveSimulation() {
		simulationDAO.saveAndFlush(simulation);
	}
	
	private ArrayList<String> getTriggeredEmos() {
		return triggeredEmos;
	}

	private LinkedHashMap<String, Integer> getEmosSortedByPrecedence() {
		List<Entry<String, Integer>> sortedList = new LinkedList<Entry<String, Integer>>(
				triggeredEmosPrecedence.entrySet());
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
	
	private void invokeEmotions() {
		LinkedHashMap<String, Integer> trigEmosByPrecedence = getEmosSortedByPrecedence();
		Iterator<String> iterEmosByPrecedence = trigEmosByPrecedence.keySet()
				.iterator();
		
		while (iterEmosByPrecedence.hasNext()) {
			String emoToBeInvoked = iterEmosByPrecedence.next();

			if (emoToBeInvoked.equalsIgnoreCase("joy")
					|| emoToBeInvoked.equalsIgnoreCase("distress")) {
				invokeDesirabilityEmos(emoToBeInvoked);
			} else if (emoToBeInvoked.equalsIgnoreCase("happy-for")
					|| emoToBeInvoked.equalsIgnoreCase("sorry-for")
					|| emoToBeInvoked.equalsIgnoreCase("resentment")
					|| emoToBeInvoked.equalsIgnoreCase("gloating")) {
				invokeFortunesOfOthersEmos(emoToBeInvoked);
			} else if (emoToBeInvoked.equalsIgnoreCase("hope")
					|| emoToBeInvoked.equalsIgnoreCase("fear")) {
				invokeProspectEmos(emoToBeInvoked);
			} else if ((currIter > 1)
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
			} else if (emoToBeInvoked.equalsIgnoreCase("gratitude")
					|| emoToBeInvoked.equalsIgnoreCase("anger")) {
				invokeEventAgentNoCogUnitEmos(emoToBeInvoked);
			} else if (emoToBeInvoked.equalsIgnoreCase("gratification")
					|| emoToBeInvoked.equalsIgnoreCase("remorse")) {
				invokeEventAgentCogUnitEmos(emoToBeInvoked);
			} else if (emoToBeInvoked.equalsIgnoreCase("liking")
					|| emoToBeInvoked.equalsIgnoreCase("disliking")) {
				invokeObjectEmos(emoToBeInvoked);
			} else {
				System.err.println("Not a valid emotion for this iteration.");
			}

		}
	}
	
	private void setGoalValues() {
		AGoalValue = agentTab.getaGoalValue();
		IGoalValue = agentTab.getiGoalValue();
		RGoalValue = agentTab.getrGoalValue();
	}
	
	private void initializeEvents() {
		eventTabDAO = context.getBean(EventTabDAO.class);
		events = eventTabDAO
				.getEventsBySimIdUserIdIterEventAndAgent(constVars.getSimId(),
						constVars.getUserId(), currIter, eventID, agentID);
	}
	
	private void initializeObservedEmosDAO() {
		observedEmosDAO = context
				.getBean(ObservedEmotionsDAO.class);
	}
	
	private void initializePreviousStateDAO() {
		prevStateDAO = context.getBean(PreviousStateDAO.class);
	}

	private void calculateEventDerivedAttributes() {
		Double agentDesire = 0.0;

		Double eSenseOfReality = 0.0;
		Double ePsychoProximity = 0.0;
		Double eUnexpectedness = 0.0;
		Double eArousal = 0.0;

		for (EventTab event : events) {
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
		}

		desireSelf = ((AGoalValue + IGoalValue + RGoalValue + agentDesire) + 4) / 8.0;
		eventGlobalVars = ((eSenseOfReality + ePsychoProximity
				+ eUnexpectedness + eArousal) + 4) / 8.0;
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
	
	private Boolean isNeighbourEmotionTarget(Long neighbour) {
		return (Boolean) neighbours.get(neighbour).get("emotion_target");
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

	private Double getOthersDesirability() {
		return desireOther;
	}
	
	private Double getDeservingness() {
		return deservingness;
	}
	
	private Double getOthersLiking() {
		return likingOther;
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
	
	private void invokeAgentCogUnitEmos(String emoToBeInvoked)
	{
		HashMap<String, Object> observedEmosValues = new HashMap<String, Object>();
		Double emoFactor = (Double)emoAttitudes.get(emoToBeInvoked).get("emotion_factor");
		
		Iterator<Long> iterNeighbours = neighbours.keySet().iterator();
		
		while(iterNeighbours.hasNext())
		{
			Long agentNeighbour = iterNeighbours.next();
			Double potential = 0.0;
			Double intensity = 0.0;
			
			if((Boolean)neighbours.get(agentNeighbour).get("emotion_target"))
			{
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
		
		while(iterNeighbours.hasNext())
		{
			Long agentNeighbour = iterNeighbours.next();
			
			if((Boolean)neighbours.get(agentNeighbour).get("emotion_target"))
			{
				calculateAgentBasedAttributes(agentNeighbour);
				Double potential = 0.0;
				Double intensity = 0.0;
				
				if(emoToBeInvoked.equalsIgnoreCase("appreciation")
						&& (praiseworthiness > 0))
				{
					potential = (Double)EmotionFunctions.calcAppreciationPotential(emoFactor, Math.abs(praiseworthiness), expDiff, agentGlobalVars);
				}
				else if(emoToBeInvoked.equalsIgnoreCase("reproach")
						&& (praiseworthiness < 0))
				{
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
		AgentTabDAO agentDAO = context.getBean(AgentTabDAO.class);
		AgentTab agent = agentDAO.getAgentByUserIdSimIdAgentIdAndIter(
				constVars.getUserId(), constVars.getSimId(), agentID, currIter);

		AGoalValue = agent.getaGoalValue();
		IGoalValue = agent.getiGoalValue();
		RGoalValue = agent.getrGoalValue();

		EventTabDAO eventTabDAO = context.getBean(EventTabDAO.class);
		List<EventTab> events = eventTabDAO
				.getEventsBySimIdUserIdIterEventAndAgent(constVars.getSimId(),
						constVars.getUserId(), currIter, eventID, agentID);
		Double agentDesire = 0.0;
		
		Double eSenseOfReality = 0.0;
		Double ePsychoProximity = 0.0;
		Double eUnexpectedness = 0.0;
		Double eArousal = 0.0;
		
		for(EventTab event:events)
		{
			agentDesire = event.getAgentDesirability();
			
			// Get the event global variables component
			
			eSenseOfReality = event.getEventSenseOfReality();
			ePsychoProximity = event.getEventPsychoProximity();
			eUnexpectedness = event.getEventUnexpectedness();
			eArousal = event.getEventArousal();
		}
		
		eventGlobalVars = ((eSenseOfReality + ePsychoProximity 
		           + eUnexpectedness + eArousal)+4)/8.0;
		
		desireSelf = ((AGoalValue + IGoalValue + RGoalValue + agentDesire)+4)/8.0;
		
		Double emoFactor = (Double)emoAttitudes.get(emoToBeInvoked).get("emotion_factor");
		
		Iterator<Long> iterNeighbours = neighbours.keySet().iterator();
		
		while(iterNeighbours.hasNext())
		{
			Long agentNeighbour = iterNeighbours.next();
			
			if((Boolean)neighbours.get(agentNeighbour).get("emotion_target"))
			{
				praiseworthiness = (Double)neighbours.get(agentNeighbour).get("praiseworthiness");
				Double expDiff = (Double)neighbours.get(agentNeighbour).get("expectation_deviation");
				
				
				// Get Agent Global Variables
				
				Double aSenseOfReality = (Double)neighbours.get(agentNeighbour).get("agent_sense_of_reality");
				Double aPsychoProximity = (Double)neighbours.get(agentNeighbour).get("agent_psycho_proximity");
				Double aUnexpectedness = (Double)neighbours.get(agentNeighbour).get("agent_unexpectedness");
				Double aArousal = (Double)neighbours.get(agentNeighbour).get("agent_arousal");
				
				agentGlobalVars = ((aSenseOfReality + aPsychoProximity + aUnexpectedness + aArousal)+4)/8.0;
				
				// Invoke the gratitude/anger emotion below
				
				if(emoToBeInvoked.equalsIgnoreCase("gratitude")
						&& (desireSelf > 0)
						&& (praiseworthiness > 0))
				{
					Double gratitudePotential = (Double)EmotionFunctions.calcGratitudePotential(emoFactor, Math.abs(praiseworthiness), Math.abs(desireSelf), 
							expDiff, agentGlobalVars, eventGlobalVars);
					Double gratitudeIntensity = (gratitudePotential - (Double)emoAttitudes.get(emoToBeInvoked).get("threshold"));
					
					// Update the observed emotions
					ObservedEmotions obsEmos = new ObservedEmotions();
					obsEmos.setIterNo(currIter);
					obsEmos.setAgentId(agentID);
					obsEmos.setTargetEvent(eventID);
					obsEmos.setTargetNeighbour(agentNeighbour);
					obsEmos.setTargetObj(-1l);
					obsEmos.setEmotion(emoToBeInvoked);
					obsEmos.setEmoPot(gratitudePotential);
					obsEmos.setEmoIntensity(gratitudeIntensity);
					obsEmos.setUserId(constVars.getUserId());
					obsEmos.setSimId(constVars.getSimId());
					ObservedEmotionsDAO obsEmosDAO = context
							.getBean(ObservedEmotionsDAO.class);
					obsEmosDAO.saveAndFlush(obsEmos);
				}
				else if(emoToBeInvoked.equalsIgnoreCase("anger")
						&& (desireSelf < 0)
						&& (praiseworthiness < 0))
				{
					Double angerPotential = (Double)EmotionFunctions.calcAngerPotential(emoFactor, Math.abs(praiseworthiness), Math.abs(desireSelf), 
							expDiff, agentGlobalVars, eventGlobalVars);
					Double angerIntensity = (angerPotential - (Double)emoAttitudes.get(emoToBeInvoked).get("threshold"));
					
					// Update the observed emotions
					ObservedEmotions obsEmos = new ObservedEmotions();
					obsEmos.setIterNo(currIter);
					obsEmos.setAgentId(agentID);
					obsEmos.setTargetEvent(eventID);
					obsEmos.setTargetNeighbour(agentNeighbour);
					obsEmos.setTargetObj(-1l);
					obsEmos.setEmotion(emoToBeInvoked);
					obsEmos.setEmoPot(angerPotential);
					obsEmos.setEmoIntensity(angerIntensity);
					obsEmos.setUserId(constVars.getUserId());
					obsEmos.setSimId(constVars.getSimId());
					ObservedEmotionsDAO obsEmosDAO = context
							.getBean(ObservedEmotionsDAO.class);
					obsEmosDAO.saveAndFlush(obsEmos);
				}
				
				
				// Invoked the gratitude/anger emotion above
				
			}
					
		}
		
	}
	
	
	private void invokeEventAgentCogUnitEmos(String emoToBeInvoked)
	{
		AgentTabDAO agentDAO = context.getBean(AgentTabDAO.class);
		AgentTab agent = agentDAO.getAgentByUserIdSimIdAgentIdAndIter(
				constVars.getUserId(), constVars.getSimId(), agentID, currIter);

		AGoalValue = agent.getaGoalValue();
		IGoalValue = agent.getiGoalValue();
		RGoalValue = agent.getrGoalValue();

		EventTabDAO eventTabDAO = context.getBean(EventTabDAO.class);
		List<EventTab> events = eventTabDAO
				.getEventsBySimIdUserIdIterEventAndAgent(constVars.getSimId(),
						constVars.getUserId(), currIter, eventID, agentID);
		Double agentDesire = 0.0;
		
		Double eSenseOfReality = 0.0;
		Double ePsychoProximity = 0.0;
		Double eUnexpectedness = 0.0;
		Double eArousal = 0.0;
		
		for(EventTab event:events)
		{
			agentDesire = event.getAgentDesirability();
			
			// Get the event global variables component
			
			eSenseOfReality = event.getEventSenseOfReality();
			ePsychoProximity = event.getEventPsychoProximity();
			eUnexpectedness = event.getEventUnexpectedness();
			eArousal = event.getEventArousal();
		}
		
		
		eventGlobalVars = ((eSenseOfReality + ePsychoProximity 
		           + eUnexpectedness + eArousal)+4)/8.0;
		
		desireSelf = ((AGoalValue + IGoalValue + RGoalValue + agentDesire)+4)/8.0;
		
		Double emoFactor = (Double)emoAttitudes.get(emoToBeInvoked).get("emotion_factor");
		
		Iterator<Long> iterNeighbours = neighbours.keySet().iterator();
		
		while(iterNeighbours.hasNext())
		{
			Long agentNeighbour = iterNeighbours.next();
			
			if((Boolean)neighbours.get(agentNeighbour).get("emotion_target"))
			{
				praiseworthiness = (Double)neighbours.get(agentNeighbour).get("praiseworthiness");
				Double expDiff = (Double)neighbours.get(agentNeighbour).get("expectation_deviation");
				Double cogUnit = (Double)neighbours.get(agentNeighbour).get("cognitive_unit_strength");
				
				// Get Agent Global Variables
				
				Double aSenseOfReality = (Double)neighbours.get(agentNeighbour).get("agent_sense_of_reality");
				Double aPsychoProximity = (Double)neighbours.get(agentNeighbour).get("agent_psycho_proximity");
				Double aUnexpectedness = (Double)neighbours.get(agentNeighbour).get("agent_unexpectedness");
				Double aArousal = (Double)neighbours.get(agentNeighbour).get("agent_arousal");
				
				agentGlobalVars = ((aSenseOfReality + aPsychoProximity + aUnexpectedness + aArousal)+4)/8.0;
				
				// Invoke the gratification/remorse emotion below
				
				if(emoToBeInvoked.equalsIgnoreCase("gratification")
						&& (desireSelf > 0)
						&& (praiseworthiness > 0)
						&& (cogUnit > 0))
				{
					Double gratificationPotential = (Double)EmotionFunctions.calcGratificationPotential(emoFactor, Math.abs(praiseworthiness), Math.abs(desireSelf), 
							expDiff, cogUnit, agentGlobalVars, eventGlobalVars);
					Double gratificationIntensity = (gratificationPotential - (Double)emoAttitudes.get(emoToBeInvoked).get("threshold"));
					
					// Update the observed emotions
					ObservedEmotions obsEmos = new ObservedEmotions();
					obsEmos.setIterNo(currIter);
					obsEmos.setAgentId(agentID);
					obsEmos.setTargetEvent(eventID);
					obsEmos.setTargetNeighbour(agentNeighbour);
					obsEmos.setTargetObj(-1l);
					obsEmos.setEmotion(emoToBeInvoked);
					obsEmos.setEmoPot(gratificationPotential);
					obsEmos.setEmoIntensity(gratificationIntensity);
					obsEmos.setUserId(constVars.getUserId());
					obsEmos.setSimId(constVars.getSimId());
					ObservedEmotionsDAO obsEmosDAO = context
							.getBean(ObservedEmotionsDAO.class);
					obsEmosDAO.saveAndFlush(obsEmos);
				}
				else if(emoToBeInvoked.equalsIgnoreCase("remorse")
						&& (desireSelf < 0)
						&& (praiseworthiness < 0)
						&& (cogUnit > 0))
				{
					Double remorsePotential = (Double)EmotionFunctions.calcRemorsePotential(emoFactor, Math.abs(praiseworthiness), Math.abs(desireSelf), 
							expDiff, cogUnit, agentGlobalVars, eventGlobalVars);
					Double remorseIntensity = (remorsePotential - (Double)emoAttitudes.get(emoToBeInvoked).get("threshold"));
					
					// Update the observed emotions
					ObservedEmotions obsEmos = new ObservedEmotions();
					obsEmos.setIterNo(currIter);
					obsEmos.setAgentId(agentID);
					obsEmos.setTargetEvent(eventID);
					obsEmos.setTargetNeighbour(agentNeighbour);
					obsEmos.setTargetObj(-1l);
					obsEmos.setEmotion(emoToBeInvoked);
					obsEmos.setEmoPot(remorsePotential);
					obsEmos.setEmoIntensity(remorseIntensity);
					obsEmos.setUserId(constVars.getUserId());
					obsEmos.setSimId(constVars.getSimId());
					ObservedEmotionsDAO obsEmosDAO = context
							.getBean(ObservedEmotionsDAO.class);
					obsEmosDAO.saveAndFlush(obsEmos);
				}
				
				
				// Invoked the gratification/remorse emotion above
				
			}
					
		}	
	}
	
	
	private void invokeObjectEmos(String emoToBeInvoked)
	{
		Double emoFactor = (Double)emoAttitudes.get(emoToBeInvoked).get("emotion_factor");
		
		ObjectTabDAO objectDAO = context.getBean(ObjectTabDAO.class);
		ObjectTab object = objectDAO.getObjectByIterObjectIdAgentUserAndSimId(
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
			
			// Invoke liking/disliking emotion below
			
			if(emoToBeInvoked.equalsIgnoreCase("liking")
					&& (appealingness > 0))
			{
				Double likingPotential = (Double)EmotionFunctions.calcLikingPotential(emoFactor, Math.abs(appealingness), familiarity, objectGlobalVars);
				Double likingIntensity = (likingPotential - (Double)emoAttitudes.get(emoToBeInvoked).get("threshold"));
				
				// Update the observed emotions
				ObservedEmotions obsEmos = new ObservedEmotions();
				obsEmos.setIterNo(currIter);
				obsEmos.setAgentId(agentID);
				obsEmos.setTargetEvent(-1l);
				obsEmos.setTargetNeighbour(-1l);
				obsEmos.setTargetObj(objectID);
				obsEmos.setEmotion(emoToBeInvoked);
				obsEmos.setEmoPot(likingPotential);
				obsEmos.setEmoIntensity(likingIntensity);
				obsEmos.setUserId(constVars.getUserId());
				obsEmos.setSimId(constVars.getSimId());
				ObservedEmotionsDAO obsEmosDAO = context
						.getBean(ObservedEmotionsDAO.class);
				obsEmosDAO.saveAndFlush(obsEmos);
			}
			else if(emoToBeInvoked.equalsIgnoreCase("disliking")
					&& (appealingness < 0))
			{
				Double dislikingPotential = (Double)EmotionFunctions.calcDislikingPotential(emoFactor, Math.abs(appealingness), familiarity, objectGlobalVars);
				Double dislikingIntensity = (dislikingPotential - (Double)emoAttitudes.get(emoToBeInvoked).get("threshold"));
				
				// Update the observed emotions
				ObservedEmotions obsEmos = new ObservedEmotions();
				obsEmos.setIterNo(currIter);
				obsEmos.setAgentId(agentID);
				obsEmos.setTargetEvent(-1l);
				obsEmos.setTargetNeighbour(-1l);
				obsEmos.setTargetObj(objectID);
				obsEmos.setEmotion(emoToBeInvoked);
				obsEmos.setEmoPot(dislikingPotential);
				obsEmos.setEmoIntensity(dislikingIntensity);
				obsEmos.setUserId(constVars.getUserId());
				obsEmos.setSimId(constVars.getSimId());
				ObservedEmotionsDAO obsEmosDAO = context
						.getBean(ObservedEmotionsDAO.class);
				obsEmosDAO.saveAndFlush(obsEmos);
			}		
		
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
	

}
