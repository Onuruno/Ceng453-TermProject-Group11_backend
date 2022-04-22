package com.group11.server.repository;
import com.group11.server.model.Game;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    /**
     * This method is a query to get all the games in the Game table
     * @param pageable is the parameter for paging query.
     * @return the list of games sorted by score and endTime in descending manner
     */
    @Query(value = "SELECT Game.id, Game.username, Game.score, Game.activity, Game.startTime, Game.endTime " +
            "FROM Game " +
            " ORDER BY score DESC, endTime DESC", nativeQuery = true)
    List<Object[]> findAllGame(Pageable pageable);

    /**
     * This method is a query to get all the games after a given date
     * in the Game table including the starting date
     * @param date is the parameter for the starting date
     * @param pageable is the parameter for paging query.
     * @return the list of games sorted by score and endTime in descending manner
     */
    @Query(value = "SELECT Game.id, Game.username, Game.score, Game.activity, Game.startTime, Game.endTime " +
            " FROM Game " +
            "WHERE Game.startTime >= :date" +
            " ORDER BY score DESC, endTime DESC", nativeQuery = true)
    List<Object[]> findAllByStartTimeAfter(@Param("date") LocalDateTime date,
                                           Pageable pageable);

    /**
     * This method is a query to get all the games before a given date
     * in the Game table excluding the ending Date
     * @param date is the parameter for the end date
     * @param pageable is the parameter for paging query.
     * @return the list of games sorted by score and endTime in descending manner
     */
    @Query(value = "SELECT Game.id, Game.username, Game.score, Game.activity, Game.startTime, Game.endTime " +
            " FROM Game " +
            "WHERE Game.endTime < :date" +
            " ORDER BY score DESC, endTime DESC", nativeQuery = true)
    List<Object[]> findAllByStartTimeBefore(@Param("date") LocalDateTime date,
                                            Pageable pageable);

    /**
     * This method is a query to get all the games between two given dates
     * in the Game table including start date and excluding the end date
     * @param startDate is the parameter for the start date
     * @param endDate is the parameter for the end date
     * @param pageable is the parameter for paging query.
     * @return the list of games sorted by score and endTime in descending manner
     */

    @Query(value = "SELECT Game.id, Game.username, Game.score, Game.activity, Game.startTime, Game.endTime " +
            " FROM Game " +
            "WHERE Game.startTime >= :startDate AND Game.endTime < :endDate " +
            "ORDER BY score DESC, endTime DESC", nativeQuery = true)
    List<Object[]> findAllByStartTimeAfterAndEndTimeBefore(@Param("startDate") LocalDateTime startDate,
                                                           @Param("endDate") LocalDateTime endDate,
                                                           Pageable pageable);

    /**
     * This method is a query to get all the games for a given userName
     * in the Game table
     * @param userName is the parameter for the username
     * @param pageable is the parameter for paging query.
     * @return the list of games sorted by score and endTime in descending manner
     */
    @Query(value = "SELECT Game.id, Game.username, Game.score, Game.activity, Game.startTime, Game.endTime " +
            "FROM Game " +
            "WHERE Game.username = :userName " +
            "ORDER BY score DESC, endTime DESC", nativeQuery = true)
    List<Object[]> findAllByUsername(@Param("userName") String userName,
                                     Pageable pageable);

    /**
     * This method is a query to get all the players and their scores
     * in the games that played in last 7 days.s
     * @param pageable is the parameter for paging query.
     * @return list of players with their overall scores
     */
    @Query(value = "SELECT p.*, SUM(g.score) as sum_score " +
            "FROM Game g, Player p " +
            "WHERE g.endTime < SYSDATE() " +
            "AND g.endTime > SYSDATE()-7 " +
            "AND g.username = p.username " +
            "GROUP BY g.username", nativeQuery = true)
    List<Object[]> findLeaderboardWeekly(Pageable pageable);

    /**
     * This method is a query to get all the players and their scores
     * in the games that played in last 7 days.s
     * @param pageable is the parameter for paging query.
     * @return list of players with their overall scores
     */
    @Query(value = "SELECT p.username, SUM(g.score) as sum_score " +
            "FROM Game g, Player p " +
            "WHERE g.endTime < SYSDATE() " +
            "AND g.endTime > SYSDATE()-30 " +
            "AND g.username = p.username " +
            "GROUP BY g.username", nativeQuery = true)
    List<Object[]> findLeaderboardMonthly(Pageable pageable);

}
