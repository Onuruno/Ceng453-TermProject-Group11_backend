package com.group11.server.service;

import com.group11.server.model.Player;
import com.group11.server.repository.PlayerRepository;
import com.group11.server.utils.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PlayerServiceImpl implements PlayerService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UsersDetailsService usersDetailsService;
    private final PlayerRepository playerRepository;
    private final JwtUtil jwtUtil;

    /**
     * This method logins player with given credentials
     *
     * @param requestPlayer Player information that will be used for login action
     * @return JwtResponse with the current session's JWTToken
     */
    @Override
    public ResponseEntity<?> login(Player requestPlayer) {
        if (requestPlayer.getUsername().isEmpty()) {
            return ResponseEntity.status(400).body("Username cannot be empty.");
        }
        if (requestPlayer.getPassword().isEmpty()) {
            return ResponseEntity.status(400).body("Password cannot be empty.");
        }
        try {
            Optional<Player> optPlayer = playerRepository.findByUsername(requestPlayer.getUsername());
            if (optPlayer.isEmpty()) {
                return ResponseEntity.status(403).body("Incorrect username or password");
            }
            Player player = optPlayer.get();
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(403).body(e.toString());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error" + e.toString());
        }

        final UserDetails userDetails = usersDetailsService
                .loadUserByUsername(requestPlayer.getUsername());

        //Creating authentication jwt token
        final String jwt = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(jwt);
    }

    /**
     * This method gets the ID of the player with given username.
     *
     * @param username Username of the player whose ID is wanted
     * @return The ID of the player with given username
     * @throws Exception if ID does not exist
     */
    @Override
    public Long getPlayerID(String username) throws Exception {
        Optional<Player> optPlayer = playerRepository.findByUsername(username);
        if (optPlayer.isEmpty()) throw new Exception("Player is not found.");

        Player player = optPlayer.get();
        return player.getId();
    }

    /**
     * This method gets all players in database.
     *
     * @return List of all players
     */
    @Override
    public List<Player> getAllPlayers() {
        return new ArrayList<>(playerRepository.findAll());
    }

    /**
     * This method gets player with given ID.
     *
     * @param Id ID of the player that is wanted
     * @return Player with the given ID
     * @throws Exception if ID does not exist
     */
    @Override
    public Player getPlayer(Long Id) throws Exception {
        Optional<Player> optPlayer = playerRepository.findById(Id);
        if (optPlayer.isEmpty()) throw new Exception("Player is not found");

        Player player = optPlayer.get();
        return player;
    }

    /**
     * This method registers the given player.
     * It saves new valid Player to database.
     * If player is not valid or credentials are bad, returns a message about that error.
     *
     * @param player Player information that will be registered
     * @return Response message of the server
     */
    @Override
    public ResponseEntity<?> register(Player player) {

        if (player.getUsername().isEmpty())
            return ResponseEntity.status(400).body("Username cannot be empty.");
        if (player.getPassword().isEmpty())
            return ResponseEntity.status(400).body("Password cannot be empty.");
        if (playerRepository.findByUsername(player.getUsername()).isPresent())
            return ResponseEntity.status(409).body("Username is already taken.");
        else {
            player.setPassword(passwordEncoder.encode(player.getPassword()));
            playerRepository.save(player);
            return ResponseEntity.ok("Player created successfully. Please log in");
        }
    }

    /**
     * This method updates information of the player with given ID.
     * If player is not valid or credentials are bad, returns a message about that error.
     *
     * @param requestPlayer User information that will be registered
     * @param Id ID of the player that will be updated
     * @return Response message of the server as String
     */
    @Override
    public String updatePlayer(Player requestPlayer, Long Id) {
        if (playerRepository.findById(Id).isPresent()) {
            Player player = playerRepository.findById(Id).get();

            player.setPassword(passwordEncoder.encode(requestPlayer.getPassword()));
            playerRepository.save(player);
            return "Player is updated successfully.";
        }
        return "Player is not found, please check the input fields and id.";
    }

}
