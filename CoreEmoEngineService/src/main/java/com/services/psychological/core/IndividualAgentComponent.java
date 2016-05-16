package com.services.psychological.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
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
	private HashMap<Long, HashMap<String, Object>> neighbours = new HashMap<Long, HashMap<String, Object>>();
	private Long simulation_id;
	private Long proposedIter;
	private Long workingIter;
	private Long maxIter;
	private Long currIter;
	private ArrayList<String> triggeredEmos;
	private HashMap<String, Integer> triggeredEmosPrecedence = new HashMap<String, Integer>();
	private HashMap<String, HashMap<String, Object>> emoAttitudes = new HashMap<String, HashMap<String, Object>>();

	private ConstantVariables constVars;

	@Autowired
	@Qualifier("psycheConfig")
	private Properties psycheConfig;

	@Autowired
	ApplicationContext context;

	public IndividualAgentComponent(Long simId, Long agentId, Long eventId,
			Long objId, ConstantVariables constVars) {
		agentID = agentId;
		eventID = eventId;
		objectID = objId;
		simulation_id = simId;
		isAgent = false;
		isEvent = false;
		isObject = false;
		this.constVars = constVars;

	}

	public void agentPerception() {
		SimulationDAO simDAO = context.getBean(SimulationDAO.class);
		Simulation sim = simDAO.getSimulationByUserAndSimId(
				constVars.getUserId(), constVars.getSimId());

		currIter = sim.getCurrIter();

		if (currIter == 1) {
			proposedIter = sim.getProposedIter();
			workingIter = proposedIter;
			maxIter = sim.getMaxIter();
		} else {
			workingIter = sim.getWorkingIter();
		}

		if (currIter <= workingIter) {
			EmotionalAttitudesDAO emoAttDAO = context
					.getBean(EmotionalAttitudesDAO.class);
			List<EmotionalAttitudes> emoAtts = emoAttDAO
					.getEmoAttByAgentIdUserIdIterAndSimId(agentID,
							constVars.getUserId(), currIter,
							constVars.getSimId());

			for (EmotionalAttitudes emoAttSingle : emoAtts) {
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

			Set<String> emoAttKeySet = emoAttitudes.keySet();
			Iterator<String> emoIter = emoAttKeySet.iterator();
			Long tempWorkingCount = 0l;

			int noOfEmos = emoAttKeySet.size();
			Long noOfEmosToInvoke = 0l;

			AgentTabDAO agentDAO = context.getBean(AgentTabDAO.class);
			AgentTab agent = agentDAO.getAgentByUserIdSimIdAgentIdAndIter(
					constVars.getUserId(), constVars.getSimId(), agentID,
					currIter);

			noOfEmosToInvoke = agent.getNumOfEmosToInvoke();
			if (noOfEmosToInvoke == null) {
				noOfEmosToInvoke = Math.round(noOfEmos * Math.random());
			}

			TreeMap<Integer, String> sortedEmoOccurCount = new TreeMap<Integer, String>();

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

			Iterator<Integer> sortedEmoIter = sortedEmoOccurCount
					.descendingKeySet().iterator();

			int invokeNoEmos = 0;

			while (sortedEmoIter.hasNext()) {
				Integer emoCnt = sortedEmoIter.next();
				String emotions = sortedEmoOccurCount.get(emoCnt);
				int emosOccur = 0;
				int calcOccur = 0;
				String tempEmo = "";

				if (invokeNoEmos < noOfEmosToInvoke) {// ---- **** Outermost IF
														// **** ----
					if (emotions.contains(",")) {
						int incCount = emotions.split(",").length;

						if (incCount > (noOfEmosToInvoke - invokeNoEmos - 1)) {
							for (int i = 0; i < (noOfEmosToInvoke - invokeNoEmos); i++) {
								tempEmo = emotions.split(",")[i];

								emosOccur++;
								calcOccur = (Integer) emoAttitudes.get(tempEmo)
										.get("occurrence_count");
								emosOccur += (Integer) emoAttitudes
										.get(tempEmo).get("occurred_count");
								emoAttitudes.get(tempEmo).put("occurred_count",
										emosOccur);

								if (calcOccur >= emosOccur) {
									triggeredEmos.add(tempEmo);
									emoAttitudes.get(tempEmo).put(
											"occurrence_count",
											(calcOccur - emosOccur));
									triggeredEmosPrecedence.put(tempEmo,
											(Integer) emoAttitudes.get(tempEmo)
													.get("precedence"));
								} else {
									emoAttitudes.get(tempEmo).put(
											"occurrence_count", 0);
								}
								calcOccur = 0;
								tempEmo = null;
								emosOccur = 0;
							}
							break;
						} else {
							for (int i = 0; i < incCount; i++) {
								tempEmo = emotions.split(",")[i];

								emosOccur++;
								calcOccur = (Integer) emoAttitudes.get(tempEmo)
										.get("occurrence_count");
								emosOccur += (Integer) emoAttitudes
										.get(tempEmo).get("occurred_count");
								emoAttitudes.get(tempEmo).put("occurred_count",
										emosOccur);

								if (calcOccur >= emosOccur) {
									triggeredEmos.add(tempEmo);
									emoAttitudes.get(tempEmo).put(
											"occurrence_count",
											(calcOccur - emosOccur));
									triggeredEmosPrecedence.put(tempEmo,
											(Integer) emoAttitudes.get(tempEmo)
													.get("precedence"));
								} else {
									emoAttitudes.get(tempEmo).put(
											"occurrence_count", 0);
								}
								calcOccur = 0;
								tempEmo = null;
								emosOccur = 0;
							}

							invokeNoEmos += (incCount - 1);
						}
					} else {

						emosOccur++;
						calcOccur = (Integer) emoAttitudes.get(emotions).get(
								"occurrence_count");
						emosOccur += (Integer) emoAttitudes.get(emotions).get(
								"occurred_count");
						emoAttitudes.get(emotions).put("occurred_count",
								emosOccur);

						if (calcOccur >= emosOccur) {
							triggeredEmos.add(emotions);
							emoAttitudes.get(emotions).put("occurrence_count",
									(calcOccur - emosOccur));
							triggeredEmosPrecedence.put(
									emotions,
									(Integer) emoAttitudes.get(emotions).get(
											"precedence"));
						} else {
							emoAttitudes.get(emotions).put("occurrence_count",
									0);
						}
						calcOccur = 0;
						tempEmo = null;
						emosOccur = 0;

						invokeNoEmos++;
					}

				}// ---- **** Outermost IF **** ----
				else {
					if (emotions.contains(",")) {
						int noCombEmos = emotions.split(",").length;

						for (int i = 0; i < noCombEmos; i++) {
							String tmpEmo = emotions.split(",")[i];
							calcOccur = (Integer) emoAttitudes.get(tmpEmo).get(
									"occurrence_count");
							emosOccur = (Integer) emoAttitudes.get(tmpEmo).get(
									"occurred_count");
							emoAttitudes.get(tmpEmo).put("occurrence_count",
									(calcOccur - emosOccur));
						}

					} else {
						calcOccur = (Integer) emoAttitudes.get(emotions).get(
								"occurrence_count");
						emosOccur = (Integer) emoAttitudes.get(emotions).get(
								"occurred_count");
						emoAttitudes.get(emotions).put("occurrence_count",
								(calcOccur - emosOccur));
					}
				}
			}

			if (tempWorkingCount < maxIter) {
				workingIter = tempWorkingCount;
			} else {
				workingIter = maxIter;
			}

			sim.setWorkingIter(workingIter);
			simDAO.saveAndFlush(sim);

		} else {
			System.out.println("Simulation completes.");
		}

	}

	public ArrayList<String> getTriggeredEmos() {
		return triggeredEmos;
	}

	public LinkedHashMap<String, Integer> getEmosSortedByPrecedence() {
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
		LinkedHashMap<String, Integer> trigEmosByPrecedence = getEmosSortedByPrecedence();
		Iterator<String> iterEmosByPrecedence = trigEmosByPrecedence.keySet()
				.iterator();

		AgentNetworkDAO agNetDAO = context.getBean(AgentNetworkDAO.class);

		List<AgentNetwork> neighboursList = agNetDAO.getAgentNeighbours(
				constVars.getSimId(), constVars.getUserId(), currIter, agentID,
				eventID);

		for (AgentNetwork neighbour : neighboursList) {
			// Read all the data from the agent_network
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

	private void invokeDesirabilityEmos(String emoToBeInvoked) {
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

		for (EventTab event : events) {
			agentDesire = event.getAgentDesirability();

			// Get the components of event global variables

			eSenseOfReality = event.getEventSenseOfReality();
			ePsychoProximity = event.getEventPsychoProximity();
			eUnexpectedness = event.getEventUnexpectedness();
			eArousal = event.getEventArousal();
		}

		desireSelf = ((AGoalValue + IGoalValue + RGoalValue + agentDesire) + 4) / 8.0;
		eventGlobalVars = ((eSenseOfReality + ePsychoProximity
				+ eUnexpectedness + eArousal) + 4) / 8.0;

		Double emoFactor = (Double) emoAttitudes.get(emoToBeInvoked).get(
				"emotion_factor");

		// Invoke the joy/distress emotions below

		if (emoToBeInvoked.equalsIgnoreCase("joy") && (desireSelf > 0)) {

			Double joyPotential = EmotionFunctions.calcJoyPotential(emoFactor,
					Math.abs(desireSelf), eventGlobalVars);
			Double joyIntensity = (joyPotential - (Double) emoAttitudes.get(
					emoToBeInvoked).get("threshold"));

			ObservedEmotions obsEmos = new ObservedEmotions();
			obsEmos.setIterNo(currIter);
			obsEmos.setAgentId(agentID);
			obsEmos.setTargetEvent(eventID);
			obsEmos.setTargetNeighbour(-1l);
			obsEmos.setTargetObj(-1l);
			obsEmos.setEmotion(emoToBeInvoked);
			obsEmos.setEmoPot(joyPotential);
			obsEmos.setEmoIntensity(joyIntensity);
			obsEmos.setUserId(constVars.getUserId());
			obsEmos.setSimId(constVars.getSimId());
			ObservedEmotionsDAO obsEmosDAO = context
					.getBean(ObservedEmotionsDAO.class);
			obsEmosDAO.saveAndFlush(obsEmos);

		} else if (emoToBeInvoked.equalsIgnoreCase("distress")
				&& (desireSelf < 0)) {
			Double distressPotential = EmotionFunctions.calcDistressPotential(
					emoFactor, Math.abs(desireSelf), eventGlobalVars);
			Double distressIntensity = (distressPotential - (Double) emoAttitudes
					.get(emoToBeInvoked).get("threshold"));

			ObservedEmotions obsEmos = new ObservedEmotions();
			obsEmos.setIterNo(currIter);
			obsEmos.setAgentId(agentID);
			obsEmos.setTargetEvent(eventID);
			obsEmos.setTargetNeighbour(-1l);
			obsEmos.setTargetObj(-1l);
			obsEmos.setEmotion(emoToBeInvoked);
			obsEmos.setEmoPot(distressPotential);
			obsEmos.setEmoIntensity(distressIntensity);
			obsEmos.setUserId(constVars.getUserId());
			obsEmos.setSimId(constVars.getSimId());
			ObservedEmotionsDAO obsEmosDAO = context
					.getBean(ObservedEmotionsDAO.class);
			obsEmosDAO.saveAndFlush(obsEmos);
		}

	}

	private void invokeFortunesOfOthersEmos(String emoToBeInvoked) {
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

		for (EventTab event : events) {
			agentDesire = event.getAgentDesirability();

			// Get the components of event global variables

			eSenseOfReality = event.getEventSenseOfReality();
			ePsychoProximity = event.getEventPsychoProximity();
			eUnexpectedness = event.getEventUnexpectedness();
			eArousal = event.getEventArousal();
		}

		desireSelf = ((AGoalValue + IGoalValue + RGoalValue + agentDesire) + 4) / 8.0;
		eventGlobalVars = ((eSenseOfReality + ePsychoProximity
				+ eUnexpectedness + eArousal) + 4) / 8.0;

		Double emoFactor = (Double) emoAttitudes.get(emoToBeInvoked).get(
				"emotion_factor");

		// Get the variables for neighbours who are target of the emotion

		Iterator<Long> iterNeighbours = neighbours.keySet().iterator();

		while (iterNeighbours.hasNext()) {
			Long agentNeighbour = iterNeighbours.next();

			if ((Boolean) neighbours.get(agentNeighbour).get("emotion_target")) {
				Double desireOther = (Double) neighbours.get(agentNeighbour)
						.get("other_desirability");
				Double deservingness = (Double) neighbours.get(agentNeighbour)
						.get("deservingness");
				Double likingOther = (Double) neighbours.get(agentNeighbour)
						.get("other_liking");

				// Invoke the fortunes of others emotion below

				if (emoToBeInvoked.equalsIgnoreCase("happy-for")
						&& (desireSelf > 0) && (desireOther > 0)) {

					Double happyForPotential = EmotionFunctions
							.calcHappyForPotential(emoFactor,
									Math.abs(desireSelf),
									Math.abs(desireOther), deservingness,
									likingOther, eventGlobalVars);
					Double happyForIntensity = (happyForPotential - (Double) emoAttitudes
							.get(emoToBeInvoked).get("threshold"));

					ObservedEmotions obsEmos = new ObservedEmotions();
					obsEmos.setIterNo(currIter);
					obsEmos.setAgentId(agentID);
					obsEmos.setTargetEvent(eventID);
					obsEmos.setTargetNeighbour(-1l);
					obsEmos.setTargetObj(-1l);
					obsEmos.setEmotion(emoToBeInvoked);
					obsEmos.setEmoPot(happyForPotential);
					obsEmos.setEmoIntensity(happyForIntensity);
					obsEmos.setUserId(constVars.getUserId());
					obsEmos.setSimId(constVars.getSimId());
					ObservedEmotionsDAO obsEmosDAO = context
							.getBean(ObservedEmotionsDAO.class);
					obsEmosDAO.saveAndFlush(obsEmos);
				} else if (emoToBeInvoked.equalsIgnoreCase("sorry-for")
						&& (desireSelf < 0) && (desireOther < 0)) {

					Double sorryForPotential = EmotionFunctions
							.calcSorryForPotential(emoFactor,
									Math.abs(desireSelf),
									Math.abs(desireOther), deservingness,
									likingOther, eventGlobalVars);
					Double sorryForIntensity = (sorryForPotential - (Double) emoAttitudes
							.get(emoToBeInvoked).get("threshold"));

					ObservedEmotions obsEmos = new ObservedEmotions();
					obsEmos.setIterNo(currIter);
					obsEmos.setAgentId(agentID);
					obsEmos.setTargetEvent(eventID);
					obsEmos.setTargetNeighbour(-1l);
					obsEmos.setTargetObj(-1l);
					obsEmos.setEmotion(emoToBeInvoked);
					obsEmos.setEmoPot(sorryForPotential);
					obsEmos.setEmoIntensity(sorryForIntensity);
					obsEmos.setUserId(constVars.getUserId());
					obsEmos.setSimId(constVars.getSimId());
					ObservedEmotionsDAO obsEmosDAO = context
							.getBean(ObservedEmotionsDAO.class);
					obsEmosDAO.saveAndFlush(obsEmos);

				} else if (emoToBeInvoked.equalsIgnoreCase("resentment")
						&& (desireSelf < 0) && (desireOther > 0)) {

					Double resentmentPotential = EmotionFunctions
							.calcResentmentPotential(emoFactor,
									Math.abs(desireSelf),
									Math.abs(desireOther), deservingness,
									likingOther, eventGlobalVars);
					Double resentmentIntensity = (resentmentPotential - (Double) emoAttitudes
							.get(emoToBeInvoked).get("threshold"));

					ObservedEmotions obsEmos = new ObservedEmotions();
					obsEmos.setIterNo(currIter);
					obsEmos.setAgentId(agentID);
					obsEmos.setTargetEvent(eventID);
					obsEmos.setTargetNeighbour(-1l);
					obsEmos.setTargetObj(-1l);
					obsEmos.setEmotion(emoToBeInvoked);
					obsEmos.setEmoPot(resentmentPotential);
					obsEmos.setEmoIntensity(resentmentIntensity);
					obsEmos.setUserId(constVars.getUserId());
					obsEmos.setSimId(constVars.getSimId());
					ObservedEmotionsDAO obsEmosDAO = context
							.getBean(ObservedEmotionsDAO.class);
					obsEmosDAO.saveAndFlush(obsEmos);

				} else if (emoToBeInvoked.equalsIgnoreCase("gloating")
						&& (desireSelf > 0) && (desireOther < 0)) {

					Double gloatingPotential = EmotionFunctions
							.calcGloatingPotential(emoFactor,
									Math.abs(desireSelf),
									Math.abs(desireOther), deservingness,
									likingOther, eventGlobalVars);
					Double gloatingIntensity = (gloatingPotential - (Double) emoAttitudes
							.get(emoToBeInvoked).get("threshold"));

					ObservedEmotions obsEmos = new ObservedEmotions();
					obsEmos.setIterNo(currIter);
					obsEmos.setAgentId(agentID);
					obsEmos.setTargetEvent(eventID);
					obsEmos.setTargetNeighbour(-1l);
					obsEmos.setTargetObj(-1l);
					obsEmos.setEmotion(emoToBeInvoked);
					obsEmos.setEmoPot(gloatingPotential);
					obsEmos.setEmoIntensity(gloatingIntensity);
					obsEmos.setUserId(constVars.getUserId());
					obsEmos.setSimId(constVars.getSimId());
					ObservedEmotionsDAO obsEmosDAO = context
							.getBean(ObservedEmotionsDAO.class);
					obsEmosDAO.saveAndFlush(obsEmos);

				}

			}

		}
	}

	private void invokeProspectEmos(String emoToBeInvoked) {
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

		for (EventTab event : events) {
			agentDesire = event.getAgentDesirability();

			// Get the components of event global variables

			eSenseOfReality = event.getEventSenseOfReality();
			ePsychoProximity = event.getEventPsychoProximity();
			eUnexpectedness = event.getEventUnexpectedness();
			eArousal = event.getEventArousal();

			likelihood = event.getEventLikelihood();
		}

		desireSelf = ((AGoalValue + IGoalValue + RGoalValue + agentDesire) + 4) / 8.0;
		eventGlobalVars = ((eSenseOfReality + ePsychoProximity
				+ eUnexpectedness + eArousal) + 4) / 8.0;

		Double emoFactor = (Double) emoAttitudes.get(emoToBeInvoked).get(
				"emotion_factor");

		// Store the hope/fear intensity and potential in DB. Update previous
		// state and observed emotions

		// Invoke the hope/fear emotions below

		if (emoToBeInvoked.equalsIgnoreCase("hope") && (desireSelf > 0)
				&& (likelihood > 0)) {
			Double hopePotential = EmotionFunctions.calcHopePotential(
					emoFactor, Math.abs(desireSelf), likelihood,
					eventGlobalVars);
			Double hopeIntensity = (hopePotential - (Double) emoAttitudes.get(
					emoToBeInvoked).get("threshold"));

			ObservedEmotions obsEmos = new ObservedEmotions();
			obsEmos.setIterNo(currIter);
			obsEmos.setAgentId(agentID);
			obsEmos.setTargetEvent(eventID);
			obsEmos.setTargetNeighbour(-1l);
			obsEmos.setTargetObj(-1l);
			obsEmos.setEmotion(emoToBeInvoked);
			obsEmos.setEmoPot(hopePotential);
			obsEmos.setEmoIntensity(hopeIntensity);
			obsEmos.setUserId(constVars.getUserId());
			obsEmos.setSimId(constVars.getSimId());
			ObservedEmotionsDAO obsEmosDAO = context
					.getBean(ObservedEmotionsDAO.class);
			obsEmosDAO.saveAndFlush(obsEmos);

			// TODO Update previous state

			PreviousState prevState = new PreviousState();
			prevState.setEventId(eventID);
			prevState.setAgentId(agentID);
			prevState.setIterNo(currIter);
			prevState.setEmotion(emoToBeInvoked);
			prevState.setHopePotential(hopePotential);
			prevState.setFearPotential(0.0);
			prevState.setUserId(constVars.getUserId());
			prevState.setSimId(constVars.getSimId());
			PreviousStateDAO prevStateDAO = context.getBean(PreviousStateDAO.class);
			prevStateDAO.saveAndFlush(prevState);
		}

	}

	private void invokeProspectConfirmationEmos(String emoToBeInvoked) {
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

		Double agentBelief = 0.0;

		for (EventTab event : events) {
			agentDesire = event.getAgentDesirability();

			// Get the components of event global variables

			eSenseOfReality = event.getEventSenseOfReality();
			ePsychoProximity = event.getEventPsychoProximity();
			eUnexpectedness = event.getEventUnexpectedness();
			eArousal = event.getEventArousal();

			effort = event.getEventEffort();
			realization = event.getEventRealization();
			agentBelief = event.getAgentBelief();
		}

		String prevEmotion = "hope";
		if (emoToBeInvoked.equalsIgnoreCase("fears-confirmed")
				|| emoToBeInvoked.equalsIgnoreCase("relief")) {
			prevEmotion = "fear";
		}

		PreviousStateDAO prevStateDAO = context.getBean(PreviousStateDAO.class);
		List<PreviousState> prevStates = prevStateDAO
				.getPrevStateByIterNoAgentEventEmotionUserAndSim(
						constVars.getUserId(), constVars.getSimId(),
						(currIter - 1), agentID, eventID, prevEmotion);
		
		int noOfPrevEmosRows = 0;
		
		for(PreviousState prevState:prevStates)
		{
			noOfPrevEmosRows++;
			prevHopePotential = prevState.getHopePotential();
			prevFearPotential = prevState.getFearPotential();
		}
		
		desireSelf = ((AGoalValue + IGoalValue + RGoalValue + agentDesire)+4)/8.0;
		eventGlobalVars = ((eSenseOfReality + ePsychoProximity 
				           + eUnexpectedness + eArousal)+4)/8.0;
		
		Double emoFactor = (Double)emoAttitudes.get(emoToBeInvoked).get("emotion_factor");
		
		if(emoToBeInvoked.equalsIgnoreCase("satisfaction")
				&& (noOfPrevEmosRows > 0)
				&& (desireSelf > 0)
				&& (agentBelief > 0))
		{
			Double satisfactionPotential = EmotionFunctions.calcSatisfactionPotential(emoFactor, prevHopePotential, effort, realization, eventGlobalVars);
			Double satisfactionIntensity = (satisfactionPotential - (Double)emoAttitudes.get(emoToBeInvoked).get("threshold"));
			
			// Update the observed emotions
			ObservedEmotions obsEmos = new ObservedEmotions();
			obsEmos.setIterNo(currIter);
			obsEmos.setAgentId(agentID);
			obsEmos.setTargetEvent(eventID);
			obsEmos.setTargetNeighbour(-1l);
			obsEmos.setTargetObj(-1l);
			obsEmos.setEmotion(emoToBeInvoked);
			obsEmos.setEmoPot(satisfactionPotential);
			obsEmos.setEmoIntensity(satisfactionIntensity);
			obsEmos.setUserId(constVars.getUserId());
			obsEmos.setSimId(constVars.getSimId());
			ObservedEmotionsDAO obsEmosDAO = context
					.getBean(ObservedEmotionsDAO.class);
			obsEmosDAO.saveAndFlush(obsEmos);
			
									
		}
		else if(emoToBeInvoked.equalsIgnoreCase("fears-confirmed")
				&& (noOfPrevEmosRows > 0)
				&& (desireSelf < 0)
				&& (agentBelief > 0))
		{
			Double fearsConfirmedPotential = EmotionFunctions.calcFearsConfPotential(emoFactor, prevFearPotential, effort, realization, eventGlobalVars);
			Double fearsConfirmedIntensity = (fearsConfirmedPotential - (Double)emoAttitudes.get(emoToBeInvoked).get("threshold"));
			
			// Update the observed emotions
			ObservedEmotions obsEmos = new ObservedEmotions();
			obsEmos.setIterNo(currIter);
			obsEmos.setAgentId(agentID);
			obsEmos.setTargetEvent(eventID);
			obsEmos.setTargetNeighbour(-1l);
			obsEmos.setTargetObj(-1l);
			obsEmos.setEmotion(emoToBeInvoked);
			obsEmos.setEmoPot(fearsConfirmedPotential);
			obsEmos.setEmoIntensity(fearsConfirmedIntensity);
			obsEmos.setUserId(constVars.getUserId());
			obsEmos.setSimId(constVars.getSimId());
			ObservedEmotionsDAO obsEmosDAO = context
					.getBean(ObservedEmotionsDAO.class);
			obsEmosDAO.saveAndFlush(obsEmos);
		}
		else if(emoToBeInvoked.equalsIgnoreCase("relief")
				&& (noOfPrevEmosRows > 0)
				&& (desireSelf < 0)
				&& (agentBelief < 0))
		{
			Double reliefPotential = EmotionFunctions.calcReliefPotential(emoFactor, prevHopePotential, effort, realization, eventGlobalVars);
			Double reliefIntensity = (reliefPotential - (Double)emoAttitudes.get(emoToBeInvoked).get("threshold"));
			
			// Update the observed emotions
			ObservedEmotions obsEmos = new ObservedEmotions();
			obsEmos.setIterNo(currIter);
			obsEmos.setAgentId(agentID);
			obsEmos.setTargetEvent(eventID);
			obsEmos.setTargetNeighbour(-1l);
			obsEmos.setTargetObj(-1l);
			obsEmos.setEmotion(emoToBeInvoked);
			obsEmos.setEmoPot(reliefPotential);
			obsEmos.setEmoIntensity(reliefIntensity);
			obsEmos.setUserId(constVars.getUserId());
			obsEmos.setSimId(constVars.getSimId());
			ObservedEmotionsDAO obsEmosDAO = context
					.getBean(ObservedEmotionsDAO.class);
			obsEmosDAO.saveAndFlush(obsEmos);
		}
		else if(emoToBeInvoked.equalsIgnoreCase("disappointment")
				&& (noOfPrevEmosRows > 0)
				&& (desireSelf > 0)
				&& (agentBelief < 0))
		{
			Double disappointmentPotential = EmotionFunctions.calcDisappointmentPotential(emoFactor, prevHopePotential, effort, realization, eventGlobalVars);
			Double disappointmentIntensity = (disappointmentPotential - (Double)emoAttitudes.get(emoToBeInvoked).get("threshold"));
			
			// Update the observed emotions
			ObservedEmotions obsEmos = new ObservedEmotions();
			obsEmos.setIterNo(currIter);
			obsEmos.setAgentId(agentID);
			obsEmos.setTargetEvent(eventID);
			obsEmos.setTargetNeighbour(-1l);
			obsEmos.setTargetObj(-1l);
			obsEmos.setEmotion(emoToBeInvoked);
			obsEmos.setEmoPot(disappointmentPotential);
			obsEmos.setEmoIntensity(disappointmentIntensity);
			obsEmos.setUserId(constVars.getUserId());
			obsEmos.setSimId(constVars.getSimId());
			ObservedEmotionsDAO obsEmosDAO = context
					.getBean(ObservedEmotionsDAO.class);
			obsEmosDAO.saveAndFlush(obsEmos);
		}
		
		PreviousState prevState = new PreviousState();
		prevState.setEventId(eventID);
		prevState.setAgentId(agentID);
		prevState.setIterNo(currIter);
		prevState.setEmotion(emoToBeInvoked);
		prevState.setHopePotential(0.0);
		prevState.setFearPotential(0.0);
		prevState.setUserId(constVars.getUserId());
		prevState.setSimId(constVars.getSimId());
		prevStateDAO.saveAndFlush(prevState);

	}
	
	private void invokeAgentCogUnitEmos(String emoToBeInvoked)
	{
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
				
				// Invoke the pride/self-reproach emotion below
				
				if(emoToBeInvoked.equalsIgnoreCase("pride")
						&& (praiseworthiness > 0)
						&& (cogUnit > 0))
				{
					Double pridePotential = (Double)EmotionFunctions.calcPridePotential(emoFactor, Math.abs(praiseworthiness), expDiff, cogUnit, agentGlobalVars);
					Double prideIntensity = (pridePotential - (Double)emoAttitudes.get(emoToBeInvoked).get("threshold"));
					
					// Update the observed emotions
					ObservedEmotions obsEmos = new ObservedEmotions();
					obsEmos.setIterNo(currIter);
					obsEmos.setAgentId(agentID);
					obsEmos.setTargetEvent(-1l);
					obsEmos.setTargetNeighbour(agentNeighbour);
					obsEmos.setTargetObj(-1l);
					obsEmos.setEmotion(emoToBeInvoked);
					obsEmos.setEmoPot(pridePotential);
					obsEmos.setEmoIntensity(prideIntensity);
					obsEmos.setUserId(constVars.getUserId());
					obsEmos.setSimId(constVars.getSimId());
					ObservedEmotionsDAO obsEmosDAO = context
							.getBean(ObservedEmotionsDAO.class);
					obsEmosDAO.saveAndFlush(obsEmos);
					
				}
				else if(emoToBeInvoked.equalsIgnoreCase("self-reproach")
						&& (praiseworthiness < 0)
						&& (cogUnit > 0))
				{
					Double selfReproachPotential = (Double)EmotionFunctions.calcSelfReproachPotential(emoFactor, Math.abs(praiseworthiness), expDiff, cogUnit, agentGlobalVars);
					Double selfReproachIntensity = (selfReproachPotential - (Double)emoAttitudes.get(emoToBeInvoked).get("threshold"));
					
					// Update the observed emotions
					ObservedEmotions obsEmos = new ObservedEmotions();
					obsEmos.setIterNo(currIter);
					obsEmos.setAgentId(agentID);
					obsEmos.setTargetEvent(-1l);
					obsEmos.setTargetNeighbour(agentNeighbour);
					obsEmos.setTargetObj(-1l);
					obsEmos.setEmotion(emoToBeInvoked);
					obsEmos.setEmoPot(selfReproachPotential);
					obsEmos.setEmoIntensity(selfReproachIntensity);
					obsEmos.setUserId(constVars.getUserId());
					obsEmos.setSimId(constVars.getSimId());
					ObservedEmotionsDAO obsEmosDAO = context
							.getBean(ObservedEmotionsDAO.class);
					obsEmosDAO.saveAndFlush(obsEmos);
				}
				
				// Invoked the pride/self-reproach emotion above
				
			}
					
		}
	}
	
	private void invokeAgentNoCogUnitEmos(String emoToBeInvoked)
	{
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
				
				// Invoke the appreciation/reproach emotion below
				
				if(emoToBeInvoked.equalsIgnoreCase("appreciation")
						&& (praiseworthiness > 0))
				{
					Double appreciationPotential = (Double)EmotionFunctions.calcAppreciationPotential(emoFactor, Math.abs(praiseworthiness), expDiff, agentGlobalVars);
					Double appreciationIntensity = (appreciationPotential - (Double)emoAttitudes.get(emoToBeInvoked).get("threshold"));
					
					// Update the observed emotions
					ObservedEmotions obsEmos = new ObservedEmotions();
					obsEmos.setIterNo(currIter);
					obsEmos.setAgentId(agentID);
					obsEmos.setTargetEvent(-1l);
					obsEmos.setTargetNeighbour(agentNeighbour);
					obsEmos.setTargetObj(-1l);
					obsEmos.setEmotion(emoToBeInvoked);
					obsEmos.setEmoPot(appreciationPotential);
					obsEmos.setEmoIntensity(appreciationIntensity);
					obsEmos.setUserId(constVars.getUserId());
					obsEmos.setSimId(constVars.getSimId());
					ObservedEmotionsDAO obsEmosDAO = context
							.getBean(ObservedEmotionsDAO.class);
					obsEmosDAO.saveAndFlush(obsEmos);
				}
				else if(emoToBeInvoked.equalsIgnoreCase("reproach")
						&& (praiseworthiness < 0))
				{
					Double reproachPotential = (Double)EmotionFunctions.calcReproachPotential(emoFactor, Math.abs(praiseworthiness), expDiff, agentGlobalVars);
					Double reproachIntensity = (reproachPotential - (Double)emoAttitudes.get(emoToBeInvoked).get("threshold"));
					
					// Update the observed emotions
					ObservedEmotions obsEmos = new ObservedEmotions();
					obsEmos.setIterNo(currIter);
					obsEmos.setAgentId(agentID);
					obsEmos.setTargetEvent(-1l);
					obsEmos.setTargetNeighbour(agentNeighbour);
					obsEmos.setTargetObj(-1l);
					obsEmos.setEmotion(emoToBeInvoked);
					obsEmos.setEmoPot(reproachPotential);
					obsEmos.setEmoIntensity(reproachIntensity);
					obsEmos.setUserId(constVars.getUserId());
					obsEmos.setSimId(constVars.getSimId());
					ObservedEmotionsDAO obsEmosDAO = context
							.getBean(ObservedEmotionsDAO.class);
					obsEmosDAO.saveAndFlush(obsEmos);
				}
				
				
				// Invoked the appreciation/reproach emotion above
				
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
