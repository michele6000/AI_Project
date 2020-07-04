import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {HomeComponent} from './home/home.component';
import {RegistrationComponent} from './registration/registration.component';
import {ProfessorComponent} from './professor/professor.component';
import {EnrolledStudentsComponent} from './professor/enrolled-students/enrolled-students.component';
import {StudentComponent} from './student/student.component';
import {VmsComponent} from "./professor/vms/vms.component";
import {AssignmentsComponent} from "./professor/assignments/assignments.component";


const routes: Routes = [
  {
    path: 'home',
    component: HomeComponent
  },
  {
    path: 'register',
    component: RegistrationComponent
  },
  {
    path: 'teacher/course',
    component: ProfessorComponent,
    children: [
      {
        path: ':course/students',
        component: EnrolledStudentsComponent
      },
      {
        path: 'applicazioni-internet/vms',
        component: VmsComponent
      },
      {
        path: 'applicazioni-internet/assignments',
        component: AssignmentsComponent
      }
    ]
  },
  {
    path: 'student',
    component: StudentComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
