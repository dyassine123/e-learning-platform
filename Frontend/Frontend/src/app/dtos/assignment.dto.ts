import { AssignmentStatus } from '../models/AssignmentStatus';

export interface AssignmentCreateRequest {
  courseId: number;
  title: string;
  description: string;
  attachmentUrl?: string;
  dueDate?: string | Date;
}

export interface AssignmentResponse {
  id: number;
  courseId: number;
  createdById: number;
  title: string;
  description: string;
  attachmentUrl?: string;
  dueDate?: string | Date;
  status: AssignmentStatus;
  createdAt: string | Date;
  updatedAt: string | Date;
}

export interface AssignmentSubmissionRequest {
  fileUrl?: string;
  textSubmission?: string;
}

export interface AssignmentSubmissionResponse {
  id: number;
  assignmentId: number;
  studentId: number;
  studentFullName: string;
  fileUrl?: string;
  textSubmission?: string;
  grade?: number;
  teacherComment?: string;
  submittedAt: string | Date;
  gradedAt?: string | Date;
}

export interface GradeSubmissionRequest {
  grade: number;
  teacherComment?: string;
}
