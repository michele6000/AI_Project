package it.polito.ai.project.controllers;

import it.polito.ai.project.dtos.CourseDTO;
import it.polito.ai.project.dtos.StudentDTO;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class ModelHelper {

    public static CourseDTO enrich(CourseDTO course) {
        Link selflink = linkTo(CourseController.class)
                .slash(course.getName())
                .withSelfRel();
        course.add(selflink);

        Link enrolledStudLink = linkTo(CourseController.class)
                .slash(course.getName())
                .slash("enrolled")
                .withRel("enrolled");
        course.add(enrolledStudLink);
        return course;
    }

    public static StudentDTO enrich(StudentDTO student) {
        Link selflink = linkTo(StudentController.class)
                .slash(student.getId())
                .withSelfRel();
        student.add(selflink);
        return student;
    }
}
