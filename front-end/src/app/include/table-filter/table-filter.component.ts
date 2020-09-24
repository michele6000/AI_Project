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
  @Input() filterColumn: string;
  @Output('showHistory') onShowHistory: EventEmitter<any> = new EventEmitter<any>();
  @ViewChild(MatPaginator, {static: true})
  paginator: MatPaginator;
  @ViewChild(MatSort, {static: true})
  sort: MatSort;

  constructor() {
  }

  @Input('data') set data(data) {
    this.dataSource.data = data;
  }

  @Input('columns') set columns(columns) {
    this.columnsToDisplay = columns;
  }

  ngOnInit(): void {

    this.columnsWithEdit = [...this.columnsToDisplay, 'evaluate', 'evaluation', 'showSolution', 'stopRevision', 'reviewSolution', 'edit'];

    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    this.dataSource.filterPredicate =
      (data: any, filtersJson: string) => {
        const matchFilter = [];
        const filters = JSON.parse(filtersJson);

        filters.forEach(filter => {
          const val = data[filter.id] === null ? '' : data[filter.id];
          matchFilter.push(val.toLowerCase().includes(filter.value.toLowerCase()));
        });
        return matchFilter.every(Boolean);
      };
  }

  applyFilter(filterValue: any) {
    const tableFilters = [];
    tableFilters.push({
      id: this.filterColumn,
      value: filterValue.target.value
    });


    this.dataSource.filter = JSON.stringify(tableFilters);
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  showHistory(submissionId: number, studentId: string) {
    this.onShowHistory.emit({submissionId, studentId});
  }

  showSolution(element: any) {
    console.log('Show solution');
    console.log(element);
  }

  reviewSolution(element: any) {
    console.log('Review solution');
    console.log(element);
  }

  stopRevision(element: any) {

  }
}
