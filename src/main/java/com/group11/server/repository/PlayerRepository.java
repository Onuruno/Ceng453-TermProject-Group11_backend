package com.group11.server.repository;
import com.group11.server.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    /**
     * This method overloads findByUsername function to return Optional<User>
     * It is added to return a non null valid object as checker for database answer.
     *
     * @param username Name of the player which is wanted.
     * @return an Optional object that may or not hold Player which is checkable
     */
    Optional<Player> findByUsername(String username);
}
