import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {HomeComponent} from './home/home.component';
import {RegistrationComponent} from './registration/registration.component';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatButtonModule} from '@angular/material/button';
import {HeaderComponent} from './include/header/header.component';
import {MatCardModule} from '@angular/material/card';
import {MatInputModule} from '@angular/material/input';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {AuthService} from './auth/auth.service';
import {HttpClientModule} from '@angular/common/http';
import {LoginComponent} from './login/login.component';
import {MatDialogModule} from '@angular/material/dialog';
import {TableComponent} from './include/table/table.component';
import {MatTableModule} from '@angular/material/table';
import {MatSortModule} from '@angular/material/sort';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {ProfessorComponent} from './professor/professor.component';
import {LoggedHeaderComponent} from './include/logged-header/logged-header.component';
import {MatIconModule} from '@angular/material/icon';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatListModule} from '@angular/material/list';
import {MatTabsModule} from '@angular/material/tabs';
import {EnrolledStudentsComponent} from './professor/enrolled-students/enrolled-students.component';
import {StudentComponent} from './student/student.component';
import {VmsComponent} from './professor/vms/vms.component';
import {AssignmentsComponent} from './professor/assignments/assignments.component';
import {CoursesComponent} from './professor/courses/courses.component';
import {InsertComponent} from './include/insert/insert.component';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {CreateCourseComponent} from './dialog/create-course/create-course.component';
import {TableExpandableComponent} from './include/table-expandable/table-expandable.component';
import {EditVmProfessorComponent} from './dialog/edit-vm/edit-vm-professor.component';
import {GroupsComponent} from './student/groups/groups.component';
import {VmsStudentComponent} from './student/vms-student/vms-student.component';
import {EditVmStudentComponent} from './dialog/edit-vm-student/edit-vm-student.component';
import {AssignmentsStudentComponent} from './student/assignments-student/assignments-student.component';
import {InfoGroupComponent} from './student/groups/info-group/info-group.component';
import {CreateGroupComponent} from './student/groups/create-group/create-group.component';
import {GroupDirective} from './student/groups/group.directive';
import { EditCourseComponent } from './dialog/edit-course/edit-course.component';
import {MatTooltipModule} from "@angular/material/tooltip";
import {MatExpansionModule} from "@angular/material/expansion";
import { TableFilterComponent } from './include/table-filter/table-filter.component';
import { EditHomeworkComponent } from './dialog/edit-homework/edit-homework.component';
import {MatRadioModule} from "@angular/material/radio";

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    RegistrationComponent,
    HeaderComponent,
    LoginComponent,
    TableComponent,
    ProfessorComponent,
    LoggedHeaderComponent,
    EnrolledStudentsComponent,
    StudentComponent,
    VmsComponent,
    AssignmentsComponent,
    CoursesComponent,
    InsertComponent,
    CreateCourseComponent,
    TableExpandableComponent,
    TableExpandableComponent,
    EditVmProfessorComponent,
    GroupsComponent,
    VmsStudentComponent,
    EditVmStudentComponent,
    AssignmentsStudentComponent,
    InfoGroupComponent,
    CreateGroupComponent,
    GroupDirective,
    EditCourseComponent,
    TableFilterComponent,
    EditHomeworkComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatToolbarModule,
    MatButtonModule,
    MatCardModule,
    MatInputModule,
    FormsModule,
    HttpClientModule,
    MatDialogModule,
    MatTableModule,
    MatSortModule,
    MatPaginatorModule,
    MatCheckboxModule,
    MatIconModule,
    MatSidenavModule,
    MatListModule,
    MatTabsModule,
    ReactiveFormsModule,
    MatAutocompleteModule,
    MatTooltipModule,
    MatExpansionModule,
    MatRadioModule
  ],
  providers: [AuthService],
  bootstrap: [AppComponent]
})
export class AppModule {
}
