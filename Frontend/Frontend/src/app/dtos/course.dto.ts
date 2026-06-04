import { CourseStatus } from '../models/CourseStatus';
import { LessonType } from '../models/Lesson';
import { MaterialType } from '../models/LessonMaterial';

export interface CourseCreateRequest {
  categoryId: number;
  title: string;
  description: string;
  free: boolean;
  price?: number;
  language?: string;
  level?: string;
  thumbnailUrl?: string;
  previewVideoUrl?: string;
}

export interface CourseUpdateRequest extends CourseCreateRequest {}

export interface CourseResponse {
  id: number;
  ownerId: number;
  instructorName: string;
  categoryId: number;
  categoryName: string;
  title: string;
  description: string;
  free: boolean;
  price?: number;
  language?: string;
  level?: string;
  thumbnailUrl?: string;
  previewVideoUrl?: string;
  status: CourseStatus;
  createdAt: string | Date;
  updatedAt: string | Date;
  publishedAt?: string | Date;
}

export interface CourseSectionCreateRequest {
  courseId: number;
  title: string;
  description?: string;
  displayOrder: number;
}

export interface CourseSectionResponse {
  id: number;
  courseId: number;
  title: string;
  description?: string;
  displayOrder: number;
  createdAt: string | Date;
}

export interface LessonCreateRequest {
  sectionId: number;
  title: string;
  type: LessonType;
  displayOrder: number;
  estimatedMinutes?: number;
}

export interface LessonResponse {
  id: number;
  sectionId: number;
  courseId: number;
  title: string;
  type: LessonType;
  displayOrder: number;
  estimatedMinutes?: number;
  published: boolean;
  createdAt: string | Date;
}

export interface LessonMaterialCreateRequest {
  lessonId: number;
  type: MaterialType;
  title?: string;
  content: string;
}

export interface LessonMaterialResponse {
  id: number;
  lessonId: number;
  type: MaterialType;
  title?: string;
  content: string;
  createdAt: string | Date;
}

export interface LessonProgressResponse {
  id: number;
  studentId: number;
  lessonId: number;
  completed: boolean;
  progressPercent: number;
  lastSeenAt: string | Date;
}

export interface LessonProgressUpdateRequest {
  lessonId: number;
  progressPercent: number;
}

export interface EnrollmentResponse {
  courseId: number;
  courseTitle: string;
  courseThumbnailUrl: string;
  instructorName: string;
  enrolledAt: string | Date;
}
