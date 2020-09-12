import {Component, OnInit} from '@angular/core';
import {NgForm} from '@angular/forms';
import {VmStudent} from '../../models/vm-student.model';
import {StudentService} from '../../services/student.service';

@Component({
  selector: 'app-edit-vm-student',
  templateUrl: './edit-vm-student.component.html',
  styleUrls: ['./edit-vm-student.component.css']
})
export class EditVmStudentComponent implements OnInit {
  error = false;

  constructor(private studentService: StudentService) {
  }

  ngOnInit(): void {
  }

  create(f: NgForm) {
    const vm = new VmStudent();
    vm.ram = f.value.ram;
    vm.vcpu = f.value.vcpu;
    vm.disk = f.value.disk;
    this.studentService.createVM(-1, vm);
  }
}
