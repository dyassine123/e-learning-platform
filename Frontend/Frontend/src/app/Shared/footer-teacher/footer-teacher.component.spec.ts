import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FooterTeacherComponent } from './footer-teacher.component';

describe('FooterTeacherComponent', () => {
  let component: FooterTeacherComponent;
  let fixture: ComponentFixture<FooterTeacherComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FooterTeacherComponent]
    });
    fixture = TestBed.createComponent(FooterTeacherComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
