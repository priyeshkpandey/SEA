package com.services.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.services.entities.Simulation;

public interface SimulationDAO extends JpaRepository<Simulation, Long>, JpaSpecificationExecutor<Simulation>{
	
	//public void saveSimulation(Simulation sim);
	@Query("from Simulation sim where sim.userId = :userId")
	public List<Simulation> getSimulationsByUser(@Param("userId") String userId);
	
	@Query("from Simulation sim where sim.userId = :userId and sim.simId = :simId")
	public Simulation getSimulationByUserAndSimId(
			@Param("userId") String userId,
			@Param("simId") Long simId);
	
	@Query("from Simulation sim where sim.simName = :simName")
	public Simulation getSimulationByName(@Param("simName") String simName);

}
