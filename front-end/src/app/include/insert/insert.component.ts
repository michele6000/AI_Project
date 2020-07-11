import {Component, Input, OnInit} from '@angular/core';
import {MatAutocompleteSelectedEvent} from '@angular/material/autocomplete';
import {Observable} from 'rxjs';
import {FormControl} from '@angular/forms';
import {UserModel} from '../../models/user.models';

@Component({
  selector: 'app-insert',
  templateUrl: './insert.component.html',
  styleUrls: ['./insert.component.css']
})
export class InsertComponent implements OnInit {
  formControl = new FormControl();
  filteredOptions: Observable<any[]>;

  @Input('options') options: UserModel[] = [];

  private _filter(name: string): any[] {
    const filterValue = name.toLowerCase();
    return this.options.filter(option => option.matricola.toLowerCase().indexOf(filterValue) === 0);
  }

  constructor() {
  }

  ngOnInit(): void {
  }

  optionSelected($event: MatAutocompleteSelectedEvent) {

  }

  add() {

  }

  displayFn(userModel: UserModel): string {
    return userModel && userModel.matricola ? userModel.matricola : '';
  }
}
