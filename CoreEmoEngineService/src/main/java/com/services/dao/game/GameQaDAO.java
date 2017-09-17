package com.services.dao.game;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.services.entities.game.GameQa;

public interface GameQaDAO extends JpaRepository<GameQa, Long> {
	
	@Query("from GameQa gq where gq.simId = :simId")
	public List<GameQa> getGamesBySimId(@Param("simId") Long simId);

}
