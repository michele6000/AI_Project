package it.polito.ai.project.services;

import it.polito.ai.project.dtos.*;

import java.io.Reader;
import java.util.List;
import java.util.Optional;

import it.polito.ai.project.entities.VM;
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

  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR')")
  List<CourseDTO> getProfessorCourses(String professorId);

  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR')")
  Boolean deleteOne(String studentId, String courseName);

  @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN', 'ROLE_PROFESSOR')")
  List<VMDTO> getTeamVMs(Long teamId);

  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR')")
  Long createVMType(VMTypeDTO vmType);

  @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN', 'ROLE_PROFESSOR')")
  String getTeamStat(Long teamId);

  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR')")
  Boolean setVMType(String courseName, Long vmtId);

  @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN', 'ROLE_PROFESSOR')")
  VMDTO getVMConfig(String vmId);

  @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN', 'ROLE_PROFESSOR')")
  Boolean modifyVMConfiguration(String vmId, VMDTO vm);

  @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN', 'ROLE_PROFESSOR')")
  Boolean modifyVMOwner(String vmId, String studentID);

  @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN', 'ROLE_PROFESSOR')")
  Boolean addVMOwner(String vmId, String studentID);

  @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN', 'ROLE_PROFESSOR')")
  List<StudentDTO> getVMOwners(String vmId);

  @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN', 'ROLE_PROFESSOR')")
  Boolean powerVMOn(String vmId);

  @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN', 'ROLE_PROFESSOR')")
  Boolean powerVMOff(String vmId);

  @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN', 'ROLE_PROFESSOR')")
  Boolean deleteVM(String vmId);

  @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN')")
  VMDTO createVmInstance(Long teamId, VMDTO vm, String currentUsername);
}
