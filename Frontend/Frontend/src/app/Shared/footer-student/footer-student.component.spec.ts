import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FooterStudentComponent } from './footer-student.component';

describe('FooterStudentComponent', () => {
  let component: FooterStudentComponent;
  let fixture: ComponentFixture<FooterStudentComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FooterStudentComponent]
    });
    fixture = TestBed.createComponent(FooterStudentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
