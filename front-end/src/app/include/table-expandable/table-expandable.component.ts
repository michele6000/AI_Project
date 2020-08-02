import {Component, ViewChild, ViewChildren, QueryList, ChangeDetectorRef, OnInit, Output, EventEmitter, Input} from '@angular/core';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {MatSort} from '@angular/material/sort';
import {MatTableDataSource, MatTable} from '@angular/material/table';
import {MatPaginator} from "@angular/material/paginator";
import {GroupModel} from "../../models/group.model";
import {VmModel} from "../../models/vm.model";
import {StudentModel} from "../../models/student.model";

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

  @ViewChild(MatTable)
  table: MatTable<any>;

  @ViewChild(MatPaginator, {static: true})
  paginator: MatPaginator;

  @Output('edit') onEdit: EventEmitter<any> = new EventEmitter<any>();

  dataSource: MatTableDataSource<any>;
  usersData: any[] = [];

  @Input() set columns(columns) {
    this.columnsToDisplay = columns;
  }

  @Input() set innerColumns(columns) {
    this.innerDisplayedColumns = columns;
  }

  columnsToDisplay = ['id', 'name', 'vcpu', 'ram'];
  columnsWithEdit: string[];
  innerDisplayedColumns = ['name', 'state', 'link'];
  expandedElement: any | null;

  @Input() showEdit = false;

  @Input('data') set data(data) {
    this.usersData = [];
    data.forEach(group => {
      this.prepareData(group);
    });
    this.dataSource = new MatTableDataSource(this.usersData);
    console.log(data);
    if (this.table) {
      console.log('Table ok');
      this.table.renderRows();
    }
  }

  constructor(private cd: ChangeDetectorRef) {
  }

  ngOnInit() {
    this.data?.forEach(group => {
      this.prepareData(group);
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

  prepareData(group: any) {
    if (group.vms && Array.isArray(group.vms) && group.vms.length) {
      this.usersData = [...this.usersData, {...group, vms: new MatTableDataSource(group.vms)}];
    } else if (group.members && Array.isArray(group.members) && group.members.length) {
      this.usersData = [...this.usersData, {...group, vms: new MatTableDataSource(group.members)}];
    } else {
      this.usersData = [...this.usersData, group];
    }
  }

  toggleRow(element: any) {
    if (element.vms && (element.vms as MatTableDataSource<VmModel>).data.length) {
      this.expandedElement = this.expandedElement === element ? null : element;
    } else if (element.members && (element.members as MatTableDataSource<StudentModel>).data.length) {
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

const GROUPS = [
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
