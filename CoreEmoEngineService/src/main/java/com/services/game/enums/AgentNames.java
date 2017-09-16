package com.services.game.enums;

public enum AgentNames {
	
	ALBERT(1l, "Albert"), BOB(2l, "Bob"), CHERYL(3l, "Cheryl"), DAVID(4l, "David");
	
	private Long agentId;
	private String agentName;
	
	AgentNames(Long agentId, String agentName) {
		this.agentId = agentId;
		this.agentName = agentName;
	}
	
	public Long getAgentId() { return agentId; }
	public String getAgentName() { return agentName; }
	
	public static String getAgentNameById(Long agentId) {
		for (AgentNames agent : AgentNames.values()) {
			if (agent.getAgentId() == agentId) {
				return agent.getAgentName();
			}
		}
		return null;
	}

}
