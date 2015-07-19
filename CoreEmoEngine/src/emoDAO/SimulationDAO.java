package emoDAO;

import java.util.List;

import emoCoreServiceObjects.Simulation;

public interface SimulationDAO {
	
	public void saveSimulation(Simulation sim);
	public List<Simulation> getSimulationsByUser(String userId);

}
