import { Course } from './Course';
import { User } from './User';
import { QuizStatus } from './QuizStatus';

export interface Quiz {
  id: number;
  course: Course;
  createdBy: User;
  title: string;
  status: QuizStatus;
  createdAt: string | Date;
  updatedAt: string | Date;
}
