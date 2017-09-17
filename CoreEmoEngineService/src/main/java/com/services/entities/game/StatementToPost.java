package com.services.entities.game;

public class StatementToPost {
	Long simId;
	Long iterNo;
	String playerId;
	Long agentId;
	Long statementId;
	String statement;
	
	public Long getSimId() {
		return simId;
	}
	public void setSimId(Long simId) {
		this.simId = simId;
	}
	public Long getIterNo() {
		return iterNo;
	}
	public void setIterNo(Long iterNo) {
		this.iterNo = iterNo;
	}
	public String getPlayerId() {
		return playerId;
	}
	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}
	public Long getAgentId() {
		return agentId;
	}
	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}
	public Long getStatementId() {
		return statementId;
	}
	public void setStatementId(Long statementId) {
		this.statementId = statementId;
	}
	public String getStatement() {
		return statement;
	}
	public void setStatement(String statement) {
		this.statement = statement;
	}
	

}
