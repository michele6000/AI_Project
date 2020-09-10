import {Component, ComponentFactoryResolver, OnInit, ViewChild} from '@angular/core';
import {GroupDirective} from './group.directive';
import {InfoGroupComponent} from './info-group/info-group.component';
import {CreateGroupComponent} from './create-group/create-group.component';
import {StudentService} from "../../services/student.service";
import {CourseModel} from "../../models/course.model";
import {Router} from "@angular/router";

@Component({
  selector: 'app-groups',
  templateUrl: './groups.component.html',
  styleUrls: ['./groups.component.css']
})
export class GroupsComponent implements OnInit {

  @ViewChild(GroupDirective, {static: true}) adHost: GroupDirective;
  private courseParam: string;
  private corso: CourseModel;

  constructor(private studentService: StudentService, private componentFactoryResolver: ComponentFactoryResolver,
              private router: Router) {
  }

  ngOnInit() {
    this.courseParam = this.router.routerState.snapshot.url.split('/')[2];

    this.corso = this.studentService.findCourseByNameUrl(this.courseParam);

    this.studentService.teams.subscribe((teams) => {
      if (teams && teams.filter(t => t.status === 1 && t.courseName === this.corso.name).length > 0) {
        this.loadComponent(1);
      } else if (teams) {
        this.loadComponent(2);
      }
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

}
