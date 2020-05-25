package it.polito.ai.esercitazione3;

import it.polito.ai.esercitazione3.entities.User;
import it.polito.ai.esercitazione3.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableScheduling
public class Esercitazione3Application {

  public static void main(String[] args) {
    SpringApplication.run(Esercitazione3Application.class, args);
  }

  @Bean
  public CommandLineRunner adminCreator(
    UserRepository userRepo,
    PasswordEncoder encoder
  ) {
    return args -> {
      if (!userRepo.existsById("admin")) {
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(encoder.encode("admin"));
        admin.setRole("ROLE_ADMIN");
        userRepo.save(admin);
      }
    };
  }

  @Bean
  ModelMapper modelMapper() {
    return new ModelMapper();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
