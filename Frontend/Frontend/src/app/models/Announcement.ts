import { Course } from './Course';
import { User } from './User';

export interface Announcement {
  id: number;
  course: Course;
  createdBy: User;
  title: string;
  content: string;
  createdAt: string | Date;
}
