package com.services.dao.game;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.services.entities.game.GameVocabulary;

public interface GameVocabularyDAO extends JpaRepository<GameVocabulary, Long> {
	
	@Query("from GameVocabulary gv where gv.variable = :variable")
	public List<GameVocabulary> getVocabularyForVariable(@Param("variable") String variable);

}
