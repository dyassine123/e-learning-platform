import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CourseApprovalComponent } from './course-approval.component';

describe('CourseApprovalComponent', () => {
  let component: CourseApprovalComponent;
  let fixture: ComponentFixture<CourseApprovalComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CourseApprovalComponent]
    });
    fixture = TestBed.createComponent(CourseApprovalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
