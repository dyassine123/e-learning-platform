import { QuizSubmission } from './QuizSubmission';
import { QuizQuestion } from './QuizQuestion';

export interface QuizAnswer {
  id: number;
  submission: QuizSubmission;
  question: QuizQuestion;
  selectedChoiceId?: number;
  answerText?: string;
  correct: boolean;
}
