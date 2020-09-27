package it.polito.ai.project.services;

import it.polito.ai.project.dtos.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;

import java.io.Reader;
import java.util.List;
import java.util.Optional;

public interface TeamService {
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PROFESSOR')")
    boolean addCourse(CourseDTO course);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN','ROLE_PROFESSOR')")
    Optional<CourseDTO> getCourse(String name);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN','ROLE_PROFESSOR')")
    List<CourseDTO> getAllCourses();

    //  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    StudentDTO addStudent(UserDTO user, MultipartFile file);

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    Optional<StudentDTO> getStudent(String studentId);

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    List<StudentDTO> getAllStudents();

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PROFESSOR')")
    List<StudentDTO> getEnrolledStudents(String courseName);

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PROFESSOR')")
    boolean addStudentToCourse(String studentId, String courseName);

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PROFESSOR')")
    void enableCourse(String courseName, String username);

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PROFESSOR')")
    void disableCourse(String courseName, String username);

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PROFESSOR')")
    void deleteCourse(String courseName, String username);

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PROFESSOR')")
    void updateCourse(String courseName, CourseDTO course, String username);

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

    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN', 'ROLE_PROFESSOR')")
    List<StudentDTO> getConfirmedStudents(Long teamId);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN', 'ROLE_PROFESSOR')")
    List<StudentDTO> getPendentStudents(Long teamId);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN', 'ROLE_PROFESSOR')")
    void deleteMember(Long teamId, String studentId);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN', 'ROLE_PROFESSOR')")
    TeamDTO proposeTeam(String courseId, String name, List<String> membersIds);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN', 'ROLE_PROFESSOR')")
    void addMember(Long teamId, String studentId);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN', 'ROLE_PROFESSOR')")
    List<TeamDTO> getTeamForCourse(String courseName);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN', 'ROLE_PROFESSOR')")
    List<StudentDTO> getStudentsInTeams(String courseName);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN', 'ROLE_PROFESSOR')")
    List<StudentDTO> getAvailableStudents(String courseName);

    void setActive(Long id);

    void evictTeam(Long id);

    //  @PreAuthorize("hasRole('ROLE_ADMIN')")
    ProfessorDTO addProfessor(UserDTO user, MultipartFile file);

    byte[] getImage(String username);

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    boolean addProfessorToCourse(String professorId, String courseName);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN', 'ROLE_PROFESSOR')")
    List<ProfessorDTO> getProfessors(String courseName);

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    List<CourseDTO> getProfessorCourses(String professorId);

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    Boolean deleteOne(String studentId, String courseName);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_PROFESSOR','ROLE_ADMIN')")
    TeamDTO getTeam(Long teamId);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN', 'ROLE_PROFESSOR')")
    List<ProfessorDTO> getAllProfessors();
}
