import { User } from './User';
import { Category } from './Category';
import { CourseStatus } from './CourseStatus';

export interface Course {
  id: number;
  owner: User;
  category: Category;
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
