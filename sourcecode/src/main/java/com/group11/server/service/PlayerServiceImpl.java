package com.group11.server.service;

import com.group11.server.model.Player;
import com.group11.server.repository.PlayerRepository;
import com.group11.server.utils.JwtUtil;
import lombok.AllArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

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
            if(!passwordEncoder.matches(requestPlayer.getPassword(), optPlayer.get().getPassword())) {
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
            player.setResetPasswordToken(null);
            playerRepository.save(player);

            return "Player is updated successfully.";
        }
        return "Player is not found, please check the input fields and id.";
    }

    @Override
    public void updateResetPasswordToken(String token, String username) throws Exception {
        Optional<Player> player = playerRepository.findByUsername(username);
        if  (player.isPresent()) {
            player.get().setResetPasswordToken(token);
            playerRepository.save(player.get());
        }
        else {
            throw new Exception("Couldn't find a user by given username.");
        }
    }

    @Override
    public Player getByResetPasswordToken(String token) throws Exception {
        Optional<Player> player = playerRepository.findByResetPasswordToken(token);
        if (player.isPresent()) {
            return player.get();
        }
        else {
            throw new Exception("Couldn't get by the given token. ");
        }
    }

    @Override
    public ResponseEntity<?> forgotPassword(Player requestPlayer) {
        if (requestPlayer.getUsername().isEmpty()) {
            return ResponseEntity.status(400).body("Username cannot be empty.");
        }
        if (requestPlayer.getEmail().isEmpty()) {
            return ResponseEntity.status(400).body("Email cannot be empty.");
        }
        try {
            Optional<Player> optPlayer = playerRepository.findByUsername(requestPlayer.getUsername());
            if (optPlayer.isEmpty()) {
                return ResponseEntity.status(403).body("Incorrect username or email");
            }
            if(!requestPlayer.getEmail().matches(optPlayer.get().getEmail())) {
                return ResponseEntity.status(403).body("Username and email doesn't match.");
            }
            Player player = optPlayer.get();
            String username = player.getUsername();
            String token = RandomString.make(30);
            updateResetPasswordToken(token, username);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(player.getEmail());
            message.setSubject("Your Password Reset Token ");
            message.setFrom("453group11mails@gmail.com"); //ADD A PROPER EMAIL
            message.setText("In order to reset your password, please enter following code to reset password page: "
                    +token);

            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost("smtp.gmail.com");
            mailSender.setPort(587);

            mailSender.setUsername("453group11mails@gmail.com");
            mailSender.setPassword("uzkohssewcfkzrim");

            Properties props = mailSender.getJavaMailProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.debug", "true");

            mailSender.send(message);

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

    @Override
    public ResponseEntity<?> updatePassword(Player requestPlayer) {
        if (requestPlayer.getUsername().isEmpty()) {
            return ResponseEntity.status(400).body("Username cannot be empty.");
        }
        if (requestPlayer.getPassword().isEmpty()) {
            return ResponseEntity.status(400).body("Password cannot be empty.");
        }
        if (requestPlayer.getResetPasswordToken().isEmpty()) {
            return ResponseEntity.status(400).body("Reset token cannot be empty.");
        }
        try {
            Optional<Player> optPlayer = playerRepository.findByUsername(requestPlayer.getUsername());
            if (optPlayer.isEmpty()) {
                return ResponseEntity.status(403).body("Incorrect username");
            }
            if(!requestPlayer.getResetPasswordToken().matches(optPlayer.get().getResetPasswordToken())) {
                return ResponseEntity.status(403).body("Username and token doesn't match.");
            }

            updatePlayer(requestPlayer, optPlayer.get().getId());

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


}
