package it.polito.ai.project.services;

import it.polito.ai.project.dtos.TeamDTO;
import it.polito.ai.project.entities.Token;
import it.polito.ai.project.exceptions.TeamServiceException;
import it.polito.ai.project.repositories.StudentRepository;
import it.polito.ai.project.repositories.TeamRepository;
import it.polito.ai.project.repositories.TokenRepository;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {
    static final String URL_BASE = "http://localhost:8080/API";
    @Autowired
    public JavaMailSender emailSender;
    AtomicBoolean everythingOk = new AtomicBoolean();
    @Autowired
    private TokenRepository tokenRepo;

    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private TeamRepository teamRepo;

    @Autowired
    private TeamService teamService;

    @Value("${service.address}")
    private String address;

    @Override
    public void sendMessage(String address, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        //message.setTo(address);
        message.setTo(this.address);
        message.setSubject(subject);
        message.setText(body);
        //emailSender.send(message);
    }

    @Scheduled(fixedRate = 600000)
    public void deleteExpiredTokens() {
        tokenRepo
                .findAllByExpiryDateBefore(new Timestamp(new Date().getTime()))
                .forEach(
                        t -> {
                            teamService.evictTeam(t.getTeamId());
                            tokenRepo.delete(t);
                        }
                );
    }

    @Override
    public boolean confirm(String token, String username) {
        everythingOk.set(false);

        //token not existent
        if (!tokenRepo.findById(token).isPresent())
            return false;

        Long teamId = tokenRepo.getOne(token).getTeamId();

        //token expired
        if (tokenRepo.findAllByExpiryDateBefore(new Timestamp(new Date().getTime()))
                .stream()
                .anyMatch(t -> t.getId().equals(token))
        ) {
            teamService.evictTeam(teamId);
            return false;
        }

        tokenRepo.deleteById(token);

        if (tokenRepo.findAllByTeamId(teamId).size() == 0) {
            try {
                teamRepo.getOne(teamId).getPendentStudents().remove(studentRepo.getOne(username));
                teamRepo.getOne(teamId).getConfirmedStudents().add(studentRepo.getOne(username));
                teamService.setActive(teamId);
                return true;
            } catch (TeamServiceException e) {
                throw e;
            }
        } else everythingOk.set(true);
        teamRepo.getOne(teamId).getPendentStudents().remove(studentRepo.getOne(username));
        teamRepo.getOne(teamId).getConfirmedStudents().add(studentRepo.getOne(username));
        return false;
    }

    @Override
    public boolean reject(String token) {
        //token not existent
        if (!tokenRepo.findById(token).isPresent()) return false;
        //token expired
        if (tokenRepo
                .findAllByExpiryDateBefore(new Timestamp(new Date().getTime()))
                .stream()
                .anyMatch(t -> t.getId().equals(token))
        ) return false;

        Long teamId = tokenRepo.getOne(token).getTeamId();
        tokenRepo.findAllByTeamId(teamId).forEach(t -> tokenRepo.delete(t));
        try {
            teamService.evictTeam(teamId);
            return true;
        } catch (TeamServiceException e) {
            throw e;
        }
    }

    @Override
    public void notifyTeam(TeamDTO dto, List<String> memberIds, Timestamp expiryDate) {
        Long teamId = dto.getId();

        for (String m : memberIds) {
            String id = UUID.randomUUID().toString();
            Token token = new Token();
            token.setId(id);
            token.setTeamId(teamId);
            token.setExpiryDate(expiryDate);
            tokenRepo.save(token);
            String address = m + "@studenti.polito.it";
            String body =
                    "CONFIRM participation to the team: " +
                            URL_BASE +
                            "/notifications/confirm/" +
                            id + "?id=" + m +
                            "\nREJECT participation to the team: " +
                            URL_BASE +
                            "/notifications/reject/" +
                            id + "?id=" + m;
            this.sendMessage(address, "Team proposal", body);
        }
    }

    public AtomicBoolean isEverythingOk() {
        return everythingOk;
    }
}
