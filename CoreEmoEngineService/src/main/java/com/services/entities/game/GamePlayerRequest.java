package com.services.entities.game;

public class GamePlayerRequest {
	Long simId;
	String userId;
	String playerId;
	Long iterNo;
	
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
	public String getPlayerId() {
		return playerId;
	}
	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}
	public Long getIterNo() {
		return iterNo;
	}
	public void setIterNo(Long iterNo) {
		this.iterNo = iterNo;
	}

	
}
