import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardTeacherComponent } from './dashboard-teacher.component';

describe('DashboardTeacherComponent', () => {
  let component: DashboardTeacherComponent;
  let fixture: ComponentFixture<DashboardTeacherComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DashboardTeacherComponent]
    });
    fixture = TestBed.createComponent(DashboardTeacherComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
