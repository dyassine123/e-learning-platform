import { Course } from './Course';

export interface CourseSection {
  id: number;
  course: Course;
  title: string;
  description?: string;
  displayOrder: number;
  createdAt: string | Date;
}
