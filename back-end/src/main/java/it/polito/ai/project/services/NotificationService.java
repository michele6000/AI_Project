package it.polito.ai.project.services;

import it.polito.ai.project.dtos.TeamDTO;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public interface NotificationService {
    void sendMessage(String address, String subject, String body);

    boolean confirm(String token, String username);

    boolean reject(String token);

    void notifyTeam(TeamDTO dto, List<String> memberIds, Timestamp expiryDate);

    AtomicBoolean isEverythingOk();
}
