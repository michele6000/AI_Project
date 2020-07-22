import {Component, ViewChild, ViewChildren, QueryList, ChangeDetectorRef, OnInit, Output, EventEmitter, Input} from '@angular/core';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {MatSort} from '@angular/material/sort';
import {MatTableDataSource, MatTable} from '@angular/material/table';
import {MatPaginator} from "@angular/material/paginator";
import {GroupModel} from "../../models/group.model";
import {VmModel} from "../../models/vm.model";

/**
 * @title Table with expandable rows
 */
@Component({
  selector: 'app-table-expandable',
  styleUrls: ['table-expandable.component.css'],
  templateUrl: 'table-expandable.component.html',
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({height: '0px', minHeight: '0'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ],
})
export class TableExpandableComponent implements OnInit {

  @ViewChild('outerSort', {static: true}) sort: MatSort;
  @ViewChildren('innerSort') innerSort: QueryList<MatSort>;
  @ViewChildren('innerTables') innerTables: QueryList<MatTable<VmModel>>;

  @ViewChild(MatPaginator, {static: true})
  paginator: MatPaginator;

  @Output('edit') onEdit: EventEmitter<GroupModel> = new EventEmitter<GroupModel>();

  dataSource: MatTableDataSource<GroupModel>;
  usersData: any[] = [];
  columnsToDisplay = ['id', 'name', 'vcpu', 'ram'];
  columnsWithEdit: string[];
  innerDisplayedColumns = ['name', 'state', 'link'];
  expandedElement: GroupModel | null;

  @Input() showEdit = false;

  constructor(private cd: ChangeDetectorRef) {
  }

  ngOnInit() {
    GROUPS.forEach(group => {
      if (group.vms && Array.isArray(group.vms) && group.vms.length) {
        this.usersData = [...this.usersData, {...group, vms: new MatTableDataSource(group.vms)}];
      } else {
        this.usersData = [...this.usersData, group];
      }
    });
    this.dataSource = new MatTableDataSource(this.usersData);
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    if (this.showEdit) {
      this.columnsWithEdit = [...this.columnsToDisplay, 'modify'];
    } else {
      this.columnsWithEdit = [...this.columnsToDisplay];
    }
  }

  toggleRow(element: any) {
    if (element.vms && (element.vms as MatTableDataSource<VmModel>).data.length) {
      this.expandedElement = this.expandedElement === element ? null : element;
    }
    this.cd.detectChanges();
    this.innerTables.forEach((table, index) => (table.dataSource as MatTableDataSource<VmModel>).sort = this.innerSort.toArray()[index]);
  }

  applyFilter(filterValue: string) {
    this.innerTables.forEach((table, index) => (table.dataSource as MatTableDataSource<VmModel>).filter = filterValue.trim().toLowerCase());
  }

  editGroup($event, element: GroupModel) {
    $event.stopPropagation();
    this.onEdit.emit(element);
  }
}

const GROUPS: GroupModel[] = [
  {
    name: 'Gruppo 1',
    id: 1,
    ram: 8,
    disk: 250,
    vcpu: 1,
    vms: [
      {
        groupId: 1,
        link: 'https://www.google.com',
        name: 'Ubuntu 20.04',
        ownerId: 1,
        state: true
      },
      {
        groupId: 1,
        link: 'https://www.amazon.com',
        name: 'Ubuntu 20.04',
        ownerId: 1,
        state: true
      }
    ],
    maxVm: 2,
    maxActiveVmSimultaneously: 1
  },
  {
    name: 'Gruppo 2',
    id: 1,
    ram: 8,
    disk: 250,
    vcpu: 1,
    maxActiveVmSimultaneously: 1,
    maxVm: 2
  },
];
