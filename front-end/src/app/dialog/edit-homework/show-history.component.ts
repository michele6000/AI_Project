import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {NgForm} from '@angular/forms';
import {StudentSubmissionModel} from '../../models/student-submission.model';
import {StudentService} from '../../services/student.service';

const API_URL_PUBLIC = '/public/API/';
const API_URL_LOCAL = '/local/API/';

@Component({
  selector: 'app-edit-homework',
  templateUrl: './show-history.component.html',
  styleUrls: ['./show-history.component.css']
})
export class ShowHistoryComponent implements OnInit {

  error = false;
  fileAbsent = false;
  file: any;
  studentId: string;
  submissionId: number;
  showInputGrade = false;
  history: any = [];

  constructor(@Inject(MAT_DIALOG_DATA) public data: StudentSubmissionModel, private studentService: StudentService) {
    this.studentId = data.studentId;
    this.submissionId = data.submissionId;

    this.studentService.getHistorySolutions(this.studentId, this.submissionId).subscribe((submissions) => {
      this.history = submissions;
    }, (error => {

    }));
  }

  ngOnInit(): void {
  }

  handleFileSelect($event: any) {
    this.file = $event.target.files[0];
    this.fileAbsent = false;
  }

  send(f: NgForm) {

  }

  showGrade($event) {
    if ($event.value === 0) {
      this.showInputGrade = false;
    } else {
      this.showInputGrade = true;
    }
  }


  handleShowSolution(solutionId: number) {
    window.open('//' + API_URL_PUBLIC + 'students/solutions/getImage/' + solutionId, '_blank');
  }
}
