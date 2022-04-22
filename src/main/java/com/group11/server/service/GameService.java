package com.group11.server.service;

import com.group11.server.model.Player;
import org.springframework.data.util.Pair;

import java.util.List;

public interface GameService {
    void addGame(Long playerId, Integer score) throws Exception;
    List<Pair<Player, Integer>> getWeeklyGameRecordList(int pageLimit);
    List<Pair<Player, Integer>> getMonthlyGameRecordList(int pageLimit);
}
