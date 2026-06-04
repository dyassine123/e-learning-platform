import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

// Components
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
import { CoursDetailComponent } from './Student/cours-detail/cours-detail.component';
import { LessonViewComponent } from './Student/lesson-view/lesson-view.component';
import { CreateCoursComponent } from './Teacher/create-cours/create-cours.component';
import { LoginAdminComponent } from './Admin/login-admin/login-admin.component';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { VisitorComponent } from './visitor/visitor.component';
import { ListEnrollementComponent } from './Student/list-enrollement/list-enrollement.component';
import { ListCoursComponent } from './Student/list-cours/list-cours.component';
import { RoleGuard } from './guards/role.guard';
import { GuestOnlyGuard } from './guards/guest-only.guard';

// Layouts (Removed for manual integration)

const routes: Routes = [
  // Authentication Routes
  // Primary Entry Point
  { path: '', component: VisitorComponent, canActivate: [GuestOnlyGuard] },
  { path: 'login', component: LoginComponent, canActivate: [GuestOnlyGuard] },
  { path: 'register', component: RegisterComponent, canActivate: [GuestOnlyGuard] },
  { path: 'admin/login', component: LoginAdminComponent, canActivate: [GuestOnlyGuard] },

  // Admin Routes (Flattened)
  { path: 'admin/dashboard', component: DashboardAdminComponent, canActivate: [RoleGuard], data: { role: 'ADMIN' } },
  { path: 'admin/users', component: UserManagementComponent, canActivate: [RoleGuard], data: { role: 'ADMIN' } },
  { path: 'admin/approvals', component: CourseApprovalComponent, canActivate: [RoleGuard], data: { role: 'ADMIN' } },
  { path: 'admin/categories', component: CategoryManagementComponent, canActivate: [RoleGuard], data: { role: 'ADMIN' } },

  // Student Routes (Flattened)
  { path: 'student/dashboard', component: DashboardStudentComponent, canActivate: [RoleGuard], data: { role: 'STUDENT' } },
  { path: 'student/courses', component: MyCoursesComponent, canActivate: [RoleGuard], data: { role: 'STUDENT' } },
  { path: 'student/enrollments', component: ListEnrollementComponent, canActivate: [RoleGuard], data: { role: 'STUDENT' } },
  { path: 'student/explore', component: ListCoursComponent, canActivate: [RoleGuard], data: { role: 'STUDENT' } },
  { path: 'student/lesson/:id', component: LessonViewComponent, canActivate: [RoleGuard], data: { role: 'STUDENT' } },
  { path: 'student/quiz', component: QuizPlayerComponent, canActivate: [RoleGuard], data: { role: 'STUDENT' } },
  { path: 'student/quiz-player', component: QuizPlayerComponent, canActivate: [RoleGuard], data: { role: 'STUDENT' } },
  { path: 'student/course-detail/:id', component: CoursDetailComponent, canActivate: [RoleGuard], data: { role: 'STUDENT' } },

  // Teacher Routes (Flattened)
  { path: 'teacher/dashboard', component: DashboardTeacherComponent, canActivate: [RoleGuard], data: { role: 'TEACHER' } },
  { path: 'teacher/courses', component: CourseListComponent, canActivate: [RoleGuard], data: { role: 'TEACHER' } },
  { path: 'teacher/courses/:id/lessons', component: LessonEditorComponent, canActivate: [RoleGuard], data: { role: 'TEACHER' } },
  { path: 'teacher/quiz-builder', component: QuizBuilderComponent, canActivate: [RoleGuard], data: { role: 'TEACHER' } },
  { path: 'teacher/student-progress', component: StudentProgressComponent, canActivate: [RoleGuard], data: { role: 'TEACHER' } },
  { path: 'teacher/create-course', component: CreateCoursComponent, canActivate: [RoleGuard], data: { role: 'TEACHER' } },

  // Catch-all
  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
