package it.polito.ai.esercitazione3.repositories;

import it.polito.ai.esercitazione3.entities.Course;
import it.polito.ai.esercitazione3.entities.Student;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
  @Query(
    "SELECT s FROM Student s INNER JOIN s.teams t INNER JOIN t.course c WHERE c.name=:courseName"
  )
  List<Student> getStudentsInTeams(String courseName);

  @Query(
    "SELECT s FROM Student s INNER JOIN s.courses c WHERE c.name=:courseName AND s NOT IN " +
    "(SELECT s FROM Student s INNER JOIN s.teams t INNER JOIN t.course c WHERE c.name=:courseName)"
  )
  List<Student> getStudentsNotInTeams(String courseName);
}
