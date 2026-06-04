import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SideBarStudentComponent } from './side-bar-student.component';

describe('SideBarStudentComponent', () => {
  let component: SideBarStudentComponent;
  let fixture: ComponentFixture<SideBarStudentComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SideBarStudentComponent]
    });
    fixture = TestBed.createComponent(SideBarStudentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
