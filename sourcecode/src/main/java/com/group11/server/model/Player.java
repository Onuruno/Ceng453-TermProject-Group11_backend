package com.group11.server.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Player's details")
public class Player {

    /**
     * Unique Id for Players
     */
    @Id
    @GeneratedValue
    @ApiModelProperty(notes = "Player's unique id")
    private Long id;

    /**
     * Player's username
     */
    @Column(unique = true)
    @ApiModelProperty(notes = "Player's unique username")
    private String username;

    /**
     * Player's password
     */
    @ApiModelProperty(notes = "Player's password")
    private String password;

    /**
     * Player's email
     */
    @ApiModelProperty(notes = "Player's password")
    private String email;


    /**
     * Player's reset password token
     */
    @ApiModelProperty(notes = "Player's reset password token")
    private String resetPasswordToken;
}
