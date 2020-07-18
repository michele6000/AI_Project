package it.polito.ai.lab3.services;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import it.polito.ai.lab3.dtos.CourseDTO;
import it.polito.ai.lab3.dtos.ProfessorDTO;
import it.polito.ai.lab3.dtos.StudentDTO;
import it.polito.ai.lab3.dtos.TeamDTO;
import it.polito.ai.lab3.entities.*;
import it.polito.ai.lab3.exceptions.CourseNotFoundException;
import it.polito.ai.lab3.exceptions.StudentNotFoundException;
import it.polito.ai.lab3.exceptions.TeamServiceException;
import it.polito.ai.lab3.repositories.*;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class TeamServiceImpl implements TeamService {
  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private CourseRepository courseRepo;

  @Autowired
  private StudentRepository studentRepo;

  @Autowired
  private TeamRepository teamRepo;

  @Autowired
  private UserRepository userRepo;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private ProfessorRepository profRepo;

  @Autowired
  private NotificationService notification;

  @Override
  public boolean addCourse(CourseDTO course) {
    if (course == null || course.getName().length() == 0) return false;
    Course courseEntity = modelMapper.map(course, Course.class);
    if (courseRepo.existsById(courseEntity.getName())) return false;
    courseRepo.save(courseEntity);
    return true;
  }

  @Override
  public boolean addStudent(StudentDTO student) {
    if (
      student == null ||
      student.getId().length() == 0 ||
      student.getName().length() == 0 ||
      student.getFirstName().length() == 0
    ) return false;
    Student studentEntity = modelMapper.map(student, Student.class);
    if (studentRepo.existsById(studentEntity.getId())) return false;

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
    user.setRoles(Collections.singletonList("ROLE_STUDENT"));
    User u = userRepo.save(modelMapper.map(user, User.class));

    studentRepo.save(studentEntity);
    notification.sendMessage(
      "paola.caso96@gmail.com",
      "Nuova Registrazione",
      "Username: " + user.getUsername() + " password: " + password
    );
    return true;
  }

  @Override
  public boolean addStudentToCourse(String studentId, String courseName) {
    if (studentId.length() == 0 || courseName.length() == 0) return false;
    if (!studentRepo.existsById(studentId)) throw new StudentNotFoundException(
      "Student not found!"
    );
    if (!courseRepo.existsById(courseName)) throw new CourseNotFoundException(
      "Course not found!"
    );

    Course course = courseRepo.getOne(courseName);
    Student student = studentRepo.getOne(studentId);

    if (!course.isEnabled()) return false;

    Optional<Student> found = course
      .getStudents()
      .stream()
      .filter(s -> s.getId().equals(studentId))
      .findFirst();
    if (found.isPresent()) return false;

    course.addStudent(student);

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
  public List<CourseDTO> getAllCourses() {
    return courseRepo
      .findAll()
      .stream()
      .map(c -> modelMapper.map(c, CourseDTO.class))
      .collect(Collectors.toList());
  }

  @Override
  public Optional<StudentDTO> getStudent(String studentId) {
    if (!studentRepo.existsById(studentId)) return Optional.empty();

    return Optional.of(
      modelMapper.map(studentRepo.getOne(studentId), StudentDTO.class)
    );
  }

  @Override
  public List<StudentDTO> getAllStudents() {
    return studentRepo
      .findAll()
      .stream()
      .map(s -> modelMapper.map(s, StudentDTO.class))
      .collect(Collectors.toList());
  }

  @Override
  public List<StudentDTO> getEnrolledStudents(String courseName) {
    if (!courseRepo.existsById(courseName)) throw new CourseNotFoundException(
      "Course not found!"
    );

    return courseRepo
      .getOne(courseName)
      .getStudents()
      .stream()
      .map(s -> modelMapper.map(s, StudentDTO.class))
      .collect(Collectors.toList());
  }

  @Override
  public void enableCourse(String courseName) {
    if (courseRepo.existsById(courseName)) courseRepo
      .getOne(courseName)
      .setEnabled(true); else throw new CourseNotFoundException(
      "Course not found!"
    );
  }

  @Override
  public void disableCourse(String courseName) {
    if (courseRepo.existsById(courseName)) courseRepo
      .getOne(courseName)
      .setEnabled(false); else throw new CourseNotFoundException(
      "Course not found!"
    );
  }

  @Override
  public List<Boolean> addAll(List<StudentDTO> students) {
    List<Boolean> studentsAdded = new ArrayList<>();
    students.forEach(s -> studentsAdded.add(addStudent(s)));
    return studentsAdded;
  }

  @Override
  public List<Boolean> enrollAll(List<String> studentIds, String courseName) {
    List<Boolean> studentsEnrolled = new ArrayList<>();
    studentIds.forEach(
      s -> {
        try {
          studentsEnrolled.add(addStudentToCourse(s, courseName));
        } catch (StudentNotFoundException | CourseNotFoundException e) {
          throw e;
        }
      }
    );
    return studentsEnrolled;
  }

  @Override
  public List<Boolean> addAndEnroll(Reader r, String courseName) {
    CsvToBean<StudentDTO> csvToBean = new CsvToBeanBuilder<StudentDTO>(r)
      .withType(StudentDTO.class)
      .withIgnoreLeadingWhiteSpace(true)
      .build();

    List<StudentDTO> students = csvToBean.parse();

    List<Boolean> added = addAll(students);

    List<String> studentIds = students
      .stream()
      .map(s -> s.getId())
      .collect(Collectors.toList());
    try {
      List<Boolean> enrolled = enrollAll(studentIds, courseName);
      List<Boolean> addedAndEnrolled = new ArrayList<>();

      for (int i = 0; i < added.size(); i++) addedAndEnrolled.add(
        added.get(i) || enrolled.get(i)
      );

      return addedAndEnrolled;
    } catch (StudentNotFoundException | CourseNotFoundException e) {
      throw e;
    }
  }

  @Override
  public List<CourseDTO> getCourses(String studentId) {
    if (!studentRepo.existsById(studentId)) throw new StudentNotFoundException(
      "Student not found!"
    );

    return studentRepo
      .getOne(studentId)
      .getCourses()
      .stream()
      .map(c -> modelMapper.map(c, CourseDTO.class))
      .collect(Collectors.toList());
  }

  @Override
  public List<TeamDTO> getTeamsForStudent(String studentId) {
    if (!studentRepo.existsById(studentId)) throw new StudentNotFoundException(
      "Student not found!"
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
    if (!teamRepo.existsById(teamId)) throw new TeamServiceException(
      "Team not found!"
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
    String courseId,
    String name,
    List<String> membersIds
  ) {
    if (!courseRepo.existsById(courseId)) throw new CourseNotFoundException(
      "Course not found!"
    );
    if (
      !courseRepo.getOne(courseId).isEnabled()
    ) throw new TeamServiceException("Course not enabled!");
    if (
      membersIds.size() > courseRepo.getOne(courseId).getMax()
    ) throw new TeamServiceException("Too many members for this team!");
    if (
      membersIds.size() < courseRepo.getOne(courseId).getMin()
    ) throw new TeamServiceException("Too few members for this team!");

    if (
      membersIds.size() > membersIds.stream().distinct().count()
    ) throw new TeamServiceException("Duplicated members in team proposal!");

    for (String m : membersIds) {
      if (!studentRepo.existsById(m)) throw new StudentNotFoundException(
        "Student not found!"
      );
      if (
        !courseRepo
          .getOne(courseId)
          .getStudents()
          .contains(studentRepo.getOne(m))
      ) throw new TeamServiceException("Student not enrolled in this course!");
      courseRepo
        .getOne(courseId)
        .getTeams()
        .forEach(
          t -> {
            if (
              t.getMembers().contains(studentRepo.getOne(m))
            ) throw new TeamServiceException(
              "Student " +
              m +
              " is already member of a team (" +
              t.getName() +
              ") for this course!"
            );
          }
        );
    }

    Team team = new Team();
    team.setName(name);
    membersIds.forEach(
      m -> {
        team.addMember(studentRepo.getOne(m));
      }
    );
    team.setCourse(courseRepo.getOne(courseId));
    team.setId((teamRepo.save(team).getId()));

    return modelMapper.map(team, TeamDTO.class);
  }

  @Override
  public List<TeamDTO> getTeamForCourse(String courseName) {
    if (!courseRepo.existsById(courseName)) throw new CourseNotFoundException(
      "Course not found!"
    );

    return courseRepo
      .getOne(courseName)
      .getTeams()
      .stream()
      .map(t -> modelMapper.map(t, TeamDTO.class))
      .collect(Collectors.toList());
  }

  @Override
  public List<StudentDTO> getStudentsInTeams(String courseName) {
    if (!courseRepo.existsById(courseName)) throw new CourseNotFoundException(
      "Course not found!"
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
      "Course not found!"
    );

    return courseRepo
      .getStudentsNotInTeams(courseName)
      .stream()
      .map(s -> modelMapper.map(s, StudentDTO.class))
      .collect(Collectors.toList());
  }

  @Override
  public void setActive(Long teamId) {
    if (!teamRepo.existsById(teamId)) throw new TeamServiceException(
      "Team not found!"
    );

    teamRepo.getOne(teamId).setStatus(1);
  }

  @Override
  public void evictTeam(Long teamId) {
    if (!teamRepo.existsById(teamId)) throw new TeamServiceException(
      "Team not found!"
    );

    teamRepo
      .getOne(teamId)
      .getMembers()
      .forEach(s -> teamRepo.getOne(teamId).removeMember(s));
    teamRepo.deleteById(teamId);
  }

  @Override
  public boolean addProfessor(ProfessorDTO professor) {
    if (professor == null) {
      return false;
    }
    String id = professor.getId();
    if (profRepo.findById(id).isPresent()) {
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
    user.setUsername(id + "@polito.it");
    user.setPassword(passwordEncoder.encode(password));
    user.setRoles(Collections.singletonList("ROLE_PROFESSOR"));
    userRepo.save(modelMapper.map(user, User.class));
    profRepo.save(modelMapper.map(professor, Professor.class));
    notification.sendMessage(
      user.getUsername(),
      "New user",
      "Username: " + user.getUsername() + "\nPassword: " + password
    );
    return true;
  }

  @Override
  public boolean addProfessorToCourse(String professorId, String courseName) {
    Optional<Course> optionalCourseEntity = courseRepo.findById(courseName);
    if (!optionalCourseEntity.isPresent()) {
      throw new CourseNotFoundException("Course not found!");
    }
    Optional<Professor> optionalProfessorEntity = profRepo.findById(
      professorId
    );
    if (!optionalProfessorEntity.isPresent()) {
      throw new StudentNotFoundException("Professor not found!");
    }
    if (optionalCourseEntity.get().isEnabled()) {
      optionalCourseEntity.get().addProfessor(optionalProfessorEntity.get());
      return true;
    } else throw new TeamServiceException("Course not enabled");
  }

  @Override
  public List<ProfessorDTO> getProfessors(String courseName) {
    Optional<Course> optionalCourseEntity = courseRepo.findById(courseName);
    if (!optionalCourseEntity.isPresent()) {
      throw new CourseNotFoundException("Course not found!");
    }
    return courseRepo
      .getOne(courseName)
      .getProfessors()
      .stream()
      .map(s -> modelMapper.map(s, ProfessorDTO.class))
      .collect(Collectors.toList());
  }
}
