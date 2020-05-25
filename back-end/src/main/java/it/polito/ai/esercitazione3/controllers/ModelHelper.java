package it.polito.ai.esercitazione3.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import it.polito.ai.esercitazione3.dtos.CourseDTO;
import it.polito.ai.esercitazione3.dtos.StudentDTO;
import org.springframework.hateoas.Link;

public class ModelHelper {

  public static CourseDTO enrich(CourseDTO course) {
    Link selfLink = linkTo(CourseController.class)
      .slash(course.getName())
      .withSelfRel();
    Link enrolledLink = linkTo(CourseController.class)
      .slash(course.getName())
      .slash("enrolled")
      .withRel("enrolled");
    course.add(selfLink);
    course.add(enrolledLink);
    return course;
  }

  public static StudentDTO enrich(StudentDTO student) {
    Link selfLink = linkTo(StudentController.class)
      .slash(student.getId())
      .withSelfRel();
    student.add(selfLink);
    return student;
  }
}
