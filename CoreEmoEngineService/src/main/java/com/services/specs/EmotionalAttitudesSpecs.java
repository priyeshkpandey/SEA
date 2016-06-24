package com.services.specs;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.services.entities.EmotionalAttitudes;

public class EmotionalAttitudesSpecs implements Specification<EmotionalAttitudes>{

	private EmotionalAttitudes emotionalAttitudes;
	
	public EmotionalAttitudesSpecs(EmotionalAttitudes emotionalAttitudes)
	{
		this.emotionalAttitudes = emotionalAttitudes;
	}
	
	
	@Override
	public Predicate toPredicate(Root<EmotionalAttitudes> root,
			CriteriaQuery<?> query, CriteriaBuilder cb) {
		Predicate pAnd = cb.disjunction();
		
		if(emotionalAttitudes.getAgentId() != null)
		{
			pAnd = cb.and(pAnd, root.get("agentId").in(emotionalAttitudes.getAgentId()));
		}
		
		if(emotionalAttitudes.getEmoFactor() != null)
		{
			pAnd = cb.and(pAnd, root.get("emoFactor").in(emotionalAttitudes.getEmoFactor()));
		}
		
		if(emotionalAttitudes.getEmotions() != null)
		{
			pAnd = cb.and(pAnd, root.get("emotions").in(emotionalAttitudes.getEmotions()));
		}
		
		if(emotionalAttitudes.getId() != null)
		{
			pAnd = cb.and(pAnd, root.get("id").in(emotionalAttitudes.getId()));
		}
		
		if(emotionalAttitudes.getIterNo() != null)
		{
			pAnd = cb.and(pAnd, root.get("iterNo").in(emotionalAttitudes.getIterNo()));
		}
		
		if(emotionalAttitudes.getOccurredCnt() != null)
		{
			pAnd = cb.and(pAnd, root.get("occurredCnt").in(emotionalAttitudes.getOccurredCnt()));
		}
		
		if(emotionalAttitudes.getOccurrenceCnt() != null)
		{
			pAnd = cb.and(pAnd, root.get("occurrenceCnt").in(emotionalAttitudes.getOccurrenceCnt()));
		}
		
		if(emotionalAttitudes.getPrecedence() != null)
		{
			pAnd = cb.and(pAnd, root.get("precedence").in(emotionalAttitudes.getPrecedence()));
		}
		
		if(emotionalAttitudes.getPriority() != null)
		{
			pAnd = cb.and(pAnd, root.get("priority").in(emotionalAttitudes.getPriority()));
		}
		
		if(emotionalAttitudes.getProbability() != null)
		{
			pAnd = cb.and(pAnd, root.get("probability").in(emotionalAttitudes.getProbability()));
		}
		
		if(emotionalAttitudes.getSimId() != null)
		{
			pAnd = cb.and(pAnd, root.get("simId").in(emotionalAttitudes.getSimId()));
		}
		
		if(emotionalAttitudes.getThreshold() != null)
		{
			pAnd = cb.and(pAnd, root.get("threshold").in(emotionalAttitudes.getThreshold()));
		}
		
		if(emotionalAttitudes.getUserId() != null)
		{
			pAnd = cb.and(pAnd, root.get("userId").in(emotionalAttitudes.getUserId()));
		}
		
		if(emotionalAttitudes.getWeight() != null)
		{
			pAnd = cb.and(pAnd, root.get("weight").in(emotionalAttitudes.getWeight()));
		}
		
		
		return pAnd;
	}

}
