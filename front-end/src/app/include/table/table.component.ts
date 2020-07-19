import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';

@Component({
  selector: 'app-table',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.css']
})
export class TableComponent implements OnInit {
  columnsToDisplay = [];
  columnsWithCheckbox = [];
  dataSource = new MatTableDataSource();
  father: string;
  private checkedObjects: any[] = [];

  @Input() showDelete: boolean;

  @Input('data') set data(data) {
    this.dataSource.data = data;
  }

  @Input('columns') set columns(columns) {
    this.columnsToDisplay = columns;
    this.columnsWithCheckbox = ['select', ...columns];
  }

  @Output('delete') onDelete: EventEmitter<any[]> = new EventEmitter<any[]>();

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
    if (this.checkedObjects.length === this.dataSource.data.length && this.dataSource.data.length !== 0) {
      return true;
    } else {
      return false;
    }
  }

  isIndeterminated() {
    if (this.checkedObjects.length === this.dataSource.data.length || this.checkedObjects.length === 0) {
      return false;
    } else {
      return true;
    }
  }

  changeStatusAll(checked: boolean) {
    if (checked) {
      for (const s of this.dataSource.data) {
        if (this.checkedObjects.indexOf(s) === -1) {
          this.checkedObjects.push(s);
        }
      }
    } else {
      this.checkedObjects = [];
    }
  }

  change(element: any) {
    if (this.checkedObjects.indexOf(element) !== -1) {
      return true;
    } else {
      return false;
    }
  }

  changeStatus(row: any) {
    if (this.checkedObjects.indexOf(row) !== -1) {
      const index = this.checkedObjects.indexOf(row);
      this.checkedObjects.splice(index, 1);
    } else {
      this.checkedObjects.push(row);
    }
  }

  delete() {
    this.onDelete.emit(this.checkedObjects);
  }
}
