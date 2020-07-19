import {Component, OnInit} from '@angular/core';
import {NgForm} from '@angular/forms';
import {CrudService} from '../../../services/crud.service';
import {StudentModel} from '../../../models/student.model';

@Component({
  selector: 'app-create-group',
  templateUrl: './create-group.component.html',
  styleUrls: ['./create-group.component.css']
})
export class CreateGroupComponent implements OnInit {
  studentsColumns = ['email', 'name', 'surname', 'matricola'];
  studentsData: StudentModel[] = [
    {
      email: 's123456',
      name: 'Mario',
      surname: 'Rossi',
      matricola: '123456'
    },
    {
      email: 's123456',
      name: 'Paolo',
      surname: 'Verdi',
      matricola: '123456'
    }
  ];
  groupsColumns = ['Group Name', 'Matricola', 'Name'];
  groupsData = [];

  selectedStudents: StudentModel[] = [];

  constructor(private crudService: CrudService) {
  }

  ngOnInit(): void {
  }

  proposeGroup(f: NgForm) {
    if (f.valid && this.selectedStudents.length > 0) {
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
