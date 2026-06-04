import { Component } from '@angular/core';
import { TEACHER_NAV_ITEMS, TeacherNavItem } from '../teacher-navigation';

@Component({
  selector: 'app-side-bar-teacher',
  templateUrl: './side-bar-teacher.component.html',
  styleUrls: ['./side-bar-teacher.component.css']
})
export class SideBarTeacherComponent {
  readonly mainItems: TeacherNavItem[] = TEACHER_NAV_ITEMS.filter(i => i.section === 'main' && i.enabled);
  readonly toolItems: TeacherNavItem[] = TEACHER_NAV_ITEMS.filter(i => i.section === 'tools' && i.enabled);
}
