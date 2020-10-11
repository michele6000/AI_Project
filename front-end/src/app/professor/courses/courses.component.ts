import {Component, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {ActivatedRoute, Router} from '@angular/router';
import {CreateCourseComponent} from '../../dialog/create-course/create-course.component';
import {CourseModel} from '../../models/course.model';
import {EditCourseComponent} from '../../dialog/edit-course/edit-course.component';
import {ProfessorService} from '../../services/professor.service';
import {AuthService} from '../../auth/auth.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {from} from 'rxjs';
import {concatMap, toArray} from 'rxjs/operators';
import {CourseProfessorsComponent} from '../../dialog/course-professors/course-professors.component';
import {AddProfessorToCourseComponent} from '../../dialog/add-professor-to-course/add-professor-to-course.component';
import {RemoveProfessorFromCourseComponent} from '../../dialog/remove-professor-from-course/remove-professor-from-course.component';

@Component({
  selector: 'app-courses',
  templateUrl: './courses.component.html',
  styleUrls: ['./courses.component.css']
})
export class CoursesComponent implements OnInit {
  url: any;
  columns = ['acronymous', 'name', 'min', 'max'];
  data: CourseModel[] = [];
  id: string = null;

  constructor(private dialog: MatDialog, private router: Router, private activeRoute: ActivatedRoute,
              private professorService: ProfessorService, private authService: AuthService, private snackBar: MatSnackBar) {
  }

  ngOnInit(): void {
    this.activeRoute.queryParamMap
      .subscribe(params => {
        if (params.has('doCreate') && params.get('doCreate') === 'true') {
          this.dialog.open(CreateCourseComponent, {disableClose: true})
            .afterClosed()              // dopo la chiusura del dialog faccio le redirect
            .subscribe(result => {
              if (result) {              // se result è false => click su CANCEL -> redirect a HOME
                this.professorService.findCoursesByProfessor(localStorage.getItem('id'), true);
                this.router.navigate(['/teacher/courses']);
              } else {
                this.router.navigate(['/teacher/courses']);
              }
            });
        }
      });
    if (localStorage.getItem('url_teacher')) {
      this.url = localStorage.getItem('url_teacher');
    } else {
      this.url = '/teacher/';
    }

    this.authService.user.subscribe((user) => {
      if (user != null) {
        this.id = user.id;
        this.professorService.courses.subscribe(
          (courses) => {
            if (courses) {
              this.data = courses;
            } else {
              this.data = [];
            }
          }
        );
      }
    });
  }

  // @Todo: aggiornare la tabella dei corsi dopo aver fatto delete
  deleteCourse($event: CourseModel[]) {
    const res = from($event).pipe(
      concatMap(course => {
        return this.professorService.deleteCourse(course.name);
      }),
      toArray()
    );

    res.subscribe((result: boolean[]) => {
      if (result.filter(e => !e).length > 0) {
        // Almeno una ha fallito
        this.snackBar.open('Error delete courses .', 'OK', {
          duration: 5000
        });
      } else {
        // Tutte a buon fine
        this.snackBar.open('Courses deleted successfully.', 'OK', {
          duration: 5000
        });
      }
      // Aggiorno tabella
      this.professorService.findCoursesByProfessor(localStorage.getItem('id'), true);
    });
  }

  editCourse(course: CourseModel) {
    this.dialog.open(EditCourseComponent, {data: course})
      .afterClosed()
      .subscribe(result => {
        this.professorService.findCoursesByProfessor(localStorage.getItem('id'), true);
      });
  }

  changeActive($event: CourseModel) {
    if ($event.enabled) {
      this.professorService.disableCourse($event.name).subscribe((result) => this.onChangeActiveCompleted(result, $event.acronymous));
    } else {
      this.professorService.enableCourse($event.name).subscribe((result) => this.onChangeActiveCompleted(result, $event.acronymous));
    }
  }

  onChangeActiveCompleted(result: any, acronymous: string) {
    if (result) {
      this.professorService.findCoursesByProfessor(this.id, true);
    }
  }

  addProfToCourse(course: CourseModel) {
    // 1) cerco tutti i professori della piattaforma
    // 2) cerco i prof gia di quel corso
    // 3) filtro
    // 4) add prof to course
    this.professorService.findAllProfessor().subscribe(
      allProfessors => {
        this.professorService.findAllProsessorByCourse(course.name).subscribe(
          allProfessorOfCourse => {
            let professorsAfterIntersection;
            const professorsIds = allProfessorOfCourse.map(p => p.id);
            // filtro solo i professori che non sono già prof di quel corso
            professorsAfterIntersection = allProfessors.filter(p => !professorsIds.includes(p.id));
            this.dialog.open(AddProfessorToCourseComponent, {data: {professors: professorsAfterIntersection, courseName: course.name}});
          }
        );
      },
      error => {
        this.genericError();
      }
    );
  }

  // Attenzione perche possono essere piu di un professore da rimuovere
  removeProfFromCourse(course: CourseModel) {
    // 1) recupero tutti i prof di quel corso
    // 2) li passo al dialog
    this.professorService.findAllProsessorByCourse(course.name).subscribe(
      allProfessor => {
        if (allProfessor.length === 1) {
          this.snackBar.open('Warning. If you remove yourself from this course, all things attached to it will be removed too.', 'OK', {
            duration: 10000
          });
        } else {
          this.dialog.open(RemoveProfessorFromCourseComponent, {data: {professors: allProfessor, courseName: course.name}});
        }
      },
      error => {
        this.genericError();
      });
  }

  showCourseProfessor(course: CourseModel) {
    // al dialog passo il professore e il corso
    this.professorService.findAllProsessorByCourse(course.name).subscribe(result => {
        this.dialog.open(CourseProfessorsComponent, {data: {professors: result, courseName: course.name}})
          .afterClosed()
          .subscribe(res => {
          });
      },
      error => {
        this.genericError();
      });
  }

  genericError() {
    this.snackBar.open('Failed to communicate with server, try again.', 'OK', {
      duration: 5000
    });
    location.reload();
  }

}
