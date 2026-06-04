import { CourseSection } from './CourseSection';

export enum LessonType {
  VIDEO = 'VIDEO',
  ARTICLE = 'ARTICLE',
  QUIZ = 'QUIZ'
}

export interface Lesson {
  id: number;
  section: CourseSection;
  title: string;
  type: LessonType;
  displayOrder: number;
  estimatedMinutes?: number;
  published: boolean;
  createdAt: string | Date;
}
