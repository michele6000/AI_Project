import {Component, OnInit, ViewChild} from '@angular/core';
import {MatSidenav} from "@angular/material/sidenav";

@Component({
  selector: 'app-student',
  templateUrl: './student.component.html',
  styleUrls: ['./student.component.css']
})
export class StudentComponent implements OnInit {

  @ViewChild(MatSidenav)
  sidenav: MatSidenav;

  constructor() { }

  ngOnInit(): void {
  }

  toggleMenu() {
    this.sidenav.toggle();
  }
}
