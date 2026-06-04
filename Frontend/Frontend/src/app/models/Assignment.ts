import { Course } from './Course';
import { User } from './User';
import { AssignmentStatus } from './AssignmentStatus';

export interface Assignment {
  id: number;
  course: Course;
  createdBy: User;
  title: string;
  description: string;
  attachmentUrl?: string;
  dueDate?: string | Date;
  status: AssignmentStatus;
  createdAt: string | Date;
  updatedAt: string | Date;
}
