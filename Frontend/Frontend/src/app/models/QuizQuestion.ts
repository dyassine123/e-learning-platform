import { Quiz } from './Quiz';
import { QuestionType } from './QuestionType';

export interface QuizQuestion {
  id: number;
  quiz: Quiz;
  type: QuestionType;
  questionText: string;
  points: number;
  correctAnswerText?: string;
}
