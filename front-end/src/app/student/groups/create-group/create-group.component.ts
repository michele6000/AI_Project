import {Component, OnInit} from '@angular/core';
import {NgForm} from '@angular/forms';
import {CrudService} from '../../../services/crud.service';
import {StudentModel} from '../../../models/student.model';
import {ActivatedRoute, Router} from '@angular/router';
import {CourseModel} from '../../../models/course.model';

@Component({
  selector: 'app-create-group',
  templateUrl: './create-group.component.html',
  styleUrls: ['./create-group.component.css']
})
export class CreateGroupComponent implements OnInit {
  studentsColumns = ['email', 'name', 'surname', 'matricola'];
  studentsData: StudentModel[] = [
    {
      email: 's123456@studenti.polito.it',
      firstName: 'Mario',
      name: 'Rossi',
      id: 's123456@studenti.polito.it'
    },
    {
      email: 's123456@studenti.polito.it',
      firstName: 'Paolo',
      name: 'Verdi',
      id: 's123456'
    }
  ];
  groupsColumns = ['Group Name', 'Matricola', 'Name'];
  groupsData = [];

  course: CourseModel;

  selectedStudents: StudentModel[] = [];
  private courseParam: string;
  error = false;
  message = '';

  constructor(private route: ActivatedRoute, private router: Router, private crudService: CrudService) {
  }

  ngOnInit(): void {
    this.courseParam = this.router.routerState.snapshot.url.split('/')[2];

    // Recupero i parametri del corso
    this.course = this.crudService.findCourseByNameUrl(this.courseParam);
    console.log(this.course);
  }

  proposeGroup(f: NgForm) {
    if (f.valid) {
      if ((this.selectedStudents.length + 1) < this.course.min || (this.selectedStudents.length + 1) > this.course.max) {
        this.error = true;
        this.message = 'Vincoli creazione gruppo non rispettati. Minimo ' + this.course.min
          + ' membri e massimo ' + this.course.max + ' membri (compreso te stesso).';
      } else {
        this.error = false;
        this.message = '';
        const group = {
          name: '',
          timeout: 0,
          students: this.selectedStudents
        };
        group.name = f.value.name;
        group.timeout = f.value.timeout;
        this.crudService.proposeGroup(group);
      }
    }
  }
}
