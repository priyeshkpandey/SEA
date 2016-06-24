package com.services.specs;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;


import com.services.entities.AgentNetwork;

public class AgentNetworkSpecs implements Specification<AgentNetwork>{
	
	private AgentNetwork agentNetwork;
	
	public AgentNetworkSpecs(AgentNetwork agentNetwork)
	{
		this.agentNetwork = agentNetwork;
	}

	@Override
	public Predicate toPredicate(Root<AgentNetwork> root,
			CriteriaQuery<?> query, CriteriaBuilder cb) {
			
		Predicate pAnd = cb.disjunction();
		
	       if(agentNetwork.getEmoTarget() != null)
	       {
	    	   pAnd = cb.and(pAnd, root.get("emoTarget").in(agentNetwork.getEmoTarget()));
	    	   
	       }
	       
	       if(agentNetwork.getAgentArousal() != null)
	       {
	    	   pAnd = cb.and(pAnd, root.get("agentArousal").in(agentNetwork.getAgentArousal()));
	       }
	       
	       if(agentNetwork.getAgentId1() != null)
	       {
	    	   pAnd = cb.and(pAnd, root.get("agentId1").in(agentNetwork.getAgentId1()));
	       }
	       
	       if(agentNetwork.getAgentId2() != null)
	       {
	    	   pAnd = cb.and(pAnd, root.get("agentId2").in(agentNetwork.getAgentId2()));
	       }
	       
	       if(agentNetwork.getAgentPsychoProximity() != null)
	       {
	    	   pAnd = cb.and(pAnd, root.get("agentPsychoProximity").in(agentNetwork.getAgentPsychoProximity()));
	       }
	       
	       if(agentNetwork.getAgentSenseOfReality() != null)
	       {
	    	   pAnd = cb.and(pAnd, root.get("agentSenseOfReality").in(agentNetwork.getAgentSenseOfReality()));
	       }
	       
	       if(agentNetwork.getAgentUnexpect() != null)
	       {
	    	   pAnd = cb.and(pAnd, root.get("agentUnexpect").in(agentNetwork.getAgentUnexpect()));
	       }
	       
	       if(agentNetwork.getCogUnitStrength() != null)
	       {
	    	   pAnd = cb.and(pAnd, root.get("cogUnitStrength").in(agentNetwork.getCogUnitStrength()));
	       }
	       
	       if(agentNetwork.getDeservingness() != null)
	       {
	    	   pAnd = cb.and(pAnd, root.get("deservingness").in(agentNetwork.getDeservingness()));
	       }
	       
	       if(agentNetwork.getEventInvolved() != null)
	       {
	    	   pAnd = cb.and(pAnd, root.get("eventInvolved").in(agentNetwork.getEventInvolved()));
	       }
	       
	       if(agentNetwork.getExpDiff() != null)
	       {
	    	   pAnd = cb.and(pAnd, root.get("expDiff").in(agentNetwork.getExpDiff()));
	       }
	       
	       if(agentNetwork.getId() != null)
	       {
	    	   pAnd = cb.and(pAnd, root.get("id").in(agentNetwork.getId()));
	       }
	       
	       if(agentNetwork.getIterNo() != null)
	       {
	    	   pAnd = cb.and(pAnd, root.get("iterNo").in(agentNetwork.getIterNo()));
	       }
	       
	       if(agentNetwork.getOtherDesirability() != null)
	       {
	    	   pAnd = cb.and(pAnd, root.get("otherDesirability").in(agentNetwork.getOtherDesirability()));
	       }
	       
	       if(agentNetwork.getOtherLiking() != null)
	       {
	    	   pAnd = cb.and(pAnd, root.get("otherLiking").in(agentNetwork.getOtherLiking()));
	       }
	       
	       if(agentNetwork.getPraiseworthiness() != null)
	       {
	    	   pAnd = cb.and(pAnd, root.get("praiseworthiness").in(agentNetwork.getPraiseworthiness()));
	       }
	       
	       if(agentNetwork.getSimId() != null)
	       {
	    	   pAnd = cb.and(pAnd, root.get("simId").in(agentNetwork.getSimId()));
	       }
	       
	       if(agentNetwork.getUserId() != null)
	       {
	    	   pAnd = cb.and(pAnd, root.get("userId").in(agentNetwork.getUserId()));
	       }
	        
		
		
		return pAnd;
	}

	
}
