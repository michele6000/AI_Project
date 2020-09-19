import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {NgForm} from '@angular/forms';
import {ProfessorService} from '../../services/professor.service';
import {CourseModel} from '../../models/course.model';

@Component({
  selector: 'app-edit-course',
  templateUrl: './edit-course.component.html',
  styleUrls: ['./edit-course.component.css']
})
export class EditCourseComponent implements OnInit {
  error = false;
  course: CourseModel;

  constructor(@Inject(MAT_DIALOG_DATA) public data: CourseModel, private professorService: ProfessorService) {
    this.course = data;
  }

  ngOnInit(): void {

  }

  updateCourse(f: NgForm) {
    // Update course with new values

    this.course.name = f.value.name;
    this.course.acronymous = f.value.identifier;
    this.course.min = f.value.min;
    this.course.max = f.value.max;

    // TO DO: Call service
  }
}
