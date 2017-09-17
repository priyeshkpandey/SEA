package com.services.dao.game;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.services.entities.game.GamePlayerStatements;

public interface GamePlayerStatementsDAO extends JpaRepository<GamePlayerStatements, Long> {
	
	@Query("from GamePlayerStatements gps where gps.playerId = :playerId AND gps.simId = :simId AND gps.iterNo = :iterNo AND gps.isAlreadyPresented = 0")
	public List<GamePlayerStatements> getUnpresentedStatementsForPlayerByGameAndIter(@Param("playerId") String playerId, @Param("simId") Long simId, @Param("iterNo") Long iterNo);
	
	@Query("select count(gps.id) from GamePlayerStatements gps where gps.playerId = :playerId AND gps.simId = :simId")
	public Long getCountOfAllStatementsForGameByPlayer(@Param("playerId") String playerId, @Param("simId") Long simId);
	
	@Query("select count(gps.id) from GamePlayerStatements gps where gps.playerId = :playerId AND gps.simId = :simId  AND gps.isAlreadyPresented = 1") 
	public Long getCountOfPresentedStatementsForGameByPlayer(@Param("playerId") String playerId, @Param("simId") Long simId);
	
}
