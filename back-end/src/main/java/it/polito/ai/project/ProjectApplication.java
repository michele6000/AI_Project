package it.polito.ai.project;

import it.polito.ai.project.entities.User;
import it.polito.ai.project.repositories.UserRepository;
import java.util.Collections;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ProjectApplication {
  private static final String CSV_FILE_PATH = "fileTest.csv";

  public static void main(String[] args) {
    SpringApplication.run(ProjectApplication.class, args);
  }

  @Bean
  public CommandLineRunner adminCreator(
    UserRepository userRepo,
    PasswordEncoder passwordEncoder
  ) {
    return args -> {
      if (!userRepo.existsById("admin")) {
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setRoles(Collections.singletonList("ROLE_ADMIN"));
        userRepo.save(admin);
      }
    };
  }

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
