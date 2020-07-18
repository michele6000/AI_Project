package it.polito.ai.lab3.services;

import it.polito.ai.lab3.dtos.CourseDTO;
import it.polito.ai.lab3.dtos.ProfessorDTO;
import it.polito.ai.lab3.dtos.StudentDTO;
import it.polito.ai.lab3.dtos.TeamDTO;
import java.io.Reader;
import java.util.List;
import java.util.Optional;
import org.springframework.security.access.prepost.PreAuthorize;

public interface TeamService {
  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PROFESSOR')")
  boolean addCourse(CourseDTO course);

  @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN','ROLE_PROFESSOR')")
  Optional<CourseDTO> getCourse(String name);

  @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN','ROLE_PROFESSOR')")
  List<CourseDTO> getAllCourses();

  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR')")
  boolean addStudent(StudentDTO student);

  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR')")
  Optional<StudentDTO> getStudent(String studentId);

  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR')")
  List<StudentDTO> getAllStudents();

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PROFESSOR')")
  List<StudentDTO> getEnrolledStudents(String courseName);

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PROFESSOR')")
  boolean addStudentToCourse(String studentId, String courseName);

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PROFESSOR')")
  void enableCourse(String courseName);

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PROFESSOR')")
  void disableCourse(String courseName);

  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR')")
  List<Boolean> addAll(List<StudentDTO> students);

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PROFESSOR')")
  List<Boolean> enrollAll(List<String> studentsIds, String courseName);

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PROFESSOR')")
  List<Boolean> addAndEnroll(Reader r, String courseName);

  @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN')")
  List<CourseDTO> getCourses(String studentId);

  @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN')")
  List<TeamDTO> getTeamsForStudent(String studentId);

  @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN', 'ROLE_PROFESSOR')")
  List<StudentDTO> getMembers(Long teamId);

  @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN')")
  TeamDTO proposeTeam(String courseId, String name, List<String> membersIds);

  @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN', 'ROLE_PROFESSOR')")
  List<TeamDTO> getTeamForCourse(String courseName);

  @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN', 'ROLE_PROFESSOR')")
  List<StudentDTO> getStudentsInTeams(String courseName);

  @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN', 'ROLE_PROFESSOR')")
  List<StudentDTO> getAvailableStudents(String courseName);

  void setActive(Long id);

  void evictTeam(Long id);

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  boolean addProfessor(ProfessorDTO professor);

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  boolean addProfessorToCourse(String professorId, String courseName);

  @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN', 'ROLE_PROFESSOR')")
  List<ProfessorDTO> getProfessors(String courseName);
}
