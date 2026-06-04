import { Course } from './Course';
import { User } from './User';

export interface CourseReview {
  id: number;
  course: Course;
  student: User;
  rating: number; // 1..5
  comment?: string;
  createdAt: string | Date;
}
