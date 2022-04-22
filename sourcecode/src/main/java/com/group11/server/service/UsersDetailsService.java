package com.group11.server.service;

import com.group11.server.model.Player;
import com.group11.server.repository.PlayerRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UsersDetailsService implements UserDetailsService {

    private final PlayerRepository playerRepository;

    /**
     * This method overrides the loadByUsername function to connect project's database to Spring Security user database.
     * It loads UserDetails in Spring Security User Database if user is registered in project's database.
     *
     * @param username Name of the player
     * @return UserDetails of the player with given username
     * @throws UsernameNotFoundException if username is not found on database.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<Player> optPlayer = playerRepository.findByUsername(username);
        if (optPlayer.isPresent()) {
            Player player = optPlayer.get();
            return new org.springframework.security.core.userdetails.User(player.getUsername(), player.getPassword(), new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("Username or password is incorrect");
        }
    }
}