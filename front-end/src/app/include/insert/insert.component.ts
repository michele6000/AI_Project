import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {MatAutocompleteSelectedEvent} from '@angular/material/autocomplete';
import {Observable} from 'rxjs';
import {FormControl} from '@angular/forms';
import {StudentModel} from '../../models/student.model';
import {map, startWith} from "rxjs/operators";
import {MatPaginator} from '@angular/material/paginator';
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
  selector: 'app-insert',
  templateUrl: './insert.component.html',
  styleUrls: ['./insert.component.css']
})
export class InsertComponent implements OnInit {
  formControl = new FormControl();
  filteredOptions: Observable<StudentModel[]>;
  selected: StudentModel;

  @Input('options') options: StudentModel[] = [];

  @Output('add') onAdd: EventEmitter<StudentModel> = new EventEmitter<StudentModel>();

  constructor(private snackBar: MatSnackBar) {
  }

  ngOnInit(): void {
    this.filteredOptions = this.formControl.valueChanges
      .pipe(
        startWith(''),
        map(value => typeof value === 'string' ? value : value.name),
        map(name => name ? this._filter(name) : this.options.slice())
      );
  }

  optionSelected($event: MatAutocompleteSelectedEvent) {
    this.selected = $event.option.value;
  }

  add() {
    if (this.selected) {
      this.onAdd.emit(this.selected);
      this.selected = null;
    } else {
      this.snackBar.open('You have to select a student first.', 'OK', {
        duration: 5000
      });
    }
  }

  displayFn(studentModel: StudentModel): string {
    return studentModel ? studentModel.firstName + ' ' + studentModel.name + ' ' + studentModel.id : '';
  }

  private _filter(name: string): any[] {
    const filterValue = name.toLowerCase();
    return this.options.filter(option => option.id.toLowerCase().indexOf(filterValue) === 0
      || option.firstName.toLowerCase().indexOf(filterValue) === 0
      || option.name.toLowerCase().indexOf(filterValue) === 0);
  }
}
