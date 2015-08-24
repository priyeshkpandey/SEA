package com.services.dao;

import java.util.List;

import com.services.entities.Simulation;

public interface SimulationDAO {
	
	public void saveSimulation(Simulation sim);
	public List<Simulation> getSimulationsByUser(String userId);

}
