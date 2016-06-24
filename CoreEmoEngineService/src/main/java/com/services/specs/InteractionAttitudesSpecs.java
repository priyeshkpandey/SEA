package com.services.specs;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.services.entities.InteractionAttitudes;

public class InteractionAttitudesSpecs implements Specification<InteractionAttitudes>{
	
	private InteractionAttitudes interactAttitudes;
	
	public InteractionAttitudesSpecs(InteractionAttitudes interactAttitudes)
	{
		this.interactAttitudes = interactAttitudes;
	}

	@Override
	public Predicate toPredicate(Root<InteractionAttitudes> root,
			CriteriaQuery<?> query, CriteriaBuilder cb) {
		Predicate pAnd = cb.disjunction();
		
		if(interactAttitudes.getIsInfluencePersists() != null)
		{
			pAnd = cb.and(pAnd, root.get("isInfluencePersists").in(interactAttitudes.getIsInfluencePersists()));
		}
		
		if(interactAttitudes.getAgentId1() != null)
		{
			pAnd = cb.and(pAnd, root.get("agentId1").in(interactAttitudes.getAgentId1()));
		}
		
		if(interactAttitudes.getAgentId2() != null)
		{
			pAnd = cb.and(pAnd, root.get("agentId2").in(interactAttitudes.getAgentId2()));
		}
		
		if(interactAttitudes.getId() != null)
		{
			pAnd = cb.and(pAnd, root.get("id").in(interactAttitudes.getId()));
		}
		
		if(interactAttitudes.getInfluence() != null)
		{
			pAnd = cb.and(pAnd, root.get("influence").in(interactAttitudes.getInfluence()));
		}
		
		if(interactAttitudes.getInfluenceFilter() != null)
		{
			pAnd = cb.and(pAnd, root.get("influenceFilter").in(interactAttitudes.getInfluenceFilter()));
		}
		
		if(interactAttitudes.getInfluenceLikelihood() != null)
		{
			pAnd = cb.and(pAnd, root.get("influenceLikelihood").in(interactAttitudes.getInfluenceLikelihood()));
		}
		
		if(interactAttitudes.getInfluenceThreshold() != null)
		{
			pAnd = cb.and(pAnd, root.get("influenceThreshold").in(interactAttitudes.getInfluenceThreshold()));
		}
		
		if(interactAttitudes.getInteractProb() != null)
		{
			pAnd = cb.and(pAnd, root.get("interactProb").in(interactAttitudes.getInteractProb()));
		}
		
		if(interactAttitudes.getIterNo() != null)
		{
			pAnd = cb.and(pAnd, root.get("iterNo").in(interactAttitudes.getIterNo()));
		}
		
		if(interactAttitudes.getPrecedence() != null)
		{
			pAnd = cb.and(pAnd, root.get("precedence").in(interactAttitudes.getPrecedence()));
		}
		
		if(interactAttitudes.getSimId() != null)
		{
			pAnd = cb.and(pAnd, root.get("simId").in(interactAttitudes.getSimId()));
		}
		
		if(interactAttitudes.getTargetAgent() != null)
		{
			pAnd = cb.and(pAnd, root.get("targetAgent").in(interactAttitudes.getTargetAgent()));
		}
		
		if(interactAttitudes.getTargetEmotion() != null)
		{
			pAnd = cb.and(pAnd, root.get("targetEmotion").in(interactAttitudes.getTargetEmotion()));
		}
		
		if(interactAttitudes.getTargetEvent() != null)
		{
			pAnd = cb.and(pAnd, root.get("targetEvent").in(interactAttitudes.getTargetEvent()));
		}
		
		if(interactAttitudes.getThirdPerson() != null)
		{
			pAnd = cb.and(pAnd, root.get("thirdPerson").in(interactAttitudes.getThirdPerson()));
		}
		
		if(interactAttitudes.getUserId() != null)
		{
			pAnd = cb.and(pAnd, root.get("userId").in(interactAttitudes.getUserId()));
		}
		
		if(interactAttitudes.getVariable() != null)
		{
			pAnd = cb.and(pAnd, root.get("variable").in(interactAttitudes.getVariable()));
		}
		
		return pAnd;
	}

}
