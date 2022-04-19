package com.group11.server.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Game's details")
public class Game {

    /**
     * Unique Id for Games
     */
    @Id
    @GeneratedValue
    @ApiModelProperty(notes = "Game's unique id")
    private Long id;

    /**
     * Username of the Player in the Game
     */
    @ApiModelProperty(notes = "Username of the player in the game")
    private String username;

    /**
     * Player's score
     */
    @ApiModelProperty(notes = "Player's score")
    private Integer score;

    /**
     * Activeness value of the game
     */
    @ApiModelProperty(notes = "Activeness value of the game")
    private Boolean activity;

    /**
     * The time that game started
     */
    @ApiModelProperty(notes = "Start time of the game")
    private LocalDateTime startTime;

    /**
     * The time that game ended
     */
    @ApiModelProperty(notes = "End time of the game")
    private LocalDateTime endTime;
}
