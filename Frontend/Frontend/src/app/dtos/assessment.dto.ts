import { QuizStatus } from '../models/QuizStatus';
import { QuestionType } from '../models/QuestionType';

export interface QuizCreateRequest {
  courseId: number;
  title: string;
}

export interface QuizResponse {
  id: number;
  courseId: number;
  createdById: number;
  title: string;
  status: QuizStatus;
  createdAt: string | Date;
  updatedAt: string | Date;
}

export interface QuizChoiceResponse {
  id: number;
  choiceText: string;
  correct?: boolean | null;
}

export interface QuizQuestionResponse {
  id: number;
  quizId: number;
  type: QuestionType;
  questionText: string;
  points: number;
  correctAnswerText?: string | null;
  choices: QuizChoiceResponse[];
}

export interface ChoiceCreateRequest {
  choiceText: string;
  correct: boolean;
}

export interface QuestionCreateRequest {
  quizId: number;
  type: QuestionType;
  questionText: string;
  points: number;
  choices?: ChoiceCreateRequest[];
  correctAnswerText?: string;
}

export interface AnswerRequest {
  questionId: number;
  selectedChoiceId?: number;
  answerText?: string;
}

export interface SubmissionRequest {
  quizId: number;
  answers: AnswerRequest[];
}

export interface SubmissionResponse {
  submissionId: number;
  quizId: number;
  score: number;
  submittedAt: string | Date;
}
