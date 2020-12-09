import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';

@Component({
  selector: 'app-statistics-vm',
  templateUrl: './statistics-vm.component.html',
  styleUrls: ['./statistics-vm.component.css']
})
export class StatisticsVmComponent implements OnInit {

  statistics: any[];
  teamName: string;

  constructor(@Inject(MAT_DIALOG_DATA) public data) {
    this.statistics = data.statistics.genericResponse.split('\n');
    this.teamName = data.teamName;
    // rimuovo la scritta "current usage"
    this.statistics.splice(0, 1);
    for (let i = 0; i < this.statistics.length; i++) {
      if (!this.statistics[i].includes('CPU')) {
        this.statistics[i] = this.statistics[i] + ' MB';
      }
    }
  }

  ngOnInit(): void {
  }

}
