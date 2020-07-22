import {Component, ComponentFactoryResolver, OnInit, ViewChild} from '@angular/core';
import {GroupDirective} from './group.directive';
import {InfoGroupComponent} from './info-group/info-group.component';
import {CreateGroupComponent} from './create-group/create-group.component';

@Component({
  selector: 'app-groups',
  templateUrl: './groups.component.html',
  styleUrls: ['./groups.component.css']
})
export class GroupsComponent implements OnInit {

  @ViewChild(GroupDirective, {static: true}) adHost: GroupDirective;

  constructor(private componentFactoryResolver: ComponentFactoryResolver) {
  }

  ngOnInit() {
    this.loadComponent(2);
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
