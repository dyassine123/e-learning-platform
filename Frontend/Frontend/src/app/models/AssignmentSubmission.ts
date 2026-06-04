import { Assignment } from './Assignment';
import { User } from './User';

export interface AssignmentSubmission {
  id: number;
  assignment: Assignment;
  student: User;
  fileUrl?: string;
  textSubmission?: string;
  grade?: number;
  teacherComment?: string;
  submittedAt: string | Date;
  gradedAt?: string | Date;
}
