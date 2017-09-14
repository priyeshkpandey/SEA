package com.services.dao.game;

import org.springframework.data.jpa.repository.JpaRepository;

import com.services.entities.game.GameVocabulary;

public interface GameVocabularyDAO extends JpaRepository<GameVocabulary, Long> {

}
