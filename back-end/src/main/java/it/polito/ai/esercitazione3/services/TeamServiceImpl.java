package it.polito.ai.esercitazione3.services;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import it.polito.ai.esercitazione3.dtos.CourseDTO;
import it.polito.ai.esercitazione3.dtos.ProfessorDTO;
import it.polito.ai.esercitazione3.dtos.StudentDTO;
import it.polito.ai.esercitazione3.dtos.TeamDTO;
import it.polito.ai.esercitazione3.entities.*;
import it.polito.ai.esercitazione3.exceptions.CourseNotFoundException;
import it.polito.ai.esercitazione3.exceptions.StudentNotFoundException;
import it.polito.ai.esercitazione3.exceptions.TeamNotFoundException;
import it.polito.ai.esercitazione3.exceptions.TeamServiceException;
import it.polito.ai.esercitazione3.repositories.*;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class TeamServiceImpl implements TeamService {
  @Autowired
  ModelMapper modelMapper;

  @Autowired
  CourseRepository courseRepo;

  @Autowired
  StudentRepository studentRepo;

  @Autowired
  TeamRepository teamRepo;

  @Autowired
  UserRepository users;

  @Autowired
  ProfessorRepository professors;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Autowired
  NotificationService notify;

  @Value("${service.sendTo}")
  private String sendTo;

  @Override
  public boolean addCourse(CourseDTO course) {
    if (course == null) return false;
    Course _course = modelMapper.map(course, Course.class);
    if (courseRepo.existsById(_course.getName())) return false;
    courseRepo.save(_course);
    return true;
  }

  @Override
  public boolean addStudent(StudentDTO student) {
    if (student == null) return false;
    Student _student = modelMapper.map(student, Student.class);
    if (studentRepo.existsById(_student.getId())) return false;

    User user = new User();
    Random random = new Random();
    String password = random
      .ints(97, 122)
      .limit(8)
      .collect(
        StringBuilder::new,
        StringBuilder::appendCodePoint,
        StringBuilder::append
      )
      .toString();
    user.setUsername(student.getId() + "@studenti.polito.it");
    user.setPassword(passwordEncoder.encode(password));
    user.setRole("ROLE_STUDENT");
    User u = users.save(modelMapper.map(user, User.class));
    studentRepo.save(_student);
    notify.sendMessage(
      "grecomichele96@gmail.com",
      "Nuova Registrazione",
      "Username: " + user.getUsername() + " password: " + password
    );

    return true;
  }

  @Override
  public boolean addStudentToCourse(String studentId, String courseName) {
    if (studentId == null || courseName == null) return false;
    if (
      !studentRepo.findById(studentId).isPresent()
    ) throw new StudentNotFoundException("Studente non esistente!");
    if (!courseRepo.existsById(courseName)) throw new CourseNotFoundException(
      "Corso non esistente!"
    );
    if (!courseRepo.getOne(courseName).isEnabled()) return false;
    Course _course = courseRepo.getOne(courseName);

    if (!_course.isEnabled()) return false;

    Optional<Student> found = _course
      .getStudents()
      .stream()
      .filter(s -> s.getId().equals(studentId))
      .findFirst();
    if (found.isPresent()) return false;

    Student _student = studentRepo.getOne(studentId);
    _course.addStudent(_student);
    return true;
  }

  @Override
  public Optional<CourseDTO> getCourse(String name) {
    if (!courseRepo.existsById(name)) return Optional.empty();
    return Optional.of(
      modelMapper.map(courseRepo.getOne(name), CourseDTO.class)
    );
  }

  @Override
  public Optional<StudentDTO> getStudent(String studentId) {
    if (!studentRepo.existsById(studentId)) return Optional.empty();
    return Optional.of(
      modelMapper.map(studentRepo.getOne(studentId), StudentDTO.class)
    );
  }

  @Override
  public List<CourseDTO> getAllCourses() {
    return courseRepo
      .findAll()
      .stream()
      .map(l -> modelMapper.map(l, CourseDTO.class))
      .collect(Collectors.toList());
  }

  @Override
  public List<StudentDTO> getAllStudents() {
    return studentRepo
      .findAll()
      .stream()
      .map(l -> modelMapper.map(l, StudentDTO.class))
      .collect(Collectors.toList());
  }

  @Override
  public List<StudentDTO> getEnrolledStudents(String courseName) {
    if (!courseRepo.existsById(courseName)) throw new CourseNotFoundException(
      "Corso non esistente!"
    );
    return courseRepo
      .getOne(courseName)
      .getStudents()
      .stream()
      .map(l -> modelMapper.map(l, StudentDTO.class))
      .collect(Collectors.toList());
  }

  @Override
  public void enableCourse(String courseName) {
    if (!courseRepo.existsById(courseName)) throw new CourseNotFoundException(
      "Corso non esistente!"
    );
    if (courseRepo.getOne(courseName).isEnabled()) return;
    courseRepo.getOne(courseName).setEnabled(true);
  }

  @Override
  public void disableCourse(String courseName) {
    if (!courseRepo.existsById(courseName)) throw new CourseNotFoundException(
      "Corso non esistente!"
    );
    if (!courseRepo.getOne(courseName).isEnabled()) return;
    courseRepo.getOne(courseName).setEnabled(false);
  }

  @Override
  public List<Boolean> addAll(List<StudentDTO> students) {
    List<Boolean> tmp = new ArrayList<>();
    students.forEach(
      s -> {
        tmp.add(this.addStudent(s));
      }
    );
    return tmp;
  }

  @Override
  public List<Boolean> enrollAll(List<String> studentsIds, String courseName) {
    List<Boolean> tmp = new ArrayList<>();
    studentsIds.forEach(
      s -> {
        try {
          tmp.add(this.addStudentToCourse(s, courseName));
        } catch (StudentNotFoundException | CourseNotFoundException e) {
          throw e;
        }
      }
    );
    return tmp;
  }

  @Override
  public List<Boolean> addAndEnroll(Reader r, String courseName) {
    List<Boolean> ret = new ArrayList<>();
    CsvToBean<StudentDTO> csvToBean = new CsvToBeanBuilder<StudentDTO>(r)
      .withType(StudentDTO.class)
      .withIgnoreLeadingWhiteSpace(true)
      .build();
    List<StudentDTO> students = csvToBean.parse();
    students.forEach(
      s -> {
        try {
          ret.add(
            this.addStudent(s) || this.addStudentToCourse(s.getId(), courseName)
          );
        } catch (StudentNotFoundException | CourseNotFoundException e) {
          throw e;
        }
      }
    );
    return ret;
  }

  @Override
  public List<CourseDTO> getCourses(String studentId) {
    if (!studentRepo.existsById(studentId)) throw new StudentNotFoundException(
      "Studente non esistente!"
    );
    return studentRepo
      .getOne(studentId)
      .getCourses()
      .stream()
      .map(l -> modelMapper.map(l, CourseDTO.class))
      .collect(Collectors.toList());
  }

  @Override
  public List<TeamDTO> getTeamsForStudent(String studentId) {
    if (!studentRepo.existsById(studentId)) throw new StudentNotFoundException(
      "Studente non esistente!"
    );
    return studentRepo
      .getOne(studentId)
      .getTeams()
      .stream()
      .map(t -> modelMapper.map(t, TeamDTO.class))
      .collect(Collectors.toList());
  }

  @Override
  public List<StudentDTO> getMembers(Long teamId) {
    if (!teamRepo.existsById(teamId)) throw new TeamNotFoundException(
      "Team non esistente!"
    );
    return teamRepo
      .getOne(teamId)
      .getMembers()
      .stream()
      .map(s -> modelMapper.map(s, StudentDTO.class))
      .collect(Collectors.toList());
  }

  @Override
  public TeamDTO proposeTeam(
    String courseName,
    String name,
    List<String> membersIds
  ) {
    if (!courseRepo.existsById(courseName)) throw new CourseNotFoundException(
      "Corso non esistente!"
    );

    Course crs = courseRepo.getOne(courseName);

    if (
      membersIds.size() > crs.getMax() || membersIds.size() < crs.getMin()
    ) throw new TeamServiceException(
      "Non sono rispettati i vincoli di dimensione!"
    );

    if (
      membersIds.size() != membersIds.stream().distinct().count()
    ) throw new TeamServiceException("Il gruppo proposto contiene duplicati!");

    membersIds.forEach(
      studentId -> {
        //Lo studente esiste
        if (
          !studentRepo.existsById(studentId)
        ) throw new StudentNotFoundException("Studente non esistente!");

        Student tmp = studentRepo.getOne(studentId);

        //Lo studente è iscritto al corso
        if (!crs.getStudents().contains(tmp)) throw new TeamServiceException(
          "Lo studente " + studentId + " non appartiene al corso!"
        );

        //Lo studente non sia iscritto ad altri gruppi
        crs
          .getTeams()
          .forEach(
            t -> {
              if (t.getMembers().contains(tmp)) throw new TeamServiceException(
                "Lo studente " +
                studentId +
                " appartiene già al team: " +
                t.getName()
              );
            }
          );
      }
    );

    if (
      !courseRepo.getOne(courseName).isEnabled()
    ) throw new TeamServiceException("Il corso è disabilitato!");

    Team team = new Team();
    team.setName(name);
    team.setCourse(crs);
    membersIds.forEach(
      s -> {
        team.addMember(studentRepo.getOne(s));
      }
    );
    team.setId(teamRepo.save(team).getId());

    return modelMapper.map(team, TeamDTO.class);
  }

  @Override
  public List<TeamDTO> getTeamForCourse(String courseName) {
    if (!courseRepo.existsById(courseName)) throw new CourseNotFoundException(
      "Corso non esistente!"
    );
    return courseRepo
      .getOne(courseName)
      .getTeams()
      .stream()
      .map(l -> modelMapper.map(l, TeamDTO.class))
      .collect(Collectors.toList());
  }

  @Override
  public List<StudentDTO> getStudentsInTeams(String courseName) {
    if (!courseRepo.existsById(courseName)) throw new CourseNotFoundException(
      "Corso non esistente!"
    );
    return courseRepo
      .getStudentsInTeams(courseName)
      .stream()
      .map(s -> modelMapper.map(s, StudentDTO.class))
      .collect(Collectors.toList());
  }

  @Override
  public List<StudentDTO> getAvailableStudents(String courseName) {
    if (!courseRepo.existsById(courseName)) throw new CourseNotFoundException(
      "Corso non esistente!"
    );
    return courseRepo
      .getStudentsNotInTeams(courseName)
      .stream()
      .map(s -> modelMapper.map(s, StudentDTO.class))
      .collect(Collectors.toList());
  }

  @Override
  public boolean setActive(Long id) {
    if (teamRepo.existsById(id)) {
      teamRepo.getOne(id).setStatus(1);
      return true;
    }
    return false;
  }

  @Override
  public boolean evictTeam(Long id) {
    if (teamRepo.existsById(id)) {
      teamRepo.deleteById(id);
      return true;
    }
    return false;
  }

  @Override
  public boolean addProfessor(ProfessorDTO professor) {
    if (professor == null) {
      return false;
    }
    String id = professor.getId();
    if (professors.findById(id).isPresent()) {
      return false;
    }
    User user = new User();
    Random random = new Random();
    String password = random
      .ints(97, 122)
      .limit(8)
      .collect(
        StringBuilder::new,
        StringBuilder::appendCodePoint,
        StringBuilder::append
      )
      .toString();
    user.setUsername(professor.getId() + "@polito.it");
    user.setPassword(passwordEncoder.encode(password));
    user.setRole("ROLE_PROFESSOR");
    User u = users.save(modelMapper.map(user, User.class));
    professors.save(modelMapper.map(professor, Professor.class));
    notify.sendMessage(
      sendTo,
      "Nuova Registrazione",
      "Username: " + user.getUsername() + "<br>Password: " + password
    );
    return true;
  }

  @Override
  public boolean addProfessorToCourse(String professorId, String courseName) {
    Optional<Course> optionalCourseEntity = courseRepo.findById(courseName);
    if (!optionalCourseEntity.isPresent()) {
      throw new TeamServiceException("Corso " + courseName + " non esistente");
    }
    Optional<Professor> optionalProfessorEntity = professors.findById(
      professorId
    );
    if (!optionalProfessorEntity.isPresent()) {
      throw new TeamServiceException(
        "Professore con id " + professorId + " non esistente"
      );
    }
    if (optionalCourseEntity.get().isEnabled()) {
      return optionalCourseEntity
        .get()
        .addProfessor(optionalProfessorEntity.get());
    } else {
      throw new TeamServiceException("Corso " + courseName + " non abilitato");
    }
  }

  @Override
  public List<ProfessorDTO> getProfessors(String courseName) {
    if (!courseRepo.existsById(courseName)) {
      throw new TeamServiceException("Corso " + courseName + " non esistente");
    }
    return courseRepo
      .getOne(courseName)
      .getProfessors()
      .stream()
      .map(s -> modelMapper.map(s, ProfessorDTO.class))
      .collect(Collectors.toList());
  }
}
