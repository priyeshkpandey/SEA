package com.services.psychological.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import java.lang.reflect.Method;
import java.sql.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class IndividualAgent {
	private int agentID;         // used but not assigned a value
	private int eventID;		 // used but not assigned a value
	private int objectID;		 // used but not assigned a value
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
	private HashMap<Integer, HashMap<String, Object>> neighbours 
	             = new HashMap<Integer, HashMap<String, Object>>();  
	private Long simulation_id;
	private int proposedIter;
	private int workingIter;
	private int maxIter;
	private int currIter;
	private ArrayList<String> triggeredEmos;
	private HashMap<String, Integer> triggeredEmosPrecedence = new HashMap<String, Integer>();
	private HashMap<String, HashMap<String, Object>> emoAttitudes = new HashMap<String, HashMap<String, Object>>();
	
	private ModelVariables constVars;
	
	@Autowired
	@Qualifier("psycheConfig")
	private Properties psycheConfig;
	
	IndividualAgent(Long simId, int agentId, int eventId, int objId, ModelVariables constVars)
	{
		agentID = agentId;
		eventID = eventId;
		objectID = objId;
		simulation_id = simId;
		isAgent = false;
		isEvent = false;
		isObject = false;
		this.constVars = constVars;
	}
	
	public void agentPerception()
	{
		triggeredEmos = new ArrayList<String>();
		Connection conn = null;
		//System.out.println("Inside agentPerception!");
		try
		{
			Class.forName(constVars.getDBDriver()).newInstance();
			conn = DriverManager.getConnection(constVars.getDBConnection(),
					constVars.getDBUserName(), constVars.getDBPassword());
			
			// [1] Logic between this starting and ending comments
			
			
			// *** Simulation Data table query execution ***
			String simulationQuery = constVars.querySimDataBySimID();
			PreparedStatement psSimDataQuery = conn.prepareStatement(simulationQuery);
			psSimDataQuery.setLong(1, simulation_id);
			
			ResultSet rsSimData = psSimDataQuery.executeQuery();
			boolean isSimExists = rsSimData.first();
			if(isSimExists && 
			   rsSimData.isFirst() && 
			   rsSimData.isLast())
			{	
			   currIter = rsSimData.getInt("current_iteration");
			   
			   if(currIter == 1)
			   {
				   proposedIter = rsSimData.getInt("proposed_iteration");
                   workingIter = proposedIter;
                   maxIter = rsSimData.getInt("max_iteration");
			   }
			   else
			   {
				  workingIter = rsSimData.getInt("working_iteration"); 
			   }
			   
			   // *** Closing Simulation Data table query ***
			   psSimDataQuery.close();
			   rsSimData.close();
			 
			   
			// *** Start of block if currIter <= workingIter
			 if(currIter<=workingIter)
			 {
			   // *** Emotional Attitudes table query execution ***
			   String emoAttQuery =  constVars.queryEmoAttByAgIDIter();
			   PreparedStatement psEmoAttQuery = conn.prepareStatement(emoAttQuery);
			   psEmoAttQuery.setInt(1, currIter);
			   psEmoAttQuery.setInt(2, agentID);
			   
			   ResultSet rsEmoAtt = psEmoAttQuery.executeQuery();
			   
			   
			   // *** Start of collecting emotional attitudes ***
			   while(rsEmoAtt.next())
			   {
				   String emo = rsEmoAtt.getString("emotions");
				   HashMap<String, Object> emoAtt = new HashMap<String, Object>();
				   emoAtt.put("probability", rsEmoAtt.getDouble("probability"));
				   emoAtt.put("precedence", rsEmoAtt.getInt("precedence"));
				   emoAtt.put("threshold", rsEmoAtt.getDouble("threshold"));
				   emoAtt.put("weight", rsEmoAtt.getDouble("weight"));
				   emoAtt.put("priority", rsEmoAtt.getInt("priority"));
				   emoAtt.put("emotion_factor", rsEmoAtt.getDouble("emotion_factor"));
				   emoAtt.put("occurrence_count", rsEmoAtt.getInt("occurrence_count"));
				   emoAtt.put("occurred_count", rsEmoAtt.getInt("occurred_count"));
				   emoAttitudes.put(emo, emoAtt);
			   }
			// *** End of collecting emotional attitudes ***
			   
			   // *** Closing the Emotional Attitudes table query ***
			   psEmoAttQuery.close();
			   rsEmoAtt.close();
			   
			   Set<String> emoAttKeySet = emoAttitudes.keySet();
			   Iterator<String> emoIter = emoAttKeySet.iterator();
			   int tempWorkingCount = 0;
			   
			   int noOfEmos = emoAttKeySet.size();
			   int noOfEmosToInvoke = 0;
			   //System.out.println("Max emotions to invoke-->"+noOfEmosToInvoke);
			   
			   // *** Check if a model exists for No of Emotions to Invoke ***
			   
			  
			   PreparedStatement psAgentTable = conn.prepareStatement(constVars.queryAgentTable());
			   psAgentTable.setInt(1, currIter);
			   psAgentTable.setInt(2, agentID);
			  
			   
			   ResultSet rsAgentTable = psAgentTable.executeQuery();
			  
			   
			   if(rsAgentTable.isBeforeFirst())
			   {
				  
			       while(rsAgentTable.next())
			       {
			    	   
				     noOfEmosToInvoke = rsAgentTable.getInt("no_of_emos_to_invoke");
				   
			       }
			   }
			   else
			   {
				   System.out.println("No Models for invoked emotions");
				   noOfEmosToInvoke = (int)Math.round(noOfEmos*Math.random());  
				   System.out.print(" "+noOfEmosToInvoke);
			   }
			   
			   psAgentTable.close();
			   rsAgentTable.close();
			   
			   // *** Checked the model for No of Emotions to Invoke ***
			   
			   TreeMap<Integer, String> sortedEmoOccurCount = new TreeMap<Integer,String>();
			   
			   // *** Start of iteration on emotional attitudes - Iteration 1 ***
			   
			   while(emoIter.hasNext())
			   {
				   String emotion = emoIter.next();
				   if(psycheConfig.getProperty("EVENT_EMOTIONS").contains(emotion.trim().toLowerCase()))
				   {
					 isEvent = true;   
				   }
				   
				   if(psycheConfig.getProperty("AGENT_EMOTIONS").contains(emotion.trim().toLowerCase()))
				   {
					 isAgent = true;   
				   }
				   
				   if(psycheConfig.getProperty("OBJECT_EMOTIONS").contains(emotion.trim().toLowerCase()))
				   {
					 isObject = true;   
				   }
				   
				   if(psycheConfig.getProperty("EVENT_AGENT_EMOTIONS").contains(emotion.trim().toLowerCase()))
				   {
					 isEvent = true; 
					 isAgent = true;
				   }
				   
				   Double emoProb =  (Double)emoAttitudes.get(emotion).get("probability");
				   Integer emoCount = (int) Math.round(workingIter*emoProb);
				   tempWorkingCount+=emoCount;
				   emoAttitudes.get(emotion).put("occurrence_count", emoCount);
				   
				   if(sortedEmoOccurCount.containsKey(emoCount))
				   {
					   String prevEmo = sortedEmoOccurCount.get(emoCount);
					   sortedEmoOccurCount.put(emoCount, prevEmo+","+emotion);
				   }
				   else
				   {
				       sortedEmoOccurCount.put(emoCount, emotion);
				   }
			   }
			   // *** End of iteration on emotional attitudes - Iteration 1 ***
			   
			   Iterator<Integer> sortedEmoIter = sortedEmoOccurCount.descendingKeySet().iterator();
			   
			   
			   int invokeNoEmos = 0;
			   
			   
			   // *** Start of iteration on sorted emotional attitudes - Iteration 1 ***
			   
			   while(sortedEmoIter.hasNext())
			   {
				   Integer emoCnt = sortedEmoIter.next();
				   String emotions = sortedEmoOccurCount.get(emoCnt);
				   int emosOccur = 0;
				   int calcOccur = 0;
				   String tempEmo = "";
				   
				   
				  if(invokeNoEmos < noOfEmosToInvoke)
				  {// ---- **** Outermost IF **** ----
				   if(emotions.contains(","))
				   {
					   int incCount = emotions.split(",").length;
					   
					   if(incCount > (noOfEmosToInvoke-invokeNoEmos-1))
					   {
						   for(int i = 0;i<(noOfEmosToInvoke-invokeNoEmos);i++)
						   {
							   tempEmo = emotions.split(",")[i];
							   
							   emosOccur++;
							   calcOccur = (Integer)emoAttitudes.get(tempEmo).get("occurrence_count");
							   emosOccur += (Integer)emoAttitudes.get(tempEmo).get("occurred_count");
							   emoAttitudes.get(tempEmo).put("occurred_count", emosOccur);
							   
							   if(calcOccur >= emosOccur)
							   {
								   triggeredEmos.add(tempEmo);
							       emoAttitudes.get(tempEmo).put("occurrence_count", (calcOccur - emosOccur));
							       triggeredEmosPrecedence.put(tempEmo, (Integer)emoAttitudes.get(tempEmo).get("precedence"));
							   }
							   else
							   {
								   emoAttitudes.get(tempEmo).put("occurrence_count", 0); 
							   }
							   calcOccur = 0;
							   tempEmo = null;
							   emosOccur = 0;
						   }
						   break;
					   }
					   else
					   {
						   for(int i = 0;i<incCount;i++)
						   {
							   tempEmo = emotions.split(",")[i];
							   
							   emosOccur++;
							   calcOccur = (Integer)emoAttitudes.get(tempEmo).get("occurrence_count");
							   emosOccur += (Integer)emoAttitudes.get(tempEmo).get("occurred_count");
							   emoAttitudes.get(tempEmo).put("occurred_count", emosOccur);
							   
							   if(calcOccur >= emosOccur)
							   {
								   triggeredEmos.add(tempEmo);
							       emoAttitudes.get(tempEmo).put("occurrence_count", (calcOccur - emosOccur));
							       triggeredEmosPrecedence.put(tempEmo, (Integer)emoAttitudes.get(tempEmo).get("precedence"));
							   }
							   else
							   {
								   emoAttitudes.get(tempEmo).put("occurrence_count", 0); 
							   }
							   calcOccur = 0;
							   tempEmo = null;
							   emosOccur = 0;
						   } 
						   
						   invokeNoEmos+=(incCount-1);
					   }
				   }
				   else
				   {
					   
					   emosOccur++;
					   calcOccur = (Integer)emoAttitudes.get(emotions).get("occurrence_count");
					   emosOccur += (Integer)emoAttitudes.get(emotions).get("occurred_count");
					   emoAttitudes.get(emotions).put("occurred_count", emosOccur);
					   
					   if(calcOccur >= emosOccur)
					   {
						   triggeredEmos.add(emotions);
					       emoAttitudes.get(emotions).put("occurrence_count", (calcOccur - emosOccur));
					       triggeredEmosPrecedence.put(emotions, (Integer)emoAttitudes.get(emotions).get("precedence"));
					   }
					   else
					   {
						   emoAttitudes.get(emotions).put("occurrence_count", 0); 
					   }
					   calcOccur = 0;
					   tempEmo = null;
					   emosOccur = 0;
					   
					   invokeNoEmos++;
				   }
				   
				   
				  }// ---- **** Outermost IF **** ----
				  else
				  {
					 if(emotions.contains(","))
					 {
						 int noCombEmos = emotions.split(",").length;
						 
						 for(int i = 0;i < noCombEmos;i++)
						 {
							 String tmpEmo = emotions.split(",")[i];
							 calcOccur = (Integer)emoAttitudes.get(tmpEmo).get("occurrence_count"); 
							 emosOccur = (Integer)emoAttitudes.get(tmpEmo).get("occurred_count");
							 emoAttitudes.get(tmpEmo).put("occurrence_count", (calcOccur - emosOccur));
						 }
						 
					 }
					 else
					 {
						 calcOccur = (Integer)emoAttitudes.get(emotions).get("occurrence_count"); 
						 emosOccur = (Integer)emoAttitudes.get(emotions).get("occurred_count");
						 emoAttitudes.get(emotions).put("occurrence_count", (calcOccur - emosOccur));
					 }
				  }
			   }
			   // *** End of iteration on sorted emotional attitudes - Iteration 1 ***
			   
			   if(tempWorkingCount < maxIter)
			   {
			      workingIter = tempWorkingCount;
			   }
			   else
			   {
				   workingIter = maxIter;
			   }
			   
			   // *** Start of DB updates of changed variables ***
			   
			   // *** Updating Working Iteration ***
			   
			   String updSimDataTabWrkItr = constVars.updSimDataWrkItrBySimID();
			   PreparedStatement psUpdWrkItr = conn.prepareStatement(updSimDataTabWrkItr);
			   psUpdWrkItr.setInt(1, workingIter);
			   psUpdWrkItr.setLong(2, simulation_id);
			   int rowsAffected = psUpdWrkItr.executeUpdate();
			   
			   if(rowsAffected > 0)
			   {
				   System.out.println("Working Iteration updated! No. of rows-->"+rowsAffected);
			   }
			   
			   psUpdWrkItr.close();
			   
			   // *** Working Iteration updated ***
			   

			   
			   // *** Updating Occurrence Count for emotions ***
			   emoIter = emoAttKeySet.iterator();
			   
			   while(emoIter.hasNext())
			   {
				 String emotion = emoIter.next();
				 Integer emoOccurCount = (Integer)emoAttitudes.get(emotion).get("occurrence_count");
				 Integer emoOccurredCount = (Integer)emoAttitudes.get(emotion).get("occurred_count");
				 String updOccurCount = constVars.updEmoAttOccurCount();
				 PreparedStatement psUpdEmoAttOccurCount = conn.prepareStatement(updOccurCount);
				 psUpdEmoAttOccurCount.setInt(1, emoOccurCount);
				 psUpdEmoAttOccurCount.setInt(2, emoOccurredCount);
				 psUpdEmoAttOccurCount.setString(3, emotion);
				 psUpdEmoAttOccurCount.setInt(4, currIter);
				 psUpdEmoAttOccurCount.setInt(5, agentID);
				 
				 rowsAffected = 0;
				 rowsAffected = psUpdEmoAttOccurCount.executeUpdate();
				 
				 if(rowsAffected > 0)
				 {
					System.out.println("Occurrence count updated for emotion "+emotion+"! No. of rows-->"+rowsAffected); 
				 }
				 
				 psUpdEmoAttOccurCount.close();
			   }
			   
			   
			   // *** Occurrence Count for emotions updated ***
			   
			   
			   // *** Start updating agent perception values
			   
			   String updAgentPercept = constVars.updAgentPerceptions();
			   PreparedStatement psUpdAgentPercept = conn.prepareStatement(updAgentPercept);
			   psUpdAgentPercept.setBoolean(1, isEvent);
			   psUpdAgentPercept.setBoolean(2, isAgent);
			   psUpdAgentPercept.setBoolean(3, isObject);
			   psUpdAgentPercept.setInt(4, agentID);
			   psUpdAgentPercept.setInt(5, currIter);
			   
			   rowsAffected = 0;
			   rowsAffected = psUpdAgentPercept.executeUpdate();
			   
			   if(rowsAffected > 0)
			   {
				   System.out.println("Agent perceptions updated! No. of rows-->"+rowsAffected);
			   }
			   
			   psUpdAgentPercept.close();
			   
			   // *** End updating agent perception values
			}
			else
			{
				System.err.println("Multiple simulations with same ID exists or no simulation exists!");
			}
		
			
			} // ** End of block if currIter <= workingIter
			else
			{
				System.out.println("Simulation completes");
			}
			
			// [1] Logic completes
			
			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
			   if(conn != null && (!conn.isClosed()))
			   {
		         conn.close();
			   }
			}
			catch(Exception e)
			{
				System.err.println("SQL connection cannot be closed!");
			}
		}
		
	
		//return perceivedEmos;
	}
	
	public ArrayList<String> getTriggeredEmos()
	{
		return triggeredEmos;
	}
	
	public LinkedHashMap<String, Integer> getEmosSortedByPrecedence()
	{
		List <Entry<String, Integer>> sortedList = new LinkedList<Entry<String, Integer>>(triggeredEmosPrecedence.entrySet());
		Collections.sort(sortedList, new Comparator<Entry<String, Integer>>(){
			public int compare(Entry<String, Integer>o1,
					Entry<String, Integer>o2)
			{
				return o1.getValue().compareTo(o2.getValue());
			}
			
		   }
		);
		
		LinkedHashMap<String, Integer> sortedByPrecedence = new LinkedHashMap<String, Integer>();
		for(Entry<String, Integer> sortedEntries:sortedList)
		{
			sortedByPrecedence.put(sortedEntries.getKey(), sortedEntries.getValue());
		}
		return sortedByPrecedence;
	}
	
	public void collectDataAndInvoke()
	{
		LinkedHashMap<String, Integer> trigEmosByPrecedence = getEmosSortedByPrecedence();
		Iterator<String> iterEmosByPrecedence = trigEmosByPrecedence.keySet().iterator();
		Connection conn = null;
		
		try{
			
			Class.forName(constVars.getDBDriver()).newInstance();
			conn = DriverManager.getConnection(constVars.getDBConnection(),
					constVars.getDBUserName(), constVars.getDBPassword());
			
		// ************************* Get data for agent neighbours *****************************	
			
			PreparedStatement psQueryAgentNeighbours = conn.prepareStatement(constVars.queryAgentNeighbours());
			psQueryAgentNeighbours.setInt(1, currIter);
			psQueryAgentNeighbours.setInt(2, agentID);
			psQueryAgentNeighbours.setInt(3, eventID);
			
			ResultSet rsAgentNeighbours = psQueryAgentNeighbours.executeQuery();
			
			while(rsAgentNeighbours.next())
			{
				// Read all the data from the agent_network
				HashMap<String, Object> agentNeighbours = new HashMap<String, Object>();
				Integer nAgentID = rsAgentNeighbours.getInt("agent_id2");
				agentNeighbours.put("event_involved", rsAgentNeighbours.getInt("event_involved"));
				agentNeighbours.put("other_desirability", rsAgentNeighbours.getDouble("other_desirability"));
				agentNeighbours.put("deservingness", rsAgentNeighbours.getDouble("deservingness"));
				agentNeighbours.put("cognitive_unit_strength", rsAgentNeighbours.getDouble("cognitive_unit_strength"));
				agentNeighbours.put("expectation_deviation", rsAgentNeighbours.getDouble("expectation_deviation"));
				agentNeighbours.put("other_liking", rsAgentNeighbours.getDouble("other_liking"));
				agentNeighbours.put("praiseworthiness", rsAgentNeighbours.getDouble("praiseworthiness"));
				agentNeighbours.put("agent_sense_of_reality", rsAgentNeighbours.getDouble("agent_sense_of_reality"));
				agentNeighbours.put("agent_psycho_proximity", rsAgentNeighbours.getDouble("agent_psycho_proximity"));
				agentNeighbours.put("agent_unexpectedness", rsAgentNeighbours.getDouble("agent_unexpectedness"));
				agentNeighbours.put("agent_arousal", rsAgentNeighbours.getDouble("agent_arousal"));
				agentNeighbours.put("emotion_target", rsAgentNeighbours.getBoolean("emotion_target"));
				neighbours.put(nAgentID, agentNeighbours);
			}
			
			rsAgentNeighbours.close();
			psQueryAgentNeighbours.close();
			
		// **************************** Got data for agent neighbours *********************************************
		
		while(iterEmosByPrecedence.hasNext())
		{
			String emoToBeInvoked = iterEmosByPrecedence.next();
			
			if(        emoToBeInvoked.equalsIgnoreCase("joy")
					|| emoToBeInvoked.equalsIgnoreCase("distress"))
			{
				
				invokeDesirabilityEmos(emoToBeInvoked, conn);
								
			}
			else if(   emoToBeInvoked.equalsIgnoreCase("happy-for")
					|| emoToBeInvoked.equalsIgnoreCase("sorry-for")
					|| emoToBeInvoked.equalsIgnoreCase("resentment")
					|| emoToBeInvoked.equalsIgnoreCase("gloating"))
			{
				
				invokeFortunesOfOthersEmos(emoToBeInvoked, conn);
				
			}
			else if(   emoToBeInvoked.equalsIgnoreCase("hope")
					|| emoToBeInvoked.equalsIgnoreCase("fear"))
			{
				
				invokeProspectEmos(emoToBeInvoked, conn);
				
			}
			else if((currIter > 1)
					&& 
					   (   emoToBeInvoked.equalsIgnoreCase("satisfaction")
						|| emoToBeInvoked.equalsIgnoreCase("fears-confirmed")
						|| emoToBeInvoked.equalsIgnoreCase("relief")
						|| emoToBeInvoked.equalsIgnoreCase("disappointment")
							)
					)
			{
				
				invokeProspectConfirmationEmos(emoToBeInvoked, conn);
				
			}
			else if(   emoToBeInvoked.equalsIgnoreCase("pride")
					|| emoToBeInvoked.equalsIgnoreCase("self-reproach"))
			{
				
				invokeAgentCogUnitEmos(emoToBeInvoked, conn);
				
			}
			else if(   emoToBeInvoked.equalsIgnoreCase("appreciation")
					|| emoToBeInvoked.equalsIgnoreCase("reproach"))
			{
				
				invokeAgentNoCogUnitEmos(emoToBeInvoked, conn);
				
			}
			else if(   emoToBeInvoked.equalsIgnoreCase("gratitude")
					|| emoToBeInvoked.equalsIgnoreCase("anger"))
			{
				
				invokeEventAgentNoCogUnitEmos(emoToBeInvoked, conn);
				
			}
			else if(   emoToBeInvoked.equalsIgnoreCase("gratification")
					|| emoToBeInvoked.equalsIgnoreCase("remorse"))
			{
				
				invokeEventAgentCogUnitEmos(emoToBeInvoked, conn);
				
			}
			else if(emoToBeInvoked.equalsIgnoreCase("liking")
					|| emoToBeInvoked.equalsIgnoreCase("disliking"))
			{
				
				invokeObjectEmos(emoToBeInvoked, conn);
			}
			else
			{
				System.err.println("Not a valid emotion for this iteration.");
			}
			
				
		}
		
		
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
			   if(conn != null && (!conn.isClosed()))
			   {
		         conn.close();
			   }
			}
			catch(Exception e)
			{
				System.err.println("SQL connection cannot be closed!");
			}
		}
	}

	private void invokeDesirabilityEmos(String emoToBeInvoked, Connection conn) throws Exception
	{
		// Get the AGoal, IGoal and RGoal values
		
		String queryAgentTab = constVars.queryAgentTable();
		PreparedStatement psAgentTab = conn.prepareStatement(queryAgentTab);
		psAgentTab.setInt(1, currIter);
		psAgentTab.setInt(2, agentID);
		
		
		ResultSet rsAgentTab = psAgentTab.executeQuery();
		
		while(rsAgentTab.next())
		{
			AGoalValue = rsAgentTab.getDouble("agoal_value");
			IGoalValue = rsAgentTab.getDouble("igoal_value");
			RGoalValue = rsAgentTab.getDouble("rgoal_value");
		}
		rsAgentTab.close();
		psAgentTab.close();
		
		
		// Get the agent_desirability from event_tab add to goal values
		PreparedStatement psQueryEventTab = conn.prepareStatement(constVars.queryEventTab());
		psQueryEventTab.setInt(1, currIter);
		psQueryEventTab.setInt(2, eventID);
		psQueryEventTab.setInt(3, agentID);
		
		ResultSet rsEventTab = psQueryEventTab.executeQuery();
		
		Double agentDesire = 0.0;
		
		Double eSenseOfReality = 0.0;
		Double ePsychoProximity = 0.0;
		Double eUnexpectedness = 0.0;
		Double eArousal = 0.0;
		
		while(rsEventTab.next())
		{
			agentDesire = rsEventTab.getDouble("agent_desirability");
			
			// Get the components of event global variables
			
			eSenseOfReality = rsEventTab.getDouble("event_sense_of_reality");
			ePsychoProximity = rsEventTab.getDouble("event_psycho_proximity");
			eUnexpectedness = rsEventTab.getDouble("event_unexpectedness");
			eArousal = rsEventTab.getDouble("event_arousal");
			
		}
		
		rsEventTab.close();
		psQueryEventTab.close();
		
		desireSelf = ((AGoalValue + IGoalValue + RGoalValue + agentDesire)+4)/8.0;
		eventGlobalVars = ((eSenseOfReality + ePsychoProximity 
				           + eUnexpectedness + eArousal)+4)/8.0;
		
		Double emoFactor = (Double)emoAttitudes.get(emoToBeInvoked).get("emotion_factor");
		
		// Invoke the joy/distress emotions below
		
		if(emoToBeInvoked.equalsIgnoreCase("joy")
				&& (desireSelf > 0))
		{
			
				Double joyPotential = EmotionFunctions.calcJoyPotential(emoFactor, Math.abs(desireSelf), eventGlobalVars);
				Double joyIntensity = (joyPotential	- (Double)emoAttitudes.get(emoToBeInvoked).get("threshold"));
				
				
					// Update the observed emotions
					PreparedStatement psInsObservedEmos = conn.prepareStatement(constVars.insObservedEmos());
					psInsObservedEmos.setInt(1, currIter);
					psInsObservedEmos.setInt(2, agentID);
					psInsObservedEmos.setInt(3, eventID);
					psInsObservedEmos.setInt(4, -1);
					psInsObservedEmos.setInt(5, -1);
					psInsObservedEmos.setString(6, emoToBeInvoked);
					psInsObservedEmos.setDouble(7, joyPotential);
					psInsObservedEmos.setDouble(8, joyIntensity);
					
					int rowsAffected = psInsObservedEmos.executeUpdate();
					
					if(rowsAffected > 0)
					{
						System.out.println("No. of rows inserted-->"+rowsAffected);
						
					}
					
					psInsObservedEmos.close();
					
				
			
		}
		else if(emoToBeInvoked.equalsIgnoreCase("distress")
				&& (desireSelf < 0))
		{
			
				Double distressPotential = EmotionFunctions.calcDistressPotential(emoFactor, Math.abs(desireSelf), eventGlobalVars);
				Double distressIntensity = (distressPotential - (Double)emoAttitudes.get(emoToBeInvoked).get("threshold"));
				
				
					// Update the observed emotions
					PreparedStatement psInsObservedEmos = conn.prepareStatement(constVars.insObservedEmos());
					psInsObservedEmos.setInt(1, currIter);
					psInsObservedEmos.setInt(2, agentID);
					psInsObservedEmos.setInt(3, eventID);
					psInsObservedEmos.setInt(4, -1);
					psInsObservedEmos.setInt(5, -1);
					psInsObservedEmos.setString(6, emoToBeInvoked);
					psInsObservedEmos.setDouble(7, distressPotential);
					psInsObservedEmos.setDouble(8, distressIntensity);
					
					int rowsAffected = psInsObservedEmos.executeUpdate();
					
					if(rowsAffected > 0)
					{
						System.out.println("No. of rows inserted-->"+rowsAffected);
						
					}
					
					psInsObservedEmos.close();
				
			
		}
		
		
		// Invoked the joy/distress emotions above
	}
	
	private void invokeFortunesOfOthersEmos(String emoToBeInvoked, Connection conn) throws Exception
	{
		// Get the AGoal, IGoal and RGoal values
		
		String queryAgentTab = constVars.queryAgentTable();
		PreparedStatement psAgentTab = conn.prepareStatement(queryAgentTab);
		psAgentTab.setInt(1, currIter);
		psAgentTab.setInt(2, agentID);
		
		
		ResultSet rsAgentTab = psAgentTab.executeQuery();
		
		while(rsAgentTab.next())
		{
			AGoalValue = rsAgentTab.getDouble("agoal_value");
			IGoalValue = rsAgentTab.getDouble("igoal_value");
			RGoalValue = rsAgentTab.getDouble("rgoal_value");
		}
		rsAgentTab.close();
		psAgentTab.close();
		
		
		// Get the agent_desirability from event_tab add to goal values
		PreparedStatement psQueryEventTab = conn.prepareStatement(constVars.queryEventTab());
		psQueryEventTab.setInt(1, currIter);
		psQueryEventTab.setInt(2, eventID);
		psQueryEventTab.setInt(3, agentID);
		
		ResultSet rsEventTab = psQueryEventTab.executeQuery();
		
		Double agentDesire = 0.0;
		
		Double eSenseOfReality = 0.0;
		Double ePsychoProximity = 0.0;
		Double eUnexpectedness = 0.0;
		Double eArousal = 0.0;
		
		while(rsEventTab.next())
		{
			agentDesire = rsEventTab.getDouble("agent_desirability");
			
			// Get the components of event global variables
			eSenseOfReality = rsEventTab.getDouble("event_sense_of_reality");
			ePsychoProximity = rsEventTab.getDouble("event_psycho_proximity");
			eUnexpectedness = rsEventTab.getDouble("event_unexpectedness");
			eArousal = rsEventTab.getDouble("event_arousal");
			
		}
		
		rsEventTab.close();
		psQueryEventTab.close();
		
		desireSelf = ((AGoalValue + IGoalValue + RGoalValue + agentDesire)+4)/8.0;
		eventGlobalVars = ((eSenseOfReality + ePsychoProximity 
				           + eUnexpectedness + eArousal)+4)/8.0;
		
		Double emoFactor = (Double)emoAttitudes.get(emoToBeInvoked).get("emotion_factor");
		
		
		// Get the variables for neighbours who are target of the emotion
		
		Iterator<Integer> iterNeighbours = neighbours.keySet().iterator();
		
		while(iterNeighbours.hasNext())
		{
			Integer agentNeighbour = iterNeighbours.next();
			
			if((Boolean)neighbours.get(agentNeighbour).get("emotion_target"))
			{
				Double desireOther = (Double)neighbours.get(agentNeighbour).get("other_desirability");
				Double deservingness = (Double)neighbours.get(agentNeighbour).get("deservingness");
				Double likingOther = (Double)neighbours.get(agentNeighbour).get("other_liking");
				
				// Invoke the fortunes of others emotion below
				
				if(emoToBeInvoked.equalsIgnoreCase("happy-for")
						&& (desireSelf > 0) 
								&& (desireOther > 0))
				{
					
						Double happyForPotential = EmotionFunctions.calcHappyForPotential(emoFactor, Math.abs(desireSelf), 
								                        Math.abs(desireOther), deservingness, likingOther, eventGlobalVars);
						Double happyForIntensity = (happyForPotential - (Double)emoAttitudes.get(emoToBeInvoked).get("threshold"));
						
						// Update the observed emotions
						PreparedStatement psInsObservedEmos = conn.prepareStatement(constVars.insObservedEmos());
						psInsObservedEmos.setInt(1, currIter);
						psInsObservedEmos.setInt(2, agentID);
						psInsObservedEmos.setInt(3, eventID);
						psInsObservedEmos.setInt(4, -1);
						psInsObservedEmos.setInt(5, -1);
						psInsObservedEmos.setString(6, emoToBeInvoked);
						psInsObservedEmos.setDouble(7, happyForPotential);
						psInsObservedEmos.setDouble(8, happyForIntensity);
						
						int rowsAffected = psInsObservedEmos.executeUpdate();
						
						if(rowsAffected > 0)
						{
							System.out.println("No. of rows inserted-->"+rowsAffected);
							
						}
						
						psInsObservedEmos.close();
					
				}
				else if(emoToBeInvoked.equalsIgnoreCase("sorry-for")
						&& (desireSelf < 0) 
								&& (desireOther < 0))
				{
					
							Double sorryForPotential = EmotionFunctions.calcSorryForPotential(emoFactor, Math.abs(desireSelf), 
		                                                    Math.abs(desireOther), deservingness, likingOther, eventGlobalVars);
							Double sorryForIntensity = (sorryForPotential - (Double)emoAttitudes.get(emoToBeInvoked).get("threshold"));

							// Update the observed emotions
							PreparedStatement psInsObservedEmos = conn.prepareStatement(constVars.insObservedEmos());
							psInsObservedEmos.setInt(1, currIter);
							psInsObservedEmos.setInt(2, agentID);
							psInsObservedEmos.setInt(3, eventID);
							psInsObservedEmos.setInt(4, -1);
							psInsObservedEmos.setInt(5, -1);
							psInsObservedEmos.setString(6, emoToBeInvoked);
							psInsObservedEmos.setDouble(7, sorryForPotential);
							psInsObservedEmos.setDouble(8, sorryForIntensity);

							int rowsAffected = psInsObservedEmos.executeUpdate();

							if(rowsAffected > 0)
							{
								System.out.println("No. of rows inserted-->"+rowsAffected);
	
							}

							psInsObservedEmos.close();
						
				}
				else if(emoToBeInvoked.equalsIgnoreCase("resentment")
						&& (desireSelf < 0) 
								&& (desireOther > 0))
				{
					
							Double resentmentPotential = EmotionFunctions.calcResentmentPotential(emoFactor, Math.abs(desireSelf), 
									Math.abs(desireOther), deservingness, likingOther, eventGlobalVars);
							Double resentmentIntensity = (resentmentPotential - (Double)emoAttitudes.get(emoToBeInvoked).get("threshold"));

							// Update the observed emotions
							PreparedStatement psInsObservedEmos = conn.prepareStatement(constVars.insObservedEmos());
							psInsObservedEmos.setInt(1, currIter);
							psInsObservedEmos.setInt(2, agentID);
							psInsObservedEmos.setInt(3, eventID);
							psInsObservedEmos.setInt(4, -1);
							psInsObservedEmos.setInt(5, -1);
							psInsObservedEmos.setString(6, emoToBeInvoked);
							psInsObservedEmos.setDouble(7, resentmentPotential);
							psInsObservedEmos.setDouble(8, resentmentIntensity);

							int rowsAffected = psInsObservedEmos.executeUpdate();

							if(rowsAffected > 0)
							{
								System.out.println("No. of rows inserted-->"+rowsAffected);

							}

							psInsObservedEmos.close();
						
				}
				else if(emoToBeInvoked.equalsIgnoreCase("gloating")
						&& (desireSelf > 0) 
								&& (desireOther < 0))
				{
					
						Double gloatingPotential = EmotionFunctions.calcGloatingPotential(emoFactor, Math.abs(desireSelf), 
								Math.abs(desireOther), deservingness, likingOther, eventGlobalVars);
						Double gloatingIntensity = (gloatingPotential - (Double)emoAttitudes.get(emoToBeInvoked).get("threshold"));

						// Update the observed emotions
						PreparedStatement psInsObservedEmos = conn.prepareStatement(constVars.insObservedEmos());
						psInsObservedEmos.setInt(1, currIter);
						psInsObservedEmos.setInt(2, agentID);
						psInsObservedEmos.setInt(3, eventID);
						psInsObservedEmos.setInt(4, -1);
						psInsObservedEmos.setInt(5, -1);
						psInsObservedEmos.setString(6, emoToBeInvoked);
						psInsObservedEmos.setDouble(7, gloatingPotential);
						psInsObservedEmos.setDouble(8, gloatingIntensity);

						int rowsAffected = psInsObservedEmos.executeUpdate();

						if(rowsAffected > 0)
						{
							System.out.println("No. of rows inserted-->"+rowsAffected);

						}

						psInsObservedEmos.close();
						
				}
				
				
				// Invoked the fortunes of others emotion above
				
			}
					
		}
		
		
	}

    private void invokeProspectEmos(String emoToBeInvoked, Connection conn) throws Exception
    {
    	// Get the AGoal, IGoal and RGoal values
		
		String queryAgentTab = constVars.queryAgentTable();
		PreparedStatement psAgentTab = conn.prepareStatement(queryAgentTab);
		psAgentTab.setInt(1, currIter);
		psAgentTab.setInt(2, agentID);
		
		
		ResultSet rsAgentTab = psAgentTab.executeQuery();
		
		while(rsAgentTab.next())
		{
			AGoalValue = rsAgentTab.getDouble("agoal_value");
			IGoalValue = rsAgentTab.getDouble("igoal_value");
			RGoalValue = rsAgentTab.getDouble("rgoal_value");
		}
		rsAgentTab.close();
		psAgentTab.close();
		
		
		// Get the agent_desirability from event_tab add to goal values
		PreparedStatement psQueryEventTab = conn.prepareStatement(constVars.queryEventTab());
		psQueryEventTab.setInt(1, currIter);
		psQueryEventTab.setInt(2, eventID);
		psQueryEventTab.setInt(3, agentID);
		
		ResultSet rsEventTab = psQueryEventTab.executeQuery();
		
		Double agentDesire = 0.0;
		
		Double eSenseOfReality = 0.0;
		Double ePsychoProximity = 0.0;
		Double eUnexpectedness = 0.0;
		Double eArousal = 0.0;
		
		
		while(rsEventTab.next())
		{
			agentDesire = rsEventTab.getDouble("agent_desirability");
			
			// Get the components of event global variables
			
			eSenseOfReality = rsEventTab.getDouble("event_sense_of_reality");
			ePsychoProximity = rsEventTab.getDouble("event_psycho_proximity");
			eUnexpectedness = rsEventTab.getDouble("event_unexpectedness");
			eArousal = rsEventTab.getDouble("event_arousal");
			
			// Get the event likelihood
			
			likelihood = rsEventTab.getDouble("event_likelihood");
			
		}
		
		rsEventTab.close();
		psQueryEventTab.close();
		
		desireSelf = ((AGoalValue + IGoalValue + RGoalValue + agentDesire)+4)/8.0;
		eventGlobalVars = ((eSenseOfReality + ePsychoProximity 
				           + eUnexpectedness + eArousal)+4)/8.0;
		
		Double emoFactor = (Double)emoAttitudes.get(emoToBeInvoked).get("emotion_factor");
		
		// Store the hope/fear intensity and potential in DB. Update previous state and observed emotions
		
		// Invoke the hope/fear emotions below
		
		if(emoToBeInvoked.equalsIgnoreCase("hope")
				&& (desireSelf > 0)
				&& (likelihood > 0))
		{
			Double hopePotential = EmotionFunctions.calcHopePotential(emoFactor, Math.abs(desireSelf), likelihood, eventGlobalVars);
			Double hopeIntensity = (hopePotential - (Double)emoAttitudes.get(emoToBeInvoked).get("threshold"));
			
			// Update the observed emotions
			PreparedStatement psInsObservedEmos = conn.prepareStatement(constVars.insObservedEmos());
			psInsObservedEmos.setInt(1, currIter);
			psInsObservedEmos.setInt(2, agentID);
			psInsObservedEmos.setInt(3, eventID);
			psInsObservedEmos.setInt(4, -1);
			psInsObservedEmos.setInt(5, -1);
			psInsObservedEmos.setString(6, emoToBeInvoked);
			psInsObservedEmos.setDouble(7, hopePotential);
			psInsObservedEmos.setDouble(8, hopeIntensity);
			
			int rowsAffected = psInsObservedEmos.executeUpdate();
			
			if(rowsAffected > 0)
			{
				System.out.println("No. of rows inserted-->"+rowsAffected);
				
			}
			
			psInsObservedEmos.close();
			
			// Update previous state
			PreparedStatement psInsPrevState = conn.prepareStatement(constVars.insPrevState());
			psInsPrevState.setInt(1, eventID);
			psInsPrevState.setInt(2, agentID);
			psInsPrevState.setInt(3, currIter);
			psInsPrevState.setString(4, emoToBeInvoked);
			psInsPrevState.setDouble(5, hopePotential);
			psInsPrevState.setDouble(6, 0.0);
			
			rowsAffected = 0;
			rowsAffected = psInsPrevState.executeUpdate();
			
			if(rowsAffected > 0)
			{
				System.out.println("No. of rows inserted-->"+rowsAffected);
				
			}
			
			psInsPrevState.close();
		}
		else if(emoToBeInvoked.equalsIgnoreCase("fear")
				&& (desireSelf < 0)
				&& (likelihood > 0))
		{
			Double fearPotential = EmotionFunctions.calcFearPotential(emoFactor, Math.abs(desireSelf), likelihood, eventGlobalVars);
			Double fearIntensity = (fearPotential - (Double)emoAttitudes.get(emoToBeInvoked).get("threshold"));
			
			// Update the observed emotions
			PreparedStatement psInsObservedEmos = conn.prepareStatement(constVars.insObservedEmos());
			psInsObservedEmos.setInt(1, currIter);
			psInsObservedEmos.setInt(2, agentID);
			psInsObservedEmos.setInt(3, eventID);
			psInsObservedEmos.setInt(4, -1);
			psInsObservedEmos.setInt(5, -1);
			psInsObservedEmos.setString(6, emoToBeInvoked);
			psInsObservedEmos.setDouble(7, fearPotential);
			psInsObservedEmos.setDouble(8, fearIntensity);
			
			int rowsAffected = psInsObservedEmos.executeUpdate();
			
			if(rowsAffected > 0)
			{
				System.out.println("No. of rows inserted-->"+rowsAffected);
				
			}
			
			psInsObservedEmos.close();
			
			// Update previous state
			PreparedStatement psInsPrevState = conn.prepareStatement(constVars.insPrevState());
			psInsPrevState.setInt(1, eventID);
			psInsPrevState.setInt(2, agentID);
			psInsPrevState.setInt(3, currIter);
			psInsPrevState.setString(4, emoToBeInvoked);
			psInsPrevState.setDouble(5, 0.0);
			psInsPrevState.setDouble(6, fearPotential);
			
			rowsAffected = 0;
			rowsAffected = psInsPrevState.executeUpdate();
			
			if(rowsAffected > 0)
			{
				System.out.println("No. of rows inserted-->"+rowsAffected);
				
			}
			
			psInsPrevState.close();
		}
		
		
		
		// Invoked the hope/fear emotions above
    }
	
	private void invokeProspectConfirmationEmos(String emoToBeInvoked, Connection conn) throws Exception
	{
		// Get the AGoal, IGoal and RGoal values
		
				String queryAgentTab = constVars.queryAgentTable();
				PreparedStatement psAgentTab = conn.prepareStatement(queryAgentTab);
				psAgentTab.setInt(1, currIter);
				psAgentTab.setInt(2, agentID);
				
				
				ResultSet rsAgentTab = psAgentTab.executeQuery();
				
				while(rsAgentTab.next())
				{
					AGoalValue = rsAgentTab.getDouble("agoal_value");
					IGoalValue = rsAgentTab.getDouble("igoal_value");
					RGoalValue = rsAgentTab.getDouble("rgoal_value");
				}
				rsAgentTab.close();
				psAgentTab.close();
		
		
		// Get the values from event_tab
		
		PreparedStatement psQueryEventTab = conn.prepareStatement(constVars.queryEventTab());
		psQueryEventTab.setInt(1, currIter);
		psQueryEventTab.setInt(2, eventID);
		psQueryEventTab.setInt(3, agentID);
		
		ResultSet rsEventTab = psQueryEventTab.executeQuery();
		
		Double agentDesire = 0.0;
		
		Double eSenseOfReality = 0.0;
		Double ePsychoProximity = 0.0;
		Double eUnexpectedness = 0.0;
		Double eArousal = 0.0;
		
		
		Double agentBelief = 0.0;
		
		while(rsEventTab.next())
		{
			agentDesire = rsEventTab.getDouble("agent_desirability");
			
			eSenseOfReality = rsEventTab.getDouble("event_sense_of_reality");
			ePsychoProximity = rsEventTab.getDouble("event_psycho_proximity");
			eUnexpectedness = rsEventTab.getDouble("event_unexpectedness");
			eArousal = rsEventTab.getDouble("event_arousal");
			
			// Get the event effort
			
			effort = rsEventTab.getDouble("event_effort");
			
			// Get the event realization
			
			realization = rsEventTab.getDouble("event_realization");
			
			// Get the agent's belief
			
			agentBelief = rsEventTab.getDouble("agent_belief");
			
		}
		
		rsEventTab.close();
		psQueryEventTab.close();
		
		
		// Get the previous hope/fear potential
		PreparedStatement psQueryPrevState = conn.prepareStatement(constVars.queryPrevState());
		psQueryPrevState.setInt(1, (currIter - 1));
		psQueryPrevState.setInt(2, eventID);
		psQueryPrevState.setInt(3, agentID);
		
		String prevEmotion = "hope";
		if(emoToBeInvoked.equalsIgnoreCase("fears-confirmed")
				|| emoToBeInvoked.equalsIgnoreCase("relief"))
		{
			prevEmotion = "fear";
		}
		psQueryPrevState.setString(4, prevEmotion);
		
		
		ResultSet rsPrevState = psQueryPrevState.executeQuery();
		
		int noOfPrevEmosRows = 0;
		
		while(rsPrevState.next())
		{
			noOfPrevEmosRows++;
			prevHopePotential = rsPrevState.getDouble("hope_potential");
			prevFearPotential = rsPrevState.getDouble("fear_potential");
		}
		
		rsPrevState.close();
		psQueryPrevState.close();
		
		desireSelf = ((AGoalValue + IGoalValue + RGoalValue + agentDesire)+4)/8.0;
		eventGlobalVars = ((eSenseOfReality + ePsychoProximity 
				           + eUnexpectedness + eArousal)+4)/8.0;
		
		Double emoFactor = (Double)emoAttitudes.get(emoToBeInvoked).get("emotion_factor");
		
		// Invoke prospect-confirmation emotions
		
		if(emoToBeInvoked.equalsIgnoreCase("satisfaction")
				&& (noOfPrevEmosRows > 0)
				&& (desireSelf > 0)
				&& (agentBelief > 0))
		{
			Double satisfactionPotential = EmotionFunctions.calcSatisfactionPotential(emoFactor, prevHopePotential, effort, realization, eventGlobalVars);
			Double satisfactionIntensity = (satisfactionPotential - (Double)emoAttitudes.get(emoToBeInvoked).get("threshold"));
			
			// Update the observed emotions
						PreparedStatement psInsObservedEmos = conn.prepareStatement(constVars.insObservedEmos());
						psInsObservedEmos.setInt(1, currIter);
						psInsObservedEmos.setInt(2, agentID);
						psInsObservedEmos.setInt(3, eventID);
						psInsObservedEmos.setInt(4, -1);
						psInsObservedEmos.setInt(5, -1);
						psInsObservedEmos.setString(6, emoToBeInvoked);
						psInsObservedEmos.setDouble(7, satisfactionPotential);
						psInsObservedEmos.setDouble(8, satisfactionIntensity);
						
						int rowsAffected = psInsObservedEmos.executeUpdate();
						
						if(rowsAffected > 0)
						{
							System.out.println("No. of rows inserted-->"+rowsAffected);
							
						}
						
						psInsObservedEmos.close();
			
									
		}
		else if(emoToBeInvoked.equalsIgnoreCase("fears-confirmed")
				&& (noOfPrevEmosRows > 0)
				&& (desireSelf < 0)
				&& (agentBelief > 0))
		{
			Double fearsConfirmedPotential = EmotionFunctions.calcFearsConfPotential(emoFactor, prevFearPotential, effort, realization, eventGlobalVars);
			Double fearsConfirmedIntensity = (fearsConfirmedPotential - (Double)emoAttitudes.get(emoToBeInvoked).get("threshold"));
			
			// Update the observed emotions
						PreparedStatement psInsObservedEmos = conn.prepareStatement(constVars.insObservedEmos());
						psInsObservedEmos.setInt(1, currIter);
						psInsObservedEmos.setInt(2, agentID);
						psInsObservedEmos.setInt(3, eventID);
						psInsObservedEmos.setInt(4, -1);
						psInsObservedEmos.setInt(5, -1);
						psInsObservedEmos.setString(6, emoToBeInvoked);
						psInsObservedEmos.setDouble(7, fearsConfirmedPotential);
						psInsObservedEmos.setDouble(8, fearsConfirmedIntensity);
						
						int rowsAffected = psInsObservedEmos.executeUpdate();
						
						if(rowsAffected > 0)
						{
							System.out.println("No. of rows inserted-->"+rowsAffected);
							
						}
						
						psInsObservedEmos.close();
		}
		else if(emoToBeInvoked.equalsIgnoreCase("relief")
				&& (noOfPrevEmosRows > 0)
				&& (desireSelf < 0)
				&& (agentBelief < 0))
		{
			Double reliefPotential = EmotionFunctions.calcReliefPotential(emoFactor, prevHopePotential, effort, realization, eventGlobalVars);
			Double reliefIntensity = (reliefPotential - (Double)emoAttitudes.get(emoToBeInvoked).get("threshold"));
			
			// Update the observed emotions
						PreparedStatement psInsObservedEmos = conn.prepareStatement(constVars.insObservedEmos());
						psInsObservedEmos.setInt(1, currIter);
						psInsObservedEmos.setInt(2, agentID);
						psInsObservedEmos.setInt(3, eventID);
						psInsObservedEmos.setInt(4, -1);
						psInsObservedEmos.setInt(5, -1);
						psInsObservedEmos.setString(6, emoToBeInvoked);
						psInsObservedEmos.setDouble(7, reliefPotential);
						psInsObservedEmos.setDouble(8, reliefIntensity);
						
						int rowsAffected = psInsObservedEmos.executeUpdate();
						
						if(rowsAffected > 0)
						{
							System.out.println("No. of rows inserted-->"+rowsAffected);
							
						}
						
						psInsObservedEmos.close();
		}
		else if(emoToBeInvoked.equalsIgnoreCase("disappointment")
				&& (noOfPrevEmosRows > 0)
				&& (desireSelf > 0)
				&& (agentBelief < 0))
		{
			Double disappointmentPotential = EmotionFunctions.calcDisappointmentPotential(emoFactor, prevHopePotential, effort, realization, eventGlobalVars);
			Double disappointmentIntensity = (disappointmentPotential - (Double)emoAttitudes.get(emoToBeInvoked).get("threshold"));
			
			// Update the observed emotions
						PreparedStatement psInsObservedEmos = conn.prepareStatement(constVars.insObservedEmos());
						psInsObservedEmos.setInt(1, currIter);
						psInsObservedEmos.setInt(2, agentID);
						psInsObservedEmos.setInt(3, eventID);
						psInsObservedEmos.setInt(4, -1);
						psInsObservedEmos.setInt(5, -1);
						psInsObservedEmos.setString(6, emoToBeInvoked);
						psInsObservedEmos.setDouble(7, disappointmentPotential);
						psInsObservedEmos.setDouble(8, disappointmentIntensity);
						
						int rowsAffected = psInsObservedEmos.executeUpdate();
						
						if(rowsAffected > 0)
						{
							System.out.println("No. of rows inserted-->"+rowsAffected);
							
						}
						
						psInsObservedEmos.close();
		}
		
		// Update previous state to re-set hope/fear potentials
		PreparedStatement psInsPrevState = conn.prepareStatement(constVars.insPrevState());
		psInsPrevState.setInt(1, eventID);
		psInsPrevState.setInt(2, agentID);
		psInsPrevState.setInt(3, currIter);
		psInsPrevState.setString(4, prevEmotion);
		psInsPrevState.setDouble(5, 0.0);
		psInsPrevState.setDouble(6, 0.0);
		
		int rowsAffected = 0;
		rowsAffected = psInsPrevState.executeUpdate();
		
		if(rowsAffected > 0)
		{
			System.out.println("No. of rows inserted-->"+rowsAffected);
			
		}
		
		psInsPrevState.close();
		
		
		// Invoked prospect-confirmation emotions
	}
    
	private void invokeAgentCogUnitEmos(String emoToBeInvoked, Connection conn) throws Exception
	{
		Double emoFactor = (Double)emoAttitudes.get(emoToBeInvoked).get("emotion_factor");
		
		Iterator<Integer> iterNeighbours = neighbours.keySet().iterator();
		
		while(iterNeighbours.hasNext())
		{
			Integer agentNeighbour = iterNeighbours.next();
			
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
					PreparedStatement psInsObservedEmos = conn.prepareStatement(constVars.insObservedEmos());
					psInsObservedEmos.setInt(1, currIter);
					psInsObservedEmos.setInt(2, agentID);
					psInsObservedEmos.setInt(3, -1);
					psInsObservedEmos.setInt(4, agentNeighbour);
					psInsObservedEmos.setInt(5, -1);
					psInsObservedEmos.setString(6, emoToBeInvoked);
					psInsObservedEmos.setDouble(7, pridePotential);
					psInsObservedEmos.setDouble(8, prideIntensity);
					
					int rowsAffected = psInsObservedEmos.executeUpdate();
					
					if(rowsAffected > 0)
					{
						System.out.println("No. of rows inserted-->"+rowsAffected);
						
					}
					
					psInsObservedEmos.close();
				}
				else if(emoToBeInvoked.equalsIgnoreCase("self-reproach")
						&& (praiseworthiness < 0)
						&& (cogUnit > 0))
				{
					Double selfReproachPotential = (Double)EmotionFunctions.calcSelfReproachPotential(emoFactor, Math.abs(praiseworthiness), expDiff, cogUnit, agentGlobalVars);
					Double selfReproachIntensity = (selfReproachPotential - (Double)emoAttitudes.get(emoToBeInvoked).get("threshold"));
					
					// Update the observed emotions
					PreparedStatement psInsObservedEmos = conn.prepareStatement(constVars.insObservedEmos());
					psInsObservedEmos.setInt(1, currIter);
					psInsObservedEmos.setInt(2, agentID);
					psInsObservedEmos.setInt(3, -1);
					psInsObservedEmos.setInt(4, agentNeighbour);
					psInsObservedEmos.setInt(5, -1);
					psInsObservedEmos.setString(6, emoToBeInvoked);
					psInsObservedEmos.setDouble(7, selfReproachPotential);
					psInsObservedEmos.setDouble(8, selfReproachIntensity);
					
					int rowsAffected = psInsObservedEmos.executeUpdate();
					
					if(rowsAffected > 0)
					{
						System.out.println("No. of rows inserted-->"+rowsAffected);
						
					}
					
					psInsObservedEmos.close();
				}
				
				// Invoked the pride/self-reproach emotion above
				
			}
					
		}
	}
	
	private void invokeAgentNoCogUnitEmos(String emoToBeInvoked, Connection conn) throws Exception
	{
		Double emoFactor = (Double)emoAttitudes.get(emoToBeInvoked).get("emotion_factor");
		
		Iterator<Integer> iterNeighbours = neighbours.keySet().iterator();
		
		while(iterNeighbours.hasNext())
		{
			Integer agentNeighbour = iterNeighbours.next();
			
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
					PreparedStatement psInsObservedEmos = conn.prepareStatement(constVars.insObservedEmos());
					psInsObservedEmos.setInt(1, currIter);
					psInsObservedEmos.setInt(2, agentID);
					psInsObservedEmos.setInt(3, -1);
					psInsObservedEmos.setInt(4, agentNeighbour);
					psInsObservedEmos.setInt(5, -1);
					psInsObservedEmos.setString(6, emoToBeInvoked);
					psInsObservedEmos.setDouble(7, appreciationPotential);
					psInsObservedEmos.setDouble(8, appreciationIntensity);
					
					int rowsAffected = psInsObservedEmos.executeUpdate();
					
					if(rowsAffected > 0)
					{
						System.out.println("No. of rows inserted-->"+rowsAffected);
						
					}
					
					psInsObservedEmos.close();
				}
				else if(emoToBeInvoked.equalsIgnoreCase("reproach")
						&& (praiseworthiness < 0))
				{
					Double reproachPotential = (Double)EmotionFunctions.calcReproachPotential(emoFactor, Math.abs(praiseworthiness), expDiff, agentGlobalVars);
					Double reproachIntensity = (reproachPotential - (Double)emoAttitudes.get(emoToBeInvoked).get("threshold"));
					
					// Update the observed emotions
					PreparedStatement psInsObservedEmos = conn.prepareStatement(constVars.insObservedEmos());
					psInsObservedEmos.setInt(1, currIter);
					psInsObservedEmos.setInt(2, agentID);
					psInsObservedEmos.setInt(3, -1);
					psInsObservedEmos.setInt(4, agentNeighbour);
					psInsObservedEmos.setInt(5, -1);
					psInsObservedEmos.setString(6, emoToBeInvoked);
					psInsObservedEmos.setDouble(7, reproachPotential);
					psInsObservedEmos.setDouble(8, reproachIntensity);
					
					int rowsAffected = psInsObservedEmos.executeUpdate();
					
					if(rowsAffected > 0)
					{
						System.out.println("No. of rows inserted-->"+rowsAffected);
						
					}
					
					psInsObservedEmos.close();
				}
				
				
				// Invoked the appreciation/reproach emotion above
				
			}
					
		}
	}
	
	private void invokeEventAgentNoCogUnitEmos(String emoToBeInvoked, Connection conn) throws Exception
	{
		// Get the AGoal, IGoal and RGoal values
		
		String queryAgentTab = constVars.queryAgentTable();
		PreparedStatement psAgentTab = conn.prepareStatement(queryAgentTab);
		psAgentTab.setInt(1, currIter);
		psAgentTab.setInt(2, agentID);
		
		
		ResultSet rsAgentTab = psAgentTab.executeQuery();
		
		while(rsAgentTab.next())
		{
			AGoalValue = rsAgentTab.getDouble("agoal_value");
			IGoalValue = rsAgentTab.getDouble("igoal_value");
			RGoalValue = rsAgentTab.getDouble("rgoal_value");
		}
		rsAgentTab.close();
		psAgentTab.close();
		
		
		// Get the values from event_tab
		
		PreparedStatement psQueryEventTab = conn.prepareStatement(constVars.queryEventTab());
		psQueryEventTab.setInt(1, currIter);
		psQueryEventTab.setInt(2, eventID);
		psQueryEventTab.setInt(3, agentID);
		
		ResultSet rsEventTab = psQueryEventTab.executeQuery();
		
		Double agentDesire = 0.0;
		
		Double eSenseOfReality = 0.0;
		Double ePsychoProximity = 0.0;
		Double eUnexpectedness = 0.0;
		Double eArousal = 0.0;
		
		
		while(rsEventTab.next())
		{
			agentDesire = rsEventTab.getDouble("agent_desirability");
			
			// Get the event global variables component
			
			eSenseOfReality = rsEventTab.getDouble("event_sense_of_reality");
			ePsychoProximity = rsEventTab.getDouble("event_psycho_proximity");
			eUnexpectedness = rsEventTab.getDouble("event_unexpectedness");
			eArousal = rsEventTab.getDouble("event_arousal");
			
								
		}
		
		rsEventTab.close();
		psQueryEventTab.close();
		
		eventGlobalVars = ((eSenseOfReality + ePsychoProximity 
		           + eUnexpectedness + eArousal)+4)/8.0;
		
		desireSelf = ((AGoalValue + IGoalValue + RGoalValue + agentDesire)+4)/8.0;
		
		Double emoFactor = (Double)emoAttitudes.get(emoToBeInvoked).get("emotion_factor");
		
		Iterator<Integer> iterNeighbours = neighbours.keySet().iterator();
		
		while(iterNeighbours.hasNext())
		{
			Integer agentNeighbour = iterNeighbours.next();
			
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
					PreparedStatement psInsObservedEmos = conn.prepareStatement(constVars.insObservedEmos());
					psInsObservedEmos.setInt(1, currIter);
					psInsObservedEmos.setInt(2, agentID);
					psInsObservedEmos.setInt(3, eventID);
					psInsObservedEmos.setInt(4, agentNeighbour);
					psInsObservedEmos.setInt(5, -1);
					psInsObservedEmos.setString(6, emoToBeInvoked);
					psInsObservedEmos.setDouble(7, gratitudePotential);
					psInsObservedEmos.setDouble(8, gratitudeIntensity);
					
					int rowsAffected = psInsObservedEmos.executeUpdate();
					
					if(rowsAffected > 0)
					{
						System.out.println("No. of rows inserted-->"+rowsAffected);
						
					}
					
					psInsObservedEmos.close();
				}
				else if(emoToBeInvoked.equalsIgnoreCase("anger")
						&& (desireSelf < 0)
						&& (praiseworthiness < 0))
				{
					Double angerPotential = (Double)EmotionFunctions.calcAngerPotential(emoFactor, Math.abs(praiseworthiness), Math.abs(desireSelf), 
							expDiff, agentGlobalVars, eventGlobalVars);
					Double angerIntensity = (angerPotential - (Double)emoAttitudes.get(emoToBeInvoked).get("threshold"));
					
					// Update the observed emotions
					PreparedStatement psInsObservedEmos = conn.prepareStatement(constVars.insObservedEmos());
					psInsObservedEmos.setInt(1, currIter);
					psInsObservedEmos.setInt(2, agentID);
					psInsObservedEmos.setInt(3, eventID);
					psInsObservedEmos.setInt(4, agentNeighbour);
					psInsObservedEmos.setInt(5, -1);
					psInsObservedEmos.setString(6, emoToBeInvoked);
					psInsObservedEmos.setDouble(7, angerPotential);
					psInsObservedEmos.setDouble(8, angerIntensity);
					
					int rowsAffected = psInsObservedEmos.executeUpdate();
					
					if(rowsAffected > 0)
					{
						System.out.println("No. of rows inserted-->"+rowsAffected);
						
					}
					
					psInsObservedEmos.close();
				}
				
				
				// Invoked the gratitude/anger emotion above
				
			}
					
		}
	}
	
	private void invokeEventAgentCogUnitEmos(String emoToBeInvoked, Connection conn) throws Exception
	{
		// Get the AGoal, IGoal and RGoal values
		
		String queryAgentTab = constVars.queryAgentTable();
		PreparedStatement psAgentTab = conn.prepareStatement(queryAgentTab);
		psAgentTab.setInt(1, currIter);
		psAgentTab.setInt(2, agentID);
		
		
		ResultSet rsAgentTab = psAgentTab.executeQuery();
		
		while(rsAgentTab.next())
		{
			AGoalValue = rsAgentTab.getDouble("agoal_value");
			IGoalValue = rsAgentTab.getDouble("igoal_value");
			RGoalValue = rsAgentTab.getDouble("rgoal_value");
		}
		rsAgentTab.close();
		psAgentTab.close();
		
		
		// Get the values from event_tab
		
		PreparedStatement psQueryEventTab = conn.prepareStatement(constVars.queryEventTab());
		psQueryEventTab.setInt(1, currIter);
		psQueryEventTab.setInt(2, eventID);
		psQueryEventTab.setInt(3, agentID);
		
		ResultSet rsEventTab = psQueryEventTab.executeQuery();
		
		Double agentDesire = 0.0;
		
		Double eSenseOfReality = 0.0;
		Double ePsychoProximity = 0.0;
		Double eUnexpectedness = 0.0;
		Double eArousal = 0.0;
		
		
		while(rsEventTab.next())
		{
			agentDesire = rsEventTab.getDouble("agent_desirability");
			
			// Get the event global variables component
			
			eSenseOfReality = rsEventTab.getDouble("event_sense_of_reality");
			ePsychoProximity = rsEventTab.getDouble("event_psycho_proximity");
			eUnexpectedness = rsEventTab.getDouble("event_unexpectedness");
			eArousal = rsEventTab.getDouble("event_arousal");
			
								
		}
		
		rsEventTab.close();
		psQueryEventTab.close();
		
		eventGlobalVars = ((eSenseOfReality + ePsychoProximity 
		           + eUnexpectedness + eArousal)+4)/8.0;
		
		desireSelf = ((AGoalValue + IGoalValue + RGoalValue + agentDesire)+4)/8.0;
		
		Double emoFactor = (Double)emoAttitudes.get(emoToBeInvoked).get("emotion_factor");
		
		Iterator<Integer> iterNeighbours = neighbours.keySet().iterator();
		
		while(iterNeighbours.hasNext())
		{
			Integer agentNeighbour = iterNeighbours.next();
			
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
					PreparedStatement psInsObservedEmos = conn.prepareStatement(constVars.insObservedEmos());
					psInsObservedEmos.setInt(1, currIter);
					psInsObservedEmos.setInt(2, agentID);
					psInsObservedEmos.setInt(3, eventID);
					psInsObservedEmos.setInt(4, agentNeighbour);
					psInsObservedEmos.setInt(5, -1);
					psInsObservedEmos.setString(6, emoToBeInvoked);
					psInsObservedEmos.setDouble(7, gratificationPotential);
					psInsObservedEmos.setDouble(8, gratificationIntensity);
					
					int rowsAffected = psInsObservedEmos.executeUpdate();
					
					if(rowsAffected > 0)
					{
						System.out.println("No. of rows inserted-->"+rowsAffected);
						
					}
					
					psInsObservedEmos.close();
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
					PreparedStatement psInsObservedEmos = conn.prepareStatement(constVars.insObservedEmos());
					psInsObservedEmos.setInt(1, currIter);
					psInsObservedEmos.setInt(2, agentID);
					psInsObservedEmos.setInt(3, eventID);
					psInsObservedEmos.setInt(4, agentNeighbour);
					psInsObservedEmos.setInt(5, -1);
					psInsObservedEmos.setString(6, emoToBeInvoked);
					psInsObservedEmos.setDouble(7, remorsePotential);
					psInsObservedEmos.setDouble(8, remorseIntensity);
					
					int rowsAffected = psInsObservedEmos.executeUpdate();
					
					if(rowsAffected > 0)
					{
						System.out.println("No. of rows inserted-->"+rowsAffected);
						
					}
					
					psInsObservedEmos.close();
				}
				
				
				// Invoked the gratification/remorse emotion above
				
			}
					
		}
	}
	
	private void invokeObjectEmos(String emoToBeInvoked, Connection conn) throws Exception
	{
		Double emoFactor = (Double)emoAttitudes.get(emoToBeInvoked).get("emotion_factor");
		
		// Get values from object_tab
		PreparedStatement psQueryObjectTab = conn.prepareStatement(constVars.queryObjectTab());
		psQueryObjectTab.setInt(1, currIter);
		psQueryObjectTab.setInt(2, objectID);
		psQueryObjectTab.setInt(3, agentID);
		
		ResultSet rsObjectTab = psQueryObjectTab.executeQuery();
		
		Double oSenseOfReality = 0.0;
		Double oPsychoProximity = 0.0;
		Double oUnexpectedness = 0.0;
		Double oArousal = 0.0;
		
		while(rsObjectTab.next())
		{
			familiarity = rsObjectTab.getDouble("familiarity");
			appealingness = rsObjectTab.getDouble("appealingness");
			
			// Get components of object global variables
			
			oSenseOfReality = rsObjectTab.getDouble("obj_sense_of_reality");
			oPsychoProximity = rsObjectTab.getDouble("obj_psycho_proximity");
			oUnexpectedness = rsObjectTab.getDouble("obj_unexpectedness");
			oArousal = rsObjectTab.getDouble("obj_arousal");
			
		}
		
		objectGlobalVars = ((oSenseOfReality + oPsychoProximity + oUnexpectedness + oArousal)+4)/8.0;
		
		// Invoke liking/disliking emotion below
		
		if(emoToBeInvoked.equalsIgnoreCase("liking")
				&& (appealingness > 0))
		{
			Double likingPotential = (Double)EmotionFunctions.calcLikingPotential(emoFactor, Math.abs(appealingness), familiarity, objectGlobalVars);
			Double likingIntensity = (likingPotential - (Double)emoAttitudes.get(emoToBeInvoked).get("threshold"));
			
			// Update the observed emotions
			PreparedStatement psInsObservedEmos = conn.prepareStatement(constVars.insObservedEmos());
			psInsObservedEmos.setInt(1, currIter);
			psInsObservedEmos.setInt(2, agentID);
			psInsObservedEmos.setInt(3, -1);
			psInsObservedEmos.setInt(4, -1);
			psInsObservedEmos.setInt(5, objectID);
			psInsObservedEmos.setString(6, emoToBeInvoked);
			psInsObservedEmos.setDouble(7, likingPotential);
			psInsObservedEmos.setDouble(8, likingIntensity);
			
			int rowsAffected = psInsObservedEmos.executeUpdate();
			
			if(rowsAffected > 0)
			{
				System.out.println("No. of rows inserted-->"+rowsAffected);
				
			}
			
			psInsObservedEmos.close();
		}
		else if(emoToBeInvoked.equalsIgnoreCase("disliking")
				&& (appealingness < 0))
		{
			Double dislikingPotential = (Double)EmotionFunctions.calcDislikingPotential(emoFactor, Math.abs(appealingness), familiarity, objectGlobalVars);
			Double dislikingIntensity = (dislikingPotential - (Double)emoAttitudes.get(emoToBeInvoked).get("threshold"));
			
			// Update the observed emotions
			PreparedStatement psInsObservedEmos = conn.prepareStatement(constVars.insObservedEmos());
			psInsObservedEmos.setInt(1, currIter);
			psInsObservedEmos.setInt(2, agentID);
			psInsObservedEmos.setInt(3, -1);
			psInsObservedEmos.setInt(4, -1);
			psInsObservedEmos.setInt(5, objectID);
			psInsObservedEmos.setString(6, emoToBeInvoked);
			psInsObservedEmos.setDouble(7, dislikingPotential);
			psInsObservedEmos.setDouble(8, dislikingIntensity);
			
			int rowsAffected = psInsObservedEmos.executeUpdate();
			
			if(rowsAffected > 0)
			{
				System.out.println("No. of rows inserted-->"+rowsAffected);
				
			}
			
			psInsObservedEmos.close();
		}
		
		// Invoked liking/disliking emotion above
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
