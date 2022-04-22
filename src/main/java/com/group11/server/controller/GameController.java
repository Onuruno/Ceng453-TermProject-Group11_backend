package com.group11.server.controller;

import com.group11.server.model.Game;
import com.group11.server.model.Player;
import com.group11.server.service.GameService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api")
public class GameController {

    private final GameService gameService;

    /**
     * This method maps POST Request to /game and saves game with given ID of player and score.
     *
     * @param playerID ID of the player
     * @param score  Score of the player
     * @throws Exception if player does not exist
     */
    @PostMapping("/game")
    @ApiOperation(value = "Saves game with given ID of player and score",
            notes = "Provide ID of player and score of player to save the game",
            response = void.class)
    public void addRecord(@ApiParam(value = "ID of the game")
                          @RequestParam(value = "playerID") Long playerID,
                          @ApiParam(value = "Score of the player")
                          @RequestParam(value = "score") Integer score) throws Exception {
        gameService.addGame(playerID, score);
    }

    /**
     * This method maps GET Request to /leaderboard_weekly
     *
     * @param pageLimit Size of the returning list. Should be positive int
     * @return A list of last week's games ordered by Score
     */
    @GetMapping("/leaderboard_weekly")
    @ApiOperation(value = "Gets highest N(pageLimit) scores of last week from the database",
            notes = "Provide page limit for receiving that number of elements in returning list",
            response = Game.class,
            responseContainer = "List")
    public List<Pair<Player, Integer>> getWeeklyRecords(@ApiParam(value = "Page limit for receiving that number of elements in returning list. Should be positive int")
                                       @RequestParam(value = "pageLimit") int pageLimit) {
        return gameService.getWeeklyGameRecordList(pageLimit);
    }

    /**
     * This method maps GET Request to /leaderboard_monthly
     *
     * @param pageLimit Size of the returning list. Should be positive int
     * @return A list of last month's games ordered by Score
     */
    @GetMapping("/leaderboard_monthly")
    @ApiOperation(value = "Gets highest N(pageLimit) scores of last month from the database",
            notes = "Provide page limit for receiving that number of elements in returning list",
            response = Game.class,
            responseContainer = "List")
    public List<Pair<Player, Integer>> getMonthlyRecords(@ApiParam(value = "Page limit for receiving that number of elements in returning list. Should be positive int")
                                        @RequestParam(value = "pageLimit") int pageLimit) {
        return gameService.getMonthlyGameRecordList(pageLimit);
    }
}
