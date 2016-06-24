package com.services.specs;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.services.entities.ObjectTab;

public class ObjectTabSpecs implements Specification<ObjectTab>{
	
	private ObjectTab object;
	
	public ObjectTabSpecs(ObjectTab object)
	{
		this.object = object;
	}

	@Override
	public Predicate toPredicate(Root<ObjectTab> root, CriteriaQuery<?> query,
			CriteriaBuilder cb) {
		
		Predicate pAnd = cb.disjunction();
		
		if(object.getAgentId() != null)
		{
			pAnd = cb.and(pAnd, root.get("agentId").in(object.getAgentId()));
		}
		
		if(object.getId() != null)
		{
			pAnd = cb.and(pAnd, root.get("id").in(object.getId()));
		}
		
		if(object.getIterNo() != null)
		{
			pAnd = cb.and(pAnd, root.get("iterNo").in(object.getIterNo()));
		}
		
		if(object.getObjAppealingness() != null)
		{
			pAnd = cb.and(pAnd, root.get("objAppealingness").in(object.getObjAppealingness()));
		}
		
		if(object.getObjArousal() != null)
		{
			pAnd = cb.and(pAnd, root.get("objArousal").in(object.getObjArousal()));
		}
		
		if(object.getObjectId() != null)
		{
			pAnd = cb.and(pAnd, root.get("objectId").in(object.getObjectId()));
		}
		
		if(object.getObjFamiliarity() != null)
		{
			pAnd = cb.and(pAnd, root.get("objFamiliarity").in(object.getObjFamiliarity()));
		}
		
		if(object.getObjLiking() != null)
		{
			pAnd = cb.and(pAnd, root.get("objLiking").in(object.getObjLiking()));
		}
		
		if(object.getObjPsychoProximity() != null)
		{
			pAnd = cb.and(pAnd, root.get("objPsychoProximity").in(object.getObjPsychoProximity()));
		}
		
		if(object.getObjSenseOfReality() != null)
		{
			pAnd = cb.and(pAnd, root.get("objSenseOfReality").in(object.getObjSenseOfReality()));
		}
		
		if(object.getObjUnexpectedness() != null)
		{
			pAnd = cb.and(pAnd, root.get("objUnexpectedness").in(object.getObjUnexpectedness()));
		}
		
		if(object.getSimId() != null)
		{
			pAnd = cb.and(pAnd, root.get("simId").in(object.getSimId()));
		}
		
		if(object.getUserId() != null)
		{
			pAnd = cb.and(pAnd, root.get("userId").in(object.getUserId()));
		}
		
		
		
		return pAnd;
	}

}
