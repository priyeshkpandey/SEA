package com.services.dao.game;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.services.entities.game.GameStatements;

public interface GameStatementsDAO extends JpaRepository<GameStatements, Long> {
	
	@Query("from GameStatements gs where gs.simId = :simId AND gs.userId = :userId") 
	public List<GameStatements> getStatementForGameByUserId(@Param("simId") Long simId, @Param("userId") String userId);

}
