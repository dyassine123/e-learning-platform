import { QuizQuestion } from './QuizQuestion';

export interface QuizChoice {
  id: number;
  question: QuizQuestion;
  choiceText: string;
  correct: boolean;
}
