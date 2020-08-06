import {Component, OnInit} from '@angular/core';
import {GroupModel} from '../../models/group.model';
import {MatDialog} from '@angular/material/dialog';
import {CreateVmProfessorComponent} from '../../dialog/create-vm/create-vm-professor.component';
import {ActivatedRoute, Router} from '@angular/router';
import {ProfessorService} from '../../services/professor.service';
import {CourseModel} from "../../models/course.model";
import {from} from "rxjs";
import {concatMap, toArray} from "rxjs/operators";
import {EditVmProfessorComponent} from "../../dialog/edit-vm-professor/edit-vm-professor.component";

@Component({
  selector: 'app-vms',
  templateUrl: './vms.component.html',
  styleUrls: ['./vms.component.css']
})
export class VmsComponent implements OnInit {
  data = [];
  columns = [];

  courseParam: string;
  corso: CourseModel;
  groupsData: GroupModel[] = [];
  groupsColumns = ['name'];
  innerGroupColumns = ['accessLink', 'owner', 'status'];

  constructor(private dialog: MatDialog, private route: ActivatedRoute, private router: Router, private professorService: ProfessorService) {

  }

  ngOnInit(): void {
    this.courseParam = this.router.routerState.snapshot.url.split('/')[2];

    this.corso = this.professorService.findCourseByNameUrl(this.courseParam);

    // Recupero l'elenco di Teams
    this.professorService.findTeamsByCourse(this.corso.name).subscribe((teams) => {
      const groupsData: GroupModel[] = [];

      from(teams).pipe(
        concatMap(t => {
          return this.professorService.findVmsByTeam(t.id);
        }),
        toArray()
      ).subscribe((vms) => {
        teams.forEach((t, i) => {
          t.vms = vms[i];
          groupsData.push(t);
        });
        this.groupsData = groupsData;
      });
    });

    // Recupero l'elenco di VM
  }

  deleteVM($event: any[]) {

  }

  createVM($event) {
    this.dialog.open(CreateVmProfessorComponent, {})
      .afterClosed()
      .subscribe(result => {
        console.log(result);
      });
  }

  modifyGroup($event: GroupModel) {

    this.dialog.open(EditVmProfessorComponent, {data: $event})
      .afterClosed()
      .subscribe(result => {
        console.log(result);
      });
  }
}
