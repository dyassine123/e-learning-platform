import { QuestionType } from '../models/QuestionType';

export interface EnrollmentStudentResponse {
  studentId: number;
  fullName: string;
  email: string;
  enrolledAt: string | Date;
}

export interface LessonProgressRowResponse {
  lessonId: number;
  lessonTitle: string;
  displayOrder: number;
  progressPercent: number;
  completed: boolean;
}

export interface StudentProgressRowResponse {
  studentId: number;
  fullName: string;
  email: string;
  overallProgressPercent: number;
  completedLessons: number;
  totalLessons: number;
  lessons: LessonProgressRowResponse[];
}

export interface CourseStudentLessonProgressResponse {
  courseId: number;
  courseTitle: string;
  totalLessons: number;
  students: StudentProgressRowResponse[];
}

export interface QuizSubmissionSummaryResponse {
  submissionId: number;
  studentId: number;
  studentFullName: string;
  studentEmail: string;
  score: number;
  submittedAt: string | Date;
}

export interface AnswerDetail {
  questionId: number;
  type: QuestionType;
  questionText: string;
  points: number;
  selectedChoiceId?: number;
  selectedChoiceText?: string;
  answerText?: string;
  correct: boolean;
}

export interface QuizSubmissionDetailResponse {
  submissionId: number;
  quizId: number;
  studentId: number;
  studentFullName: string;
  score: number;
  submittedAt: string | Date;
  answers: AnswerDetail[];
}
