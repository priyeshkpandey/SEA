package com.services.specs;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.services.entities.AgentTab;

public class AgentTabSpecs implements Specification<AgentTab>{
	
	private AgentTab agent;
	
	public AgentTabSpecs(AgentTab agent)
	{
		this.agent = agent;
	}

	@Override
	public Predicate toPredicate(Root<AgentTab> root, CriteriaQuery<?> query,
			CriteriaBuilder cb) {
		
		Predicate pAnd = cb.disjunction();
		
		if(agent.getIsAgent() != null)
		{
			pAnd = cb.and(pAnd, root.get("isAgent").in(agent.getIsAgent()));
		}
		
		if(agent.getIsAGoal() != null)
		{
			pAnd = cb.and(pAnd, root.get("isAGoal").in(agent.getIsAGoal()));
		}
		
		if(agent.getIsEvent() != null)
		{
			pAnd = cb.and(pAnd, root.get("isEvent").in(agent.getIsEvent()));
		}
		
		if(agent.getIsIGoal() != null)
		{
			pAnd = cb.and(pAnd, root.get("isIGoal").in(agent.getIsIGoal()));
		}
		
		if(agent.getIsObject() != null)
		{
			pAnd = cb.and(pAnd, root.get("isObject").in(agent.getIsObject()));
		}
		
		if(agent.getIsRGoal() != null)
		{
			pAnd = cb.and(pAnd, root.get("isRGoal").in(agent.getIsRGoal()));
		}
		
		if(agent.getAction() != null)
		{
			pAnd = cb.and(pAnd, root.get("action").in(agent.getAction()));
		}
		
		if(agent.getAgentId() != null)
		{
			pAnd = cb.and(pAnd, root.get("agentId").in(agent.getAgentId()));
		}
		
		if(agent.getaGoalValue() != null)
		{
			pAnd = cb.and(pAnd, root.get("AGoalValue").in(agent.getaGoalValue()));
		}
		
		if(agent.getId() != null)
		{
			pAnd = cb.and(pAnd, root.get("id").in(agent.getId()));
		}
		
		if(agent.getiGoalValue() != null)
		{
			pAnd = cb.and(pAnd, root.get("IGoalValue").in(agent.getiGoalValue()));
		}
		
		if(agent.getIterNo() != null)
		{
			pAnd = cb.and(pAnd, root.get("iterNo").in(agent.getIterNo()));
		}
		
		if(agent.getNumOfEmosToInvoke() != null)
		{
			pAnd = cb.and(pAnd, root.get("numOfEmosToInvoke").in(agent.getNumOfEmosToInvoke()));
		}
		
		if(agent.getrGoalValue() != null)
		{
			pAnd = cb.and(pAnd, root.get("RGoalValue").in(agent.getrGoalValue()));
		}
		
		if(agent.getSimId() != null)
		{
			pAnd = cb.and(pAnd, root.get("simId").in(agent.getSimId()));
		}
		
		if(agent.getUserId() != null)
		{
			pAnd = cb.and(pAnd, root.get("userId").in(agent.getUserId()));
		}
		
		
		return pAnd;
	}

}
