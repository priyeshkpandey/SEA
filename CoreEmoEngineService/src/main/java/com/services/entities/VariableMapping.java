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
@Table(name="variable_mappings")
public class VariableMapping implements Serializable{
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	Long id;
	
	@Column(name="variable_id")
	String variableId;
	
	@Column(name="variable_description")
	String varDescription;
	
	@Column(name="table_name")
	String tableName;
	
	@Column(name="column_name")
	String colName;
	
	@Column(name="condition_columns")
	String condCols;
	
	@Column(name="is_interaction_involved")
	Boolean isInteractionInvolved;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getVariableId() {
		return variableId;
	}

	public void setVariableId(String variableId) {
		this.variableId = variableId;
	}

	public String getVarDescription() {
		return varDescription;
	}

	public void setVarDescription(String varDescription) {
		this.varDescription = varDescription;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getColName() {
		return colName;
	}

	public void setColName(String colName) {
		this.colName = colName;
	}

	public String getCondCols() {
		return condCols;
	}

	public void setCondCols(String condCols) {
		this.condCols = condCols;
	}

	public Boolean getIsInteractionInvolved() {
		return isInteractionInvolved;
	}

	public void setIsInteractionInvolved(Boolean isInteractionInvolved) {
		this.isInteractionInvolved = isInteractionInvolved;
	}
	
	

}
