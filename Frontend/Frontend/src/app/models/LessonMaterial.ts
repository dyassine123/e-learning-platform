import { Lesson } from './Lesson';

export enum MaterialType {
  VIDEO_URL = 'VIDEO_URL',
  PDF_URL = 'PDF_URL',
  EXTERNAL_LINK = 'EXTERNAL_LINK',
  TEXT = 'TEXT'
}

export interface LessonMaterial {
  id: number;
  lesson: Lesson;
  type: MaterialType;
  title?: string;
  content: string;
  createdAt: string | Date;
}
