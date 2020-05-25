package it.polito.ai.esercitazione3.repositories;

import it.polito.ai.esercitazione3.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
  User findByUsername(String username);
}
