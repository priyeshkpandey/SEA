package com.services.dao.game;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.services.entities.game.GameQaState;

public interface GameQaStateDAO extends JpaRepository<GameQaState, Long> {
	
	@Query("from GameQaState gsq where gsq.playerId = :playerId AND gsq.simId = :simId")
	public List<GameQaState> getAskedQuestionsByPlayerAndSimId(@Param("playerId") String playerId, @Param("simId") Long simId);
	
    @Query("select sum(gsq.scoreForAnswer) from GameQaState gsq where gsq.playerId = :playerId AND gsq.simId = :simId AND gsq.isAnswerCorrect = 1")
	public Long getScoreForThePlayerByGame(@Param("playerId") String playerId, @Param("simId") Long simId);

}
