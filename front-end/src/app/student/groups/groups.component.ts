import {Component, ComponentFactoryResolver, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {GroupDirective} from './group.directive';
import {InfoGroupComponent} from './info-group/info-group.component';
import {CreateGroupComponent} from './create-group/create-group.component';
import {StudentService} from '../../services/student.service';
import {CourseModel} from '../../models/course.model';
import {Router} from '@angular/router';
import {Subscription} from 'rxjs';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-groups',
  templateUrl: './groups.component.html',
  styleUrls: ['./groups.component.css']
})
export class GroupsComponent implements OnInit, OnDestroy {

  @ViewChild(GroupDirective, {static: true}) adHost: GroupDirective;
  private courseParam: string;
  private corso: CourseModel;
  private changeCorsoSub: Subscription;

  constructor(private studentService: StudentService, private componentFactoryResolver: ComponentFactoryResolver, private router: Router,
              private snackBar: MatSnackBar) {
  }

  ngOnInit() {
    // in ascolto sul BehaviorSubject per cambiare le informazioni del gruppo in base al corso
    this.changeCorsoSub = this.studentService.eventsSubjectChangeCorsoSideNav.subscribe(next => {
        this.computeGroup();
      },
      error => {
        this.genericError();
      });
  }

  computeGroup() {
    // recupero il corso dall'url
    this.courseParam = this.router.routerState.snapshot.url.split('/')[2];
    // recupero il corso dall'array di corsi dello studente dato il nome
    this.corso = this.studentService.findCourseByNameUrl(this.courseParam);
    // carico il component con i gruppi a seconda se lo studente fa parte di un gruppo oppure no
    this.studentService.teams.subscribe((teams) => {
      if (teams && teams.filter(t => t.status === 1 && t.courseName === this.corso.name).length > 0) {
        this.loadComponent(1);
      } else if (teams) {
        this.loadComponent(2);
      }
    }, error => {
      this.genericError();
    });
  }

  loadComponent(type: number) {
    let groupItem;
    if (type === 1) {
      groupItem = InfoGroupComponent;
    } else {
      groupItem = CreateGroupComponent;
    }
    const componentFactory = this.componentFactoryResolver.resolveComponentFactory(groupItem);

    const viewContainerRef = this.adHost.viewContainerRef;
    viewContainerRef.clear();

    const componentRef = viewContainerRef.createComponent(componentFactory);
  }

  ngOnDestroy() {
    if (this.changeCorsoSub) {
      this.changeCorsoSub.unsubscribe();
    }
  }

  genericError() {
    this.snackBar.open('Failed to communicate with server, try again.', 'OK', {
      duration: 5000
    });
    location.reload();
  }

}
