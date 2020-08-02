import {Component, OnInit} from '@angular/core';
import {GroupModel} from '../../models/group.model';
import {MatDialog} from '@angular/material/dialog';
import {EditVmProfessorComponent} from '../../dialog/edit-vm/edit-vm-professor.component';
import {ActivatedRoute, Router} from '@angular/router';
import {ProfessorService} from '../../services/professor.service';

@Component({
  selector: 'app-vms',
  templateUrl: './vms.component.html',
  styleUrls: ['./vms.component.css']
})
export class VmsComponent implements OnInit {
  data = [];
  columns = [];

  courseParam: string;

  constructor(private dialog: MatDialog, private route: ActivatedRoute, private router: Router, private professorService: ProfessorService) {

  }

  ngOnInit(): void {
    this.courseParam = this.router.routerState.snapshot.url.split('/')[2];

    // Recupero l'elenco di VM
  }

  deleteVM($event: any[]) {

  }

  createVM($event) {
    this.dialog.open(EditVmProfessorComponent, {})
      .afterClosed()
      .subscribe(result => {
        console.log(result);
      });
  }
}
