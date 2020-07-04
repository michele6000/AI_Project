import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {MatTableDataSource} from "@angular/material/table";

@Component({
  selector: 'app-table',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.css']
})
export class TableComponent implements OnInit {
  columnsToDisplay = [];
  dataSource = new MatTableDataSource();

  @Input('data') set data(data) {
    this.dataSource.data = data;
  }

  @Input('columns') set columns(columns) {
    this.columnsToDisplay = columns;
  }

  @ViewChild(MatPaginator, {static: true})
  paginator: MatPaginator;

  @ViewChild(MatSort, {static: true})
  sort: MatSort;

  constructor() {
  }

  ngOnInit(): void {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
  }

  isAllChecked() {
    return false;
  }

  isIndeterminated() {
    return false;
  }

  changeStatusAll(checked: boolean) {
    return false;
  }

  change(element: any) {

  }

  changeStatus(element: any) {

  }
}
