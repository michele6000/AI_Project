import {Component, OnInit} from '@angular/core';
import {StudentService} from '../../services/student.service';
import {CourseModel} from '../../models/course.model';
import {Router} from '@angular/router';
import * as moment from 'moment';
import {MatSnackBar} from '@angular/material/snack-bar';
import {SubmissionModel} from '../../models/submission.model';
import {from} from 'rxjs';
import {concatMap, toArray} from 'rxjs/operators';

const API_URL_PUBLIC = '93.56.104.204:8080/API/';
const API_URL_LOCAL = '/local/API/';


@Component({
  selector: 'app-assignments-student',
  templateUrl: './assignments-student.component.html',
  styleUrls: ['./assignments-student.component.css']
})
export class AssignmentsStudentComponent implements OnInit {

  file: any;
  consegne: SubmissionModel[] = [];
  private courseParam: string;
  private corso: CourseModel;
  hasConsegne = false;
  imageToShow: any;
  filename = 'Choose file';

  expandPanel(matExpansionPanel, event): void {
    event.stopPropagation(); // Preventing event bubbling

    if (!this._isExpansionIndicator(event.target)) {
      matExpansionPanel.close(); // Here's the magic
    }
  }

  private _isExpansionIndicator(target: EventTarget): boolean {
    const expansionIndicatorClass = 'mat-expansion-indicator';

    return (target['classList'] && target['classList'].contains(expansionIndicatorClass));
  }

  createImageFromBlob(image: Blob) {
    const reader = new FileReader();
    reader.addEventListener('load', () => {
      this.imageToShow = reader.result;
    }, false);

    if (image) {
      reader.readAsDataURL(image);
    }
  }

  constructor(private studentService: StudentService, private router: Router, private snackBar: MatSnackBar) {
    this.courseParam = this.router.routerState.snapshot.url.split('/')[2];
    this.corso = this.studentService.findCourseByNameUrl(this.courseParam);

    // Recupero l'elenco di Submissions per questo corso
    this.studentService.findSubmissions(this.corso.name).subscribe(
      (submissions) => {

        // Richiedo in concatMap (quindi, per ogni Submission raggruppando i risultati in un
        //  unica subscribe) l'elenco delle soluzioni [historySolutions] per quella Submission
        const consegne = [];
        const result = from(submissions).pipe(
          concatMap(submission => {
            return this.studentService.getHistorySolutions(localStorage.getItem('id'), submission.id);
          }),
          toArray()
        );

        // result contiene un unico Observable
        result.subscribe((historySolutions: any[][]) => {

          // historySolutions è un Array di Array
          //  contiene, per ogni Submission, un array di solutions
          //  con corrispondenza chiave-chiave rispetto all'array di submissions

          // Per ogni Submission aggiungo alla Submission stessa l'elenco di Solutions
          //  [historySolutions] e le date formattate correttamente
          submissions.forEach((singleSubmission, key) => {
            singleSubmission.expiryString = moment(singleSubmission.expiryDate).format('L');
            singleSubmission.releaseString = moment(singleSubmission.releaseDate).format('L');
            singleSubmission.history = historySolutions[key];

            // Aggiungo la Submission aggiornata all'array
            consegne.push(singleSubmission);
          });
          // Aggiorno l'array di Submission ottenuto per popolare la vista
          this.consegne = consegne;
          if (consegne.length > 0) {
            this.hasConsegne = true;
          }
        });
      },
      (error) => {

      }
    );
  }

  ngOnInit(): void {
  }

  handleFileSelect($event: any) {
    this.file = $event.target.files[0];
    if (this.file !== undefined) {
      this.filename = this.file.name;
    } else {
      this.filename = 'Choose file';
    }
  }

  uploadSolution(id: number) {
    if (this.file !== undefined) {
      this.studentService.addSolution(localStorage.getItem('id'), id, this.file).subscribe(
        (res) => {
          this.snackBar.open('Solution uploaded successfully.', 'OK', {
            duration: 5000
          });
        },
        (error) => {
          this.snackBar.open('Error uploading solution, try again.', 'OK', {
            duration: 5000
          });
        }
      );
    } else {
      this.snackBar.open('You must select a file!', 'OK', {
        duration: 5000
      });
    }
  }

  handleShowSubmission(id: number) {
    this.studentService.getSubmissionById(this.corso.name, id).subscribe((res) => {
      console.log(res);
      console.log("HERE");
    });
    window.open('//' + API_URL_PUBLIC + 'courses/submissions/getImage/' + id, '_blank');
  }

  handleShowSolution(solutionId: number) {
    window.open('//' + API_URL_PUBLIC + 'students/solutions/getImage/'  + solutionId , '_blank');
  }
}
