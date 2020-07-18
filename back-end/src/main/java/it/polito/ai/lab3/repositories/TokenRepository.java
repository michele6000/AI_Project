package it.polito.ai.lab3.repositories;

import it.polito.ai.lab3.entities.Token;
import java.sql.Timestamp;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {
  List<Token> findAllByExpiryDateBefore(Timestamp t);

  List<Token> findAllByTeamId(Long teamId);
}
