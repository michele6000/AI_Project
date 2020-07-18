package it.polito.ai.lab3.services;

import it.polito.ai.lab3.dtos.TeamDTO;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public interface NotificationService {
  void sendMessage(String address, String subject, String body);

  boolean confirm(String token);

  boolean reject(String token);

  void notifyTeam(TeamDTO dto, List<String> memberIds);

  void deleteExpiredTokens();

  AtomicBoolean isEverythingOk();
}
