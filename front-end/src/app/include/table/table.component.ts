import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTable, MatTableDataSource} from '@angular/material/table';
import {MatDialog} from '@angular/material/dialog';
import {ConfirmDeleteComponent} from '../../dialog/confirm-delete/confirm-delete.component';
import {MatSnackBar} from '@angular/material/snack-bar';

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

  @ViewChild(MatTable)
  table: MatTable<any>;

  @Input() checkedObjects: any[] = [];
  @Output() checkedObjectsChange = new EventEmitter<any[]>();

  @Input() showDelete: boolean;
  @Input() showEdit: boolean;
  @Input() showChangeStatus: boolean;
  @Input() showCheckbox = true;
  @Input() showDetails = false;
  showAddProf: boolean = false;
  showRemoveProf: boolean = false;
  @Output('delete') onDelete: EventEmitter<any[]> = new EventEmitter<any[]>();
  @Output('edit') onEdit: EventEmitter<any> = new EventEmitter<any>();
  @Output('show') onShowDetails: EventEmitter<any> = new EventEmitter<any>();
  @Output('changeActive') onChangeActive: EventEmitter<any> = new EventEmitter<any>();
  @Output('addProf') onAddProf: EventEmitter<any> = new EventEmitter<any>();
  @Output('removeProf') onRemoveProf: EventEmitter<any> = new EventEmitter<any>();
  @ViewChild(MatPaginator, {static: true})
  paginator: MatPaginator;
  @ViewChild(MatSort, {static: true})
  sort: MatSort;

  /* checkbox like gmail */
  numberOfEntrySelected: number;
  constraintMasterCheckbox: boolean = false;
  constraintMasterCheckboxSelectAll: boolean = false;
  constraintMasterCheckboxDeselectAll: boolean = false;

  constructor(private dialog: MatDialog, private snackBar: MatSnackBar) {
  }

  @Input('data') set data(data) {
    this.dataSource.data = data;
    if (this.table) {
      this.table.renderRows();
    }
  }

  @Input('columns') set columns(columns) {
    this.columnsToDisplay = columns;
  }

  ngOnInit(): void {
    if (this.showEdit) {
      if (this.showChangeStatus) {
        if (this.showDetails) {
          this.showAddProf = true;
          this.showRemoveProf = true;
          this.columnsWithCheckbox = ['select', ...this.columnsToDisplay, 'enabled', 'addProf', 'removeProf', 'edit', 'details'];
        } else {
          this.columnsWithCheckbox = ['select', ...this.columnsToDisplay, 'enabled', 'edit'];
        }
      } else if (this.showCheckbox) {
        this.columnsWithCheckbox = ['select', ...this.columnsToDisplay, 'edit'];
      } else {
        this.columnsWithCheckbox = [...this.columnsToDisplay, 'edit'];
      }
    } else if (this.showCheckbox) {
      this.columnsWithCheckbox = ['select', ...this.columnsToDisplay];
    } else if (this.showDetails) {
      this.columnsWithCheckbox = [...this.columnsToDisplay, 'details'];
    } else {
      this.columnsWithCheckbox = [...this.columnsToDisplay];
    }

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
    this.numberOfEntrySelected = this.paginator.pageSize;
    if (checked) {
      if (this.dataSource.data.length >= this.numberOfEntrySelected) {
        // gestione like gmail solo se è necessario, ovvero il numero di entri nella tabella è maggiore del size del paginator
        this.constraintMasterCheckbox = true;
        this.constraintMasterCheckboxSelectAll = true;
        this.constraintMasterCheckboxDeselectAll = false;
        for (let i = 0; i < this.numberOfEntrySelected; i++) {
          if (this.checkedObjects.indexOf(this.dataSource.data[i]) === -1) {
            this.checkedObjects.push(this.dataSource.data[i]);
          }
        }
      } else {
        for (const s of this.dataSource.data) {
          if (this.checkedObjects.indexOf(s) === -1) {
            this.checkedObjects.push(s);
          }
        }
      }
    } else {
      this.constraintMasterCheckbox = false;
      this.constraintMasterCheckboxSelectAll = false;
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
    if (this.checkedObjects.length > 0) {
      this.dialog.open(ConfirmDeleteComponent)
        .afterClosed()
        .subscribe(result => {
          if (result) {
            this.onDelete.emit(this.checkedObjects);
          }
          this.checkedObjects = [];
        });
    } else {
      this.snackBar.open('You must select almost one element!', 'OK', {
        duration: 5000
      });
    }
  }

  edit($event: MouseEvent, element: any) {
    this.onEdit.emit(element);
  }

  enableDisable(row: any) {
    this.onChangeActive.emit(row);
  }

  isEnabled(element: any) {
    return element.enabled;
  }

  show(element: any) {
    this.onShowDetails.emit(element);
  }

  removeProfessor(element: any) {
    this.onRemoveProf.emit(element);
  }

  addProfessor(element: any) {
    this.onAddProf.emit(element);
  }

  // select all
  selectAll() {
    this.numberOfEntrySelected = this.dataSource.data.length;
    for (const s of this.dataSource.data) {
      if (this.checkedObjects.indexOf(s) === -1) {
        this.constraintMasterCheckboxSelectAll = false;
        this.constraintMasterCheckboxDeselectAll = true;
        this.checkedObjects.push(s);
      }
    }
  }

  deselctAll() {
    this.constraintMasterCheckbox = false;
    this.constraintMasterCheckboxSelectAll = false;
    this.constraintMasterCheckboxDeselectAll = false;
    this.checkedObjects = [];
  }
}
