package com.group11.server.service;

import com.group11.server.dao.GameDao;
import org.springframework.data.util.Pair;

import java.util.List;

public interface GameService {
    void addGame(Long playerId, Integer score) throws Exception;
    List<GameDao> getWeeklyGameRecordList(int pageLimit);
    List<GameDao> getMonthlyGameRecordList(int pageLimit);
}
