import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {MatAutocompleteSelectedEvent} from '@angular/material/autocomplete';
import {Observable} from 'rxjs';
import {FormControl} from '@angular/forms';
import {StudentModel} from '../../models/student.model';
import {map, startWith} from "rxjs/operators";
import {MatPaginator} from '@angular/material/paginator';

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

  constructor() {
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
    this.onAdd.emit(this.selected);

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
