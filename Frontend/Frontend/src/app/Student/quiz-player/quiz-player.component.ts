import { Component, OnInit } from '@angular/core';
import { forkJoin } from 'rxjs';
import { map } from 'rxjs/operators';
import { CourseService } from '../../services/course.service';
import { AssessmentService } from '../../services/assessment.service';
import {
  AnswerRequest,
  QuizQuestionResponse,
  QuizResponse,
  SubmissionRequest,
  SubmissionResponse
} from '../../dtos/assessment.dto';
import { EnrollmentResponse } from '../../dtos/course.dto';

@Component({
  selector: 'app-quiz-player',
  templateUrl: './quiz-player.component.html',
  styleUrls: ['./quiz-player.component.css']
})
export class QuizPlayerComponent implements OnInit {
  enrolledCourses: EnrollmentResponse[] = [];
  quizzes: QuizResponse[] = [];
  allQuizzes: (QuizResponse & { courseTitle: string })[] = [];
  questions: QuizQuestionResponse[] = [];

  selectedCourseId?: number;
  selectedQuizId?: number;

  answers = new Map<number, { selectedChoiceId?: number; answerText?: string }>();
  loading = false;
  submitting = false;
  submitted?: SubmissionResponse;
  mySubmissions: SubmissionResponse[] = [];
  error = '';

  constructor(
    private courseService: CourseService,
    private assessmentService: AssessmentService
  ) {}

  ngOnInit(): void {
    this.loadEnrolledCourses();
  }

  loadEnrolledCourses(): void {
    this.courseService.listMyEnrollments(0, 100).subscribe({
      next: (page) => {
        this.enrolledCourses = page.content;
        if (this.enrolledCourses.length > 0) {
          this.onCourseChange(this.enrolledCourses[0].courseId);
          this.fetchAllQuizzes();
        }
      },
      error: () => this.error = 'Failed to load your enrolled courses.'
    });
  }

  private fetchAllQuizzes(): void {
    this.allQuizzes = [];
    const quizRequests = this.enrolledCourses.map(course =>
      this.assessmentService.listQuizzesForCourse(course.courseId).pipe(
        map(quizzes => quizzes
          .filter(q => q.status === 'PUBLISHED')
          .map(q => ({ ...q, courseTitle: course.courseTitle }))
        )
      )
    );

    forkJoin(quizRequests).subscribe({
      next: (quizGroups) => {
        this.allQuizzes = quizGroups.flat();
      },
      error: (err) => console.error('Error fetching all quizzes', err)
    });
  }

  onCourseChange(courseId: number): void {
    this.selectedCourseId = courseId;
    this.selectedQuizId = undefined;
    this.questions = [];
    this.answers.clear();
    this.submitted = undefined;
    this.mySubmissions = [];
    this.error = '';
    if (!this.selectedCourseId) return;

    this.loading = true;
    forkJoin({
      quizzes: this.assessmentService.listQuizzesForCourse(this.selectedCourseId),
      submissions: this.assessmentService.listMySubmissions(this.selectedCourseId)
    }).subscribe({
      next: ({ quizzes, submissions }) => {
        this.mySubmissions = submissions;
        this.quizzes = quizzes.filter(q => q.status === 'PUBLISHED');
        this.loading = false;
        if (this.quizzes.length > 0) {
          this.selectQuiz(this.quizzes[0].id);
        }
      },
      error: () => {
        this.loading = false;
        this.error = 'Failed to load quizzes or your submissions.';
      }
    });
  }

  selectQuiz(quizId: number | string | undefined | null): void {
    if (quizId === undefined || quizId === null || quizId === '') {
      this.selectedQuizId = undefined;
      this.submitted = undefined;
      this.questions = [];
      this.answers.clear();
      this.loading = false;
      return;
    }

    const id = typeof quizId === 'string' ? Number(quizId) : quizId;
    if (Number.isNaN(id as number)) return;

    this.selectedQuizId = id;
    this.submitted = this.mySubmissions.find(s => s.quizId === id);
    this.answers.clear();
    this.loading = true;
    this.assessmentService.listQuestionsForQuiz(id as number).subscribe({
      next: (questions) => {
        this.questions = questions.map((question) => ({
          ...question,
          choices: question.type === 'MCQ'
            ? this.shuffleChoices(question.choices || [])
            : question.choices
        }));
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.error = 'Failed to load quiz questions.';
      }
    });
  }

  selectChoice(questionId: number, choiceId: number): void {
    this.answers.set(questionId, { selectedChoiceId: choiceId });
  }

  setTextAnswer(questionId: number, text: string): void {
    this.answers.set(questionId, { answerText: text });
  }

  submitQuiz(): void {
    if (!this.selectedQuizId || this.questions.length === 0) return;
    this.error = '';

    const answerRequests: AnswerRequest[] = this.questions.map(q => {
      const answer = this.answers.get(q.id) || {};
      return {
        questionId: q.id,
        selectedChoiceId: answer.selectedChoiceId,
        answerText: answer.answerText
      };
    });

    const hasMissing = this.questions.some(q => {
      const a = this.answers.get(q.id);
      if (!a) return true;
      if (q.type === 'MCQ') return !a.selectedChoiceId;
      return !(a.answerText && a.answerText.trim().length > 0);
    });
    if (hasMissing) {
      this.error = 'Please answer all questions before submitting.';
      return;
    }

    const request: SubmissionRequest = {
      quizId: this.selectedQuizId,
      answers: answerRequests
    };

    this.submitting = true;
    this.assessmentService.submitQuiz(request).subscribe({
      next: (res) => {
        this.submitting = false;
        this.submitted = res;
        this.mySubmissions = [res, ...this.mySubmissions.filter(s => s.quizId !== res.quizId)];
      },
      error: (err) => {
        this.submitting = false;
        this.error = err?.error?.message || 'Failed to submit quiz.';
      }
    });
  }

  hasSubmitted(quizId: number): boolean {
    return this.mySubmissions.some(s => s.quizId === quizId);
  }

  get quizTotalPoints(): number {
    return this.questions.reduce((total, question) => total + (question.points || 0), 0);
  }

  private shuffleChoices<T>(items: T[]): T[] {
    const shuffled = [...items];
    for (let i = shuffled.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];
    }
    return shuffled;
  }
}
