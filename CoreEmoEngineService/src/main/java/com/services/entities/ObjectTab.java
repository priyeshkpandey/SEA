package com.services.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name="object_tab")
public class ObjectTab implements Serializable{
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	Long id;
	
	@Column(name="iteration_no")
	Long iterNo;
	
	@Column(name="object_id")
	Long objectId;
	
	@Column(name="agent_id")
	Long agentId;
	
	@Column(name="obj_familiarity")
	Double objFamiliarity;
	
	@Column(name="obj_appealingness")
	Double objAppealingness;
	
	@Column(name="obj_sense_of_reality")
	Double objSenseOfReality;
	
	@Column(name="obj_psycho_proximity")
	Double objPsychoProximity;
	
	@Column(name="obj_unexpectedness")
	Double objUnexpectedness;
	
	@Column(name="obj_arousal")
	Double objArousal;
	
	@Column(name="obj_liking")
	Double objLiking;
	
	@Column(name="user_id")
	String userId;
	
	@Column(name="simulation_id")
	Long simId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIterNo() {
		return iterNo;
	}

	public void setIterNo(Long iterNo) {
		this.iterNo = iterNo;
	}

	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	public Long getAgentId() {
		return agentId;
	}

	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}

	public Double getObjFamiliarity() {
		return objFamiliarity;
	}

	public void setObjFamiliarity(Double objFamiliarity) {
		this.objFamiliarity = objFamiliarity;
	}

	public Double getObjAppealingness() {
		return objAppealingness;
	}

	public void setObjAppealingness(Double objAppealingness) {
		this.objAppealingness = objAppealingness;
	}

	public Double getObjSenseOfReality() {
		return objSenseOfReality;
	}

	public void setObjSenseOfReality(Double objSenseOfReality) {
		this.objSenseOfReality = objSenseOfReality;
	}

	public Double getObjPsychoProximity() {
		return objPsychoProximity;
	}

	public void setObjPsychoProximity(Double objPsychoProximity) {
		this.objPsychoProximity = objPsychoProximity;
	}

	public Double getObjUnexpectedness() {
		return objUnexpectedness;
	}

	public void setObjUnexpectedness(Double objUnexpectedness) {
		this.objUnexpectedness = objUnexpectedness;
	}

	public Double getObjArousal() {
		return objArousal;
	}

	public void setObjArousal(Double objArousal) {
		this.objArousal = objArousal;
	}

	public Double getObjLiking() {
		return objLiking;
	}

	public void setObjLiking(Double objLiking) {
		this.objLiking = objLiking;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Long getSimId() {
		return simId;
	}

	public void setSimId(Long simId) {
		this.simId = simId;
	}
	
	

}
