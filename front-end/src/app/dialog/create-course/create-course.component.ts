import {Component, OnInit} from '@angular/core';
import {NgForm} from '@angular/forms';
import {ProfessorService} from '../../services/professor.service';
import {CourseModel} from '../../models/course.model';

@Component({
  selector: 'app-create-course',
  templateUrl: './create-course.component.html',
  styleUrls: ['./create-course.component.css']
})
export class CreateCourseComponent implements OnInit {
  error = false;

  constructor(private professorService: ProfessorService) {
  }

  ngOnInit(): void {
  }

  createCourse(f: NgForm) {
    const course: CourseModel = {
      name: f.value.name,
      acronymous: f.value.identifier,
      min: f.value.min,
      max: f.value.max,
      enabled: true
    };
    this.professorService.createCourse(course);
  }
}
