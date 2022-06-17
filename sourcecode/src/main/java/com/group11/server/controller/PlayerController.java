package com.group11.server.controller;

import com.group11.server.model.Player;
import com.group11.server.repository.PlayerRepository;
import com.group11.server.service.PlayerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.*;
import org.springframework.mail.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api")
@Api(value = "Player REST Endpoints", description = "Contains endpoints to interact with the Players in API")
public class PlayerController {

    private final PlayerService playerService;
    private final PlayerRepository playerRepository;

    /**
     * This method maps POST request to /login.
     *
     * @param player Player information that will be used for login action
     * @return JwtResponse with the current sessions JWTToken
     */
    @PostMapping("/login")
    @ApiOperation(value = "Logins player with given username and password",
            notes = "Provide username and password to login",
            response = ResponseEntity.class)
    public ResponseEntity<?> login(@ApiParam(value = "User information that will be used for login action")
                                   @RequestBody Player player) {
        return playerService.login(player);
    }

    /**
     * This method maps GET request to /getUserID.
     *
     * @param username Username of the player whose ID is wanted
     * @return The ID of the player with given username
     * @throws Exception if ID does not exist or user is deleted
     */
    @GetMapping("/getPlayerID")
    @ApiOperation(value = "Gets ID of the player with given username",
            notes = "Provide username to receive ID of that player",
            response = Long.class)
    public Long getPlayerID(@ApiParam(value = "Username of the player whose ID is wanted")
                          @RequestParam(value = "username") String username) throws Exception {
        return playerService.getPlayerID(username);
    }

    /**
     * This method maps GET request to /players.
     *
     * @return List of all users
     */
    @GetMapping("/players")
    @ApiOperation(value = "Gets all player from database",
            notes = "Needs no parameter",
            response = List.class)
    public List<Player> getAllPlayers() {
        return playerService.getAllPlayers();
    }

    /**
     * This method maps GET request to /player.
     *
     * @param Id ID of the user that is wanted
     * @return Player with the given ID
     * @throws Exception if ID does not exist or user is deleted
     */
    @GetMapping("/player")
    @ApiOperation(value = "Gets the player from the provided ID",
            notes = "Provide ID to receive the player information",
            response = Player.class)
    public Player getPlayer(@ApiParam(value = "ID of the player that is wanted")
                        @RequestParam(value = "id") Long Id) throws Exception {
        return playerService.getPlayer(Id);
    }

    /**
     * This method maps POST request to /register.
     * It saves new valid Player to database.
     * If player is not valid or credentials are bad, returns a message about that error.
     *
     * @param player Player information that will be registered
     * @return Response message of the server
     */
    @PostMapping("/register")
    @ApiOperation(value = "Registers the player information given",
            notes = "Provide username and password that will be saved as new player",
            response = ResponseEntity.class)
    public ResponseEntity<?> register(@ApiParam(value = "Player information that will be registered")
                                      @RequestBody Player player
    ) {
        return playerService.register(player);
    }

    /**
     * This method maps PUT request to /profile.
     * It updates player's information with given parameters.
     * If player is not valid or credentials are bad, returns a message about that error.
     *
     * @param player Player information that will be registered
     * @param Id ID of the player that will be updated
     * @return Response message of the server as String
     */
    @PutMapping("/player")
    @ApiOperation(value = "Updates the player that is from given ID with new player information",
            notes = "Provide new password that will be updated",
            response = String.class)
    public String updatePlayer(@ApiParam(value = "Player information that will be used to update player")
                             @RequestBody Player player,
                             @ApiParam(value = "ID of the player that will be updated")
                             @RequestParam(value = "id") Long Id) {
        return playerService.updatePlayer(player, Id);
    }

    @PostMapping("/forgotpassword")
    @ApiOperation(value = "Sends a reset link to the user",
    notes = "Send an email that contains the reset link to the user.",
    response =  String.class)
    public String forgotPassword (@ApiParam(value = "mail of the given user")
                                        @RequestParam(value = "mail") String mail,
                                        @ApiParam(value = "username of the given user")
                                        @RequestParam(value = "username") String username) {
        try {
            Optional<Player> user = playerRepository.findByUsername(username);
            if (user.isPresent()) {
                if (user.get().getEmail().equals(username)) {
                    String token = RandomString.make(30);
                    playerService.updateResetPasswordToken(token, username);
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setTo(mail);
                    message.setSubject("Your Password Reset Link");
                    message.setFrom("testmail@gmail.com"); //ADD A PROPER EMAIL
                    message.setText("In order to reset your password, please click the following link: "
                            + "http://localhost:8080/resetPassword?token="+token);

                    JavaMailSender javaMailSender = new JavaMailSenderImpl();
                    javaMailSender.send(message);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "forgotPassword";
    }

    @GetMapping("/resetpassword")
    @ApiOperation(value= "Checks the token of given users reset link.",
    notes = "A token will be provided",
    response = Boolean.class)
    public Boolean resetPassword (@ApiParam(value = "Reset token of the user.")
                                  @RequestParam(value="token") String token) {
        Optional<Player> player = playerRepository.findByResetPasswordToken(token);
        if (player.isPresent() ) {
            return Boolean.TRUE;
        }
        else {
            return Boolean.FALSE;
        }
    }

    @PostMapping("/resetpassword")
    @ApiOperation(value= "Update the password of given token's users password.",
    notes = "A token and password will be provided.",
    response = Boolean.class)
    public Boolean updatePassword (@ApiParam(value = "Reset token of the user.")
                                  @RequestParam(value="token") String token,
                                   @ApiParam(value = "New password of the user")
                                   @RequestParam(value="password") String password) {
        try {
            Player player = playerService.getByResetPasswordToken(token);
            Long id = player.getId();
            Player requestPlayer = new Player();
            requestPlayer.setPassword(password);
            playerService.updatePlayer(requestPlayer, id);

        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Boolean.TRUE;
    }
}
