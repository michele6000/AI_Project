import {
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  QueryList,
  ViewChild,
  ViewChildren
} from '@angular/core';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {MatSort} from '@angular/material/sort';
import {MatTable, MatTableDataSource} from '@angular/material/table';
import {MatPaginator} from "@angular/material/paginator";
import {GroupModel} from "../../models/group.model";

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
  @ViewChildren('innerTables') innerTables: QueryList<MatTable<any>>;

  @ViewChild(MatTable)
  table: MatTable<any>;

  @ViewChild(MatPaginator, {static: true})
  paginator: MatPaginator;

  @Output('edit') onEdit: EventEmitter<any> = new EventEmitter<any>();

  dataSource: MatTableDataSource<any>;
  usersData: any[] = [];
  columnsToDisplay = ['id', 'name', 'vcpu', 'ram'];
  columnsWithEdit: string[];
  innerDisplayedColumns = ['name', 'state', 'link'];
  expandedElement: any | null;
  @Input() showEdit = false;

  constructor(private cd: ChangeDetectorRef) {
  }

  @Input() set columns(columns) {
    this.columnsToDisplay = columns;
  }

  @Input() set innerColumns(columns) {
    this.innerDisplayedColumns = columns;
  }

  @Input('data') set data(data) {
    this.usersData = [];
    data.forEach(group => {
      this.prepareData(group);
    });
    if (this.dataSource) {
      this.dataSource.data = this.usersData;
    }
    if (this.table) {
      this.table.renderRows();
    }
  }

  ngOnInit() {
    this.data?.forEach(row => {
      this.prepareData(row);
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

  prepareData(row: any) {
    if (row.vms && Array.isArray(row.vms) && row.vms.length) {
      this.usersData = [...this.usersData, {...row, nestedData: new MatTableDataSource(row.vms)}];
    } else if (row.members && Array.isArray(row.members) && row.members.length) {
      this.usersData = [...this.usersData, {...row, nestedData: new MatTableDataSource(row.members)}];
    } else {
      this.usersData = [...this.usersData, row];
    }
  }

  toggleRow(element: any) {
    if (element.nestedData && (element.nestedData as MatTableDataSource<any>).data.length) {
      this.expandedElement = this.expandedElement === element ? null : element;
    }
    this.cd.detectChanges();
    this.innerTables.forEach(
      (table, index) => (table.dataSource as MatTableDataSource<any>).sort = this.innerSort.toArray()[index]
    );
  }

  applyFilter(filterValue: string) {
    this.innerTables.forEach((table, index) => (table.dataSource as MatTableDataSource<any>).filter = filterValue.trim().toLowerCase());
  }

  editGroup($event, element: GroupModel) {
    $event.stopPropagation();
    this.onEdit.emit(element);
  }
}
