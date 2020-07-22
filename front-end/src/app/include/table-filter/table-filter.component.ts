import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';

@Component({
  selector: 'app-table-filter',
  templateUrl: './table-filter.component.html',
  styleUrls: ['./table-filter.component.css']
})
export class TableFilterComponent implements OnInit {

  columnsToDisplay = [];
  columnsWithEdit = [];
  dataSource = new MatTableDataSource();

  @Input('data') set data(data) {
    this.dataSource.data = data;
  }

  @Input('columns') set columns(columns) {
    this.columnsToDisplay = columns;
  }

  @Output('edit') onEdit: EventEmitter<any[]> = new EventEmitter<any[]>();

  @ViewChild(MatPaginator, {static: true})
  paginator: MatPaginator;

  @ViewChild(MatSort, {static: true})
  sort: MatSort;

  constructor() {
  }

  ngOnInit(): void {

    this.columnsWithEdit = [...this.columnsToDisplay, 'edit'];

    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
  }

  edit($event: MouseEvent, element: any) {
    this.onEdit.emit(element);
  }

  applyFilter(value: any) {

  }
}
