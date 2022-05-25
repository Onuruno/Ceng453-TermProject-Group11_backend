package com.group11.server.service;

import com.group11.server.dao.GameDao;
import com.group11.server.model.Game;
import com.group11.server.model.Player;
import com.group11.server.repository.GameRepository;
import com.group11.server.repository.PlayerRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.math.BigDecimal.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;

    /**
     * This method takes ID of player and score of player
     * as parameter and saves it.
     *
     * @param playerId ID of the player
     * @param score  Score of the player
     * @throws Exception if Player does not exist
     */
    @Override
    public void addGame(Long playerId, Integer score) throws  Exception {
        Optional<Player> optionalPlayer = playerRepository.findById(playerId);
        if (optionalPlayer.isEmpty()) throw new Exception("Player is not found.");
        Player player = optionalPlayer.get();
        if (score < 0) throw new Exception("Score cannot be negative");
        Game game = new Game();
        game.setUsername(player.getUsername());
        game.setScore(score);
        game.setEndTime(LocalDate.now());
        gameRepository.save(game);
    }

    /**
     * This method gets weekly games from the database ordered in decreasing order by Score with size of pageLimit.
     *
     * @param pageLimit Size of the returning list. Should be positive int
     * @return A list of games
     */
    @Override
    public List<GameDao> getWeeklyGameRecordList(int pageLimit) {

        return convertToUsernameScorePairList(gameRepository.findLeaderboardWeekly(PageRequest.of(0, pageLimit)));
    }

    /**
     * This method gets monthly games from the database ordered in decreasing order by Score with size of pageLimit.
     *
     * @param pageLimit Size of the returning list. Should be positive int
     * @return A list of games
     */
    @Override
    public List<GameDao> getMonthlyGameRecordList(int pageLimit) {

        return convertToUsernameScorePairList(gameRepository.findLeaderboardMonthly(PageRequest.of(0, pageLimit)));
    }

    /**
     * This method gets query results and transforms it into a list of
     * player and score pairs.
     *
     * @param queryResultList A list of objects that holds games
     * @return A list of username score pairs
     */
    private List<GameDao> convertToUsernameScorePairList(List<Object[]> queryResultList) {
        List<GameDao> playerScoreList = new ArrayList<>();

        for (Object[] record : queryResultList) {
            GameDao gameDao = new GameDao();
            gameDao.setUsername((String) record[0]);
            gameDao.setScore(((BigDecimal) record[1]).intValue());
            playerScoreList.add(gameDao);
        }

        return playerScoreList;
    }
}
