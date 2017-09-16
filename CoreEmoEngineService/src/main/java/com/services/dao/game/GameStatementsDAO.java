package com.services.dao.game;

import org.springframework.data.jpa.repository.JpaRepository;

import com.services.entities.game.GameStatements;

public interface GameStatementsDAO extends JpaRepository<GameStatements, Long> {

}
