import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {MatAutocompleteSelectedEvent} from '@angular/material/autocomplete';
import {Observable} from 'rxjs';
import {FormControl} from '@angular/forms';
import {UserModel} from '../../models/user.models';
import {StudentModel} from '../../models/student.model';
import {map, startWith} from "rxjs/operators";

@Component({
  selector: 'app-insert',
  templateUrl: './insert.component.html',
  styleUrls: ['./insert.component.css']
})
export class InsertComponent implements OnInit {
  formControl = new FormControl();
  filteredOptions: Observable<StudentModel[]>;
  selected : StudentModel;

  @Input('options') options: StudentModel[] = [];

  @Output('add') onAdd: EventEmitter<StudentModel> = new EventEmitter<StudentModel>();

  private _filter(name: string): any[] {
    const filterValue = name.toLowerCase();
    return this.options.filter(option => option.matricola.toLowerCase().indexOf(filterValue) === 0
    || option.name.toLowerCase().indexOf(filterValue) === 0
    || option.surname.toLowerCase().indexOf(filterValue) === 0);
  }

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
    return studentModel ? studentModel.name + ' ' + studentModel.surname + ' ' + studentModel.matricola : '';
  }
}
