import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';

@Component({
  selector: 'app-statistics-vm',
  templateUrl: './statistics-vm.component.html',
  styleUrls: ['./statistics-vm.component.css']
})
export class StatisticsVmComponent implements OnInit {

  statistics: any[];

  constructor(@Inject(MAT_DIALOG_DATA) public data, private dialogRef: MatDialogRef<StatisticsVmComponent>) {
    this.statistics = data.genericResponse.split('\n');
    // rimuovo la scritta "current usage"
    // @todo Cambiare API
    this.statistics.splice(0, 1);
  }

  ngOnInit(): void {
  }


}
