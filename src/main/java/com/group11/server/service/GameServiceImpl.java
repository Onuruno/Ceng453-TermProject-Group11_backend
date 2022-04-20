package com.group11.server.service;

import com.group11.server.model.Game;
import com.group11.server.model.Player;
import com.group11.server.repository.GameRepository;
import com.group11.server.repository.PlayerRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
        game.setEndTime(LocalDateTime.now());
        gameRepository.save(game);
    }

    /**
     * This method gets all games from the database with decreasing score order.
     *
     * @param pageLimit Size of the returning list. Should be positive int
     * @return A list of games
     */
    @Override
    public List<Game> getAllGames(int pageLimit) {

        return convertToGameList(gameRepository.findAllGame(PageRequest.of(0, pageLimit)));
    }

    /**
     * This method gets weekly games from the database ordered in decreasing order by Score with size of pageLimit.
     *
     * @param pageLimit Size of the returning list. Should be positive int
     * @return A list of games
     */
    @Override
    public List<Game> getWeeklyGameRecordList(int pageLimit) {

        LocalDateTime oneWeek = LocalDateTime.now().minus(1, ChronoUnit.WEEKS);

        return convertToGameList(gameRepository.findAllByStartTimeAfterAndEndTimeBefore(oneWeek,
                LocalDateTime.now(), PageRequest.of(0, pageLimit)));
    }

    /**
     * This method gets monthly games from the database ordered in decreasing order by Score with size of pageLimit.
     *
     * @param pageLimit Size of the returning list. Should be positive int
     * @return A list of games
     */
    @Override
    public List<Game> getMonthlyGameRecordList(int pageLimit) {

        LocalDateTime oneMonth = LocalDateTime.now().minus(1, ChronoUnit.MONTHS);

        return convertToGameList(gameRepository.findAllByStartTimeAfterAndEndTimeBefore(oneMonth,
                LocalDateTime.now(), PageRequest.of(0, pageLimit)));
    }

    /**
     * This method gets query results and transforms it into a list of games.
     *
     * @param queryResultList A list of objects that holds games
     * @return A list of games
     */
    private List<Game> convertToGameList(List<Object[]> queryResultList) {
        List<Game> gameList = new ArrayList<>();

        for (Object[] record : queryResultList) {
            Game game = new Game();
            game.setId((Long) record[0]);
            game.setUsername((String) record[1]);
            game.setScore((Integer) record[2]);
            game.setActivity((Boolean) record[3]);
            game.setStartTime((LocalDateTime) record[4]);
            game.setEndTime((LocalDateTime) record[5]);
            gameList.add(game);
        }

        return gameList;
    }
}
