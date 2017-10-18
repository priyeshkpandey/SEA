package com.services.dao.game;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.services.entities.game.GamePlayer;

public interface GamePlayerDAO extends JpaRepository<GamePlayer, Long> {
	
	@Query("from GamePlayer gp where gp.playerId = :playerId")
	public GamePlayer getPlayerByPlayerId(@Param("playerId") String playerId);

}
