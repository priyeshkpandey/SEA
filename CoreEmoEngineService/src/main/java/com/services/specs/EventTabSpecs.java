package com.services.specs;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.services.entities.EventTab;

public class EventTabSpecs implements Specification<EventTab> {

	private EventTab event;

	public EventTabSpecs(EventTab event) {
		this.event = event;
	}

	@Override
	public Predicate toPredicate(Root<EventTab> root, CriteriaQuery<?> query,
			CriteriaBuilder cb) {
		Predicate pAnd = cb.disjunction();

		if (event.getAgentBelief() != null) {
			pAnd = cb.and(pAnd,
					root.get("agentBelief").in(event.getAgentBelief()));
		}

		if (event.getAgentDesirability() != null) {
			pAnd = cb.and(
					pAnd,
					root.get("agentDesirability").in(
							event.getAgentDesirability()));
		}

		if (event.getAgentId() != null) {
			pAnd = cb.and(pAnd,
					root.get("agentId").in(event.getAgentId()));
		}

		if (event.getEventArousal() != null) {
			pAnd = cb.and(pAnd,
					root.get("eventArousal").in(event.getEventArousal()));
		}

		if (event.getEventEffort() != null) {
			pAnd = cb.and(pAnd,
					root.get("eventEffort").in(event.getEventEffort()));
		}

		if (event.getEventId() != null) {
			pAnd = cb.and(pAnd,
					root.get("eventId").in(event.getEventId()));
		}

		if (event.getEventLikelihood() != null) {
			pAnd = cb.and(pAnd,
					root.get("eventLikelihood").in(event.getEventLikelihood()));
		}

		if (event.getEventProspect() != null) {
			pAnd = cb.and(pAnd,
					root.get("eventProspect").in(event.getEventProspect()));
		}

		if (event.getEventPsychoProximity() != null) {
			pAnd = cb.and(pAnd,
					root.get("eventPsychoProximity").in(event.getEventPsychoProximity()));
		}

		if (event.getEventRealization() != null) {
			pAnd = cb.and(pAnd,
					root.get("eventRealization").in(event.getEventRealization()));
		}

		if (event.getEventSenseOfReality() != null) {
			pAnd = cb.and(pAnd,
					root.get("eventSenseOfReality").in(event.getEventSenseOfReality()));
		}

		if (event.getEventUnexpectedness() != null) {
			pAnd = cb.and(pAnd,
					root.get("eventUnexpectedness").in(event.getEventUnexpectedness()));
		}

		if (event.getId() != null) {
			pAnd = cb.and(pAnd,
					root.get("id").in(event.getId()));
		}

		if (event.getIterNo() != null) {
			pAnd = cb.and(pAnd,
					root.get("iterNo").in(event.getIterNo()));
		}

		if (event.getSimId() != null) {
			pAnd = cb.and(pAnd,
					root.get("simId").in(event.getSimId()));
		}

		if (event.getUserId() != null) {
			pAnd = cb.and(pAnd,
					root.get("userId").in(event.getUserId()));
		}

		return pAnd;
	}

}
