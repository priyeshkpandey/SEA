package com.services.entities.game;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name="game_player_statements")
public class GamePlayerStatements implements Serializable {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	Long id;
	
	@Column(name="simulation_id")
	Long simId;
	
	@Column(name="user_id")
	String userId;
	
	@Column(name="iteration_no")
	Long iterNo;
	
	@Column(name="agent_id")
	Long agentId;
	
	@Column(name="player_id")
	String playerId;
	
	@Column(name="statement_id")
	Long statementId;
	
	@Column(name="is_already_presented")
	Long isAlreadyPresented;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSimId() {
		return simId;
	}

	public void setSimId(Long simId) {
		this.simId = simId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Long getIterNo() {
		return iterNo;
	}

	public void setIterNo(Long iterNo) {
		this.iterNo = iterNo;
	}

	public Long getAgentId() {
		return agentId;
	}

	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public Long getStatementId() {
		return statementId;
	}

	public void setStatementId(Long statementId) {
		this.statementId = statementId;
	}

	public Long getIsAlreadyPresented() {
		return isAlreadyPresented;
	}

	public void setIsAlreadyPresented(Long isAlreadyPresented) {
		this.isAlreadyPresented = isAlreadyPresented;
	}

}
