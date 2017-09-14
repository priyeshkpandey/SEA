package com.services.dao.game;

import org.springframework.data.jpa.repository.JpaRepository;

import com.services.entities.game.GameQaState;

public interface GameQaStateDAO extends JpaRepository<GameQaState, Long> {

}
