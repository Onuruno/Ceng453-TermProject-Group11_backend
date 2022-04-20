package com.group11.server.service;

import com.group11.server.model.Game;

import java.util.List;

public interface GameService {
    void addGame(Long playerId, Integer score) throws Exception;
    List<Game> getAllGames(int pageLimit);
    List<Game> getWeeklyGameRecordList(int pageLimit);
    List<Game> getMonthlyGameRecordList(int pageLimit);
}
