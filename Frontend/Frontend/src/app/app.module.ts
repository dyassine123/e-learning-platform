import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { DashboardAdminComponent } from './Admin/dashboard-admin/dashboard-admin.component';
import { UserManagementComponent } from './Admin/user-management/user-management.component';
import { CourseApprovalComponent } from './Admin/course-approval/course-approval.component';
import { CategoryManagementComponent } from './Admin/category-management/category-management.component';
import { DashboardStudentComponent } from './Student/dashboard-student/dashboard-student.component';
import { MyCoursesComponent } from './Student/my-courses/my-courses.component';
import { QuizPlayerComponent } from './Student/quiz-player/quiz-player.component';

import { DashboardTeacherComponent } from './Teacher/dashboard-teacher/dashboard-teacher.component';
import { CourseListComponent } from './Teacher/course-list/course-list.component';
import { LessonEditorComponent } from './Teacher/lesson-editor/lesson-editor.component';
import { QuizBuilderComponent } from './Teacher/quiz-builder/quiz-builder.component';
import { StudentProgressComponent } from './Teacher/student-progress/student-progress.component';
import { LoadingSpinnerComponent } from './Shared/loading-spinner/loading-spinner.component';
import { CoursDetailComponent } from './Student/cours-detail/cours-detail.component';
import { LessonViewComponent } from './Student/lesson-view/lesson-view.component';
import { CreateCoursComponent } from './Teacher/create-cours/create-cours.component';
import { LoginAdminComponent } from './Admin/login-admin/login-admin.component';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { NavBarStudentComponent } from './Shared/nav-bar-student/nav-bar-student.component';
import { NavBarTeacherComponent } from './Shared/nav-bar-teacher/nav-bar-teacher.component';
import { NavBarAdminComponent } from './Shared/nav-bar-admin/nav-bar-admin.component';
import { SideBarStudentComponent } from './Shared/side-bar-student/side-bar-student.component';
import { SideBarAdminComponent } from './Shared/side-bar-admin/side-bar-admin.component';
import { SideBarTeacherComponent } from './Shared/side-bar-teacher/side-bar-teacher.component';
import { FooterStudentComponent } from './Shared/footer-student/footer-student.component';
import { FooterAdminComponent } from './Shared/footer-admin/footer-admin.component';
import { FooterTeacherComponent } from './Shared/footer-teacher/footer-teacher.component';
import { VisitorComponent } from './visitor/visitor.component';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { LessonMaterialComponent } from './Teacher/lesson-material/lesson-material.component';
import { ListEnrollementComponent } from './Student/list-enrollement/list-enrollement.component';
import { ListCoursComponent } from './Student/list-cours/list-cours.component';

@NgModule({
  declarations: [
    AppComponent,
    DashboardAdminComponent,
    UserManagementComponent,
    CourseApprovalComponent,
    CategoryManagementComponent,
    DashboardStudentComponent,
    MyCoursesComponent,
    QuizPlayerComponent,

    DashboardTeacherComponent,
    CourseListComponent,
    LessonEditorComponent,
    QuizBuilderComponent,
    StudentProgressComponent,

    LoadingSpinnerComponent,
    CoursDetailComponent,
    LessonViewComponent,
    CreateCoursComponent,
    LoginAdminComponent,
    LoginComponent,
    RegisterComponent,
    NavBarStudentComponent,
    NavBarTeacherComponent,
    NavBarAdminComponent,
    SideBarStudentComponent,
    SideBarAdminComponent,
    SideBarTeacherComponent,
    FooterStudentComponent,
    FooterAdminComponent,
    FooterTeacherComponent,
    VisitorComponent,
    LessonMaterialComponent,
    ListEnrollementComponent,
    ListCoursComponent,

  ],
  imports: [
    BrowserModule,
    CommonModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
