import { Quiz } from './Quiz';
import { User } from './User';

export interface QuizSubmission {
  id: number;
  quiz: Quiz;
  student: User;
  submittedAt: string | Date;
  score: number;
}
