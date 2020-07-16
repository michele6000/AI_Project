import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {HomeComponent} from './home/home.component';
import {RegistrationComponent} from './registration/registration.component';
import {ProfessorComponent} from './professor/professor.component';
import {EnrolledStudentsComponent} from './professor/enrolled-students/enrolled-students.component';
import {StudentComponent} from './student/student.component';
import {VmsComponent} from './professor/vms/vms.component';
import {AssignmentsComponent} from './professor/assignments/assignments.component';
import {CoursesComponent} from './professor/courses/courses.component';
import {GroupsComponent} from './student/groups/groups.component';
import {VmsStudentComponent} from "./student/vms-student/vms-student.component";


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
    path: 'teacher/courses',
    component: CoursesComponent
  },
  {
    path: 'teacher',
    component: ProfessorComponent
  },
  {
    path: 'teacher/:course',
    component: ProfessorComponent,
    children: [
      {
        path: 'students',
        component: EnrolledStudentsComponent
      },
      {
        path: 'vms',
        component: VmsComponent
      },
      {
        path: 'assignments',
        component: AssignmentsComponent
      }
    ]
  },
  {
    path: 'student',
    component: StudentComponent
  },
  {
    path: 'student/:course',
    component: StudentComponent,
    children: [
      {
        path: 'groups',
        component: GroupsComponent
      },
      {
        path: 'vms',
        component: VmsStudentComponent
      },
      {
        path: 'assignments',
        component: AssignmentsComponent
      }
    ]
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
