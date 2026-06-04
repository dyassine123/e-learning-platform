import { User } from './User';
import { Lesson } from './Lesson';

export interface LessonProgress {
  id: number;
  student: User;
  lesson: Lesson;
  completed: boolean;
  progressPercent: number; // 0..100
  lastSeenAt: string | Date;
}
