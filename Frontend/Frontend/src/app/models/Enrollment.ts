import { Course } from './Course';
import { User } from './User';

export interface Enrollment {
  id: number;
  student: User;
  course: Course;
  enrolledAt: string | Date;
}
