import {Component, OnInit} from '@angular/core';
import {switchMap} from 'rxjs/operators';
import {ActivatedRoute, ParamMap, Router} from '@angular/router';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-enrolled-students',
  templateUrl: './enrolled-students.component.html',
  styleUrls: ['./enrolled-students.component.css']
})
export class EnrolledStudentsComponent implements OnInit {
  columns = ['id', 'nome'];
  data = [
    {
      id: 1,
      nome: 'Mario'
    },
    {
      id: 2,
      nome: 'Paolo'
    }
  ];

  constructor(private route: ActivatedRoute, private router: Router) {
  }

  ngOnInit(): void {
    /*
    this.route.paramMap.pipe(
      switchMap((params: ParamMap) =>
        this.course = params.get('course')
      )
    );
    console.log(this.course);
     */
  }

}
