package com.services.dao.game;

import org.springframework.data.jpa.repository.JpaRepository;

import com.services.entities.game.GamePlayerStatements;

public interface GamePlayerStatementsDAO extends JpaRepository<GamePlayerStatements, Long> {

}
