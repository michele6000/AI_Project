import {Component, OnInit} from '@angular/core';
import {GroupModel} from '../../models/group.model';
import {MatDialog} from '@angular/material/dialog';
import {CreateVmProfessorComponent} from '../../dialog/create-vm/create-vm-professor.component';
import {ActivatedRoute, Router} from '@angular/router';
import {ProfessorService} from '../../services/professor.service';
import {CourseModel} from '../../models/course.model';
import {from} from 'rxjs';
import {concatMap, toArray} from 'rxjs/operators';
import {EditVmProfessorComponent} from '../../dialog/edit-vm-professor/edit-vm-professor.component';
import {MatSnackBar} from '@angular/material/snack-bar';
import {StatisticsVmComponent} from '../../dialog/statistics-vm/statistics-vm.component';

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

  hasVMType = false;

  constructor(private dialog: MatDialog, private route: ActivatedRoute, private router: Router, private professorService: ProfessorService, private snackBar: MatSnackBar) {
  }

  ngOnInit(): void {
    this.courseParam = this.router.routerState.snapshot.url.split('/')[2];

    this.corso = this.professorService.findCourseByNameUrl(this.courseParam);
    if (this.corso.name.length === 0) {
      return;
    }

    // Recupero il VM Type, se presente
    this.professorService.findVmTypeByCourse(this.corso.name).subscribe((vms) => {
      this.hasVMType = true;
    }, error => {
      this.hasVMType = false;
    });

    this.loadVMLimits();
  }

  createVM($event) {
    if (this.corso.name !== '') {
      this.dialog.open(CreateVmProfessorComponent, {})
        .afterClosed()
        .subscribe(result => {
          // Recupero il VM Type, se presente
          this.professorService.findVmTypeByCourse(this.corso.name).subscribe((vms) => {
            this.hasVMType = true;
          }, error => {
            this.hasVMType = false;
          });
        });
    } else {
      this.snackBar.open('You must to create a course first', 'OK', {
        duration: 5000
      });
    }
  }

  modifyGroup($event: GroupModel) {
    this.dialog.open(EditVmProfessorComponent, {data: $event})
      .afterClosed()
      .subscribe(result => {
        this.loadVMLimits();
      });
  }

  loadVMLimits() {
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
  }

  showStatistics(element: any) {
    // 1) richiesta per avere le statistiche di quella VM
    // 2) apertura dialog a cui passo le statistiche
    this.professorService.findStatisticsByTeam(element.id).subscribe(
      statistics => {
        this.dialog.open(StatisticsVmComponent, {data: statistics});
      }
    );
  }
}
