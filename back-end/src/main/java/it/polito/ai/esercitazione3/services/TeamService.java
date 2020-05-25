package it.polito.ai.esercitazione3.services;

import it.polito.ai.esercitazione3.dtos.CourseDTO;
import it.polito.ai.esercitazione3.dtos.ProfessorDTO;
import it.polito.ai.esercitazione3.dtos.StudentDTO;
import it.polito.ai.esercitazione3.dtos.TeamDTO;
import java.io.Reader;
import java.util.List;
import java.util.Optional;
import org.springframework.security.access.prepost.PreAuthorize;

public interface TeamService {
  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PROFESSOR')")
  boolean addCourse(CourseDTO course);

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PROFESSOR','ROLE_STUDENT')")
  Optional<CourseDTO> getCourse(String name);

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PROFESSOR')")
  List<CourseDTO> getAllCourses();

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PROFESSOR')")
  boolean addStudent(StudentDTO student);

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PROFESSOR','ROLE_STUDENT')")
  Optional<StudentDTO> getStudent(String studentId);

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PROFESSOR')")
  List<StudentDTO> getAllStudents();

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PROFESSOR')")
  List<StudentDTO> getEnrolledStudents(String courseName);

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PROFESSOR')")
  boolean addStudentToCourse(String studentId, String courseName);

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PROFESSOR')")
  void enableCourse(String courseName);

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PROFESSOR')")
  void disableCourse(String courseName);

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PROFESSOR')")
  List<Boolean> addAll(List<StudentDTO> students);

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PROFESSOR')")
  List<Boolean> enrollAll(List<String> studentsIds, String courseName);

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PROFESSOR')")
  List<Boolean> addAndEnroll(Reader r, String courseName);

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STUDENT')")
  List<CourseDTO> getCourses(String studentId);

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STUDENT')")
  List<TeamDTO> getTeamsForStudent(String studentId);

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PROFESSOR','ROLE_STUDENT')")
  List<StudentDTO> getMembers(Long teamId);

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STUDENT')")
  TeamDTO proposeTeam(String courseId, String name, List<String> membersIds);

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PROFESSOR','ROLE_STUDENT')")
  List<TeamDTO> getTeamForCourse(String courseName);

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PROFESSOR','ROLE_STUDENT')")
  List<StudentDTO> getStudentsInTeams(String courseName);

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PROFESSOR','ROLE_STUDENT')")
  List<StudentDTO> getAvailableStudents(String courseName);

  boolean setActive(Long id);

  boolean evictTeam(Long id);

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  boolean addProfessor(ProfessorDTO professor);

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  boolean addProfessorToCourse(String professorId, String courseName);

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PROFESSOR','ROLE_STUDENT')")
  List<ProfessorDTO> getProfessors(String courseName);
}
