import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {MatTable, MatTableDataSource} from '@angular/material/table';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {VmModel} from "../../../models/vm.model";
import {StudentService} from "../../../services/student.service";

@Component({
  selector: 'app-vms-table',
  templateUrl: './vms-table.component.html',
  styleUrls: ['./vms-table.component.css']
})
export class VmsTableComponent implements OnInit {

  columnsToDisplay = [];
  columnsWithCheckbox = [];
  dataSource = new MatTableDataSource();

  @ViewChild(MatTable)
  table: MatTable<any>;

  @Input('data') set data(data) {
    this.dataSource.data = data;
    console.log(data);
    if (this.table) {
      this.table.renderRows();
    }
  }

  @Input('columns') set columns(columns) {
    this.columnsToDisplay = columns;
  }

  @Output('edit') onEdit: EventEmitter<any> = new EventEmitter<any>();
  @Output('delete') onDelete: EventEmitter<any> = new EventEmitter<any>();

  @ViewChild(MatPaginator, {static: true})
  paginator: MatPaginator;

  @ViewChild(MatSort, {static: true})
  sort: MatSort;

  constructor(private studentService: StudentService) {
  }

  ngOnInit(): void {
    this.columnsWithCheckbox = [...this.columnsToDisplay, 'power', 'delete', 'edit'];

    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
  }

  edit($event: MouseEvent, element: any) {
    this.onEdit.emit(element);
  }

  powerOn(element: VmModel) {
    this.studentService.powerOnVm(element.id).subscribe((res) => {element.status = 'poweron'; }, (error) => {});
  }

  powerOff(element: VmModel) {
    this.studentService.powerOffVm(element.id).subscribe((res) => {element.status = 'poweroff'; }, (error) => {});
  }

  delete(element: VmModel) {
    this.onDelete.emit(element);
  }
}