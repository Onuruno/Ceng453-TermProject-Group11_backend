package com.group11.server.service;

import com.group11.server.model.Player;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PlayerService {
    ResponseEntity<?> login(Player requestPlayer);
    Long getPlayerID(String username) throws Exception;
    List<Player> getAllPlayers();
    Player getPlayer(Long Id) throws Exception;
    ResponseEntity<?> register(Player player);
    String updatePlayer(Player requestPlayer, Long Id);

    void updateResetPasswordToken(String token, String username) throws Exception;
    Player getByResetPasswordToken(String token) throws Exception;
}
