import { Component, OnInit } from '@angular/core';
import { CourseResponse } from '../../dtos/course.dto';
import { QuestionCreateRequest, QuizQuestionResponse, QuizResponse } from '../../dtos/assessment.dto';
import { QuizSubmissionSummaryResponse } from '../../dtos/dashboard.dto';
import { QuestionType } from '../../models/QuestionType';
import { CourseService } from '../../services/course.service';
import { AssessmentService } from '../../services/assessment.service';
import { DashboardService } from '../../services/dashboard.service';

@Component({
  selector: 'app-quiz-builder',
  templateUrl: './quiz-builder.component.html',
  styleUrls: ['./quiz-builder.component.css']
})
export class QuizBuilderComponent implements OnInit {
  readonly questionTypes = Object.values(QuestionType);
  courses: CourseResponse[] = [];
  quizzes: QuizResponse[] = [];
  questions: QuizQuestionResponse[] = [];
  submissions: QuizSubmissionSummaryResponse[] = [];

  selectedCourseId?: number;
  selectedQuizId?: number;

  creatingQuiz = false;
  addingQuestion = false;
  publishing = false;
  loading = false;

  quizTitle = '';
  message = '';
  error = '';

  questionForm: {
    type: QuestionType;
    questionText: string;
    points: number;
    choices: { choiceText: string; correct: boolean }[];
    correctAnswerText: string;
  } = {
    type: QuestionType.MCQ,
    questionText: '',
    points: 1,
    choices: [
      { choiceText: '', correct: true },
      { choiceText: '', correct: false }
    ],
    correctAnswerText: ''
  };

  constructor(
    private courseService: CourseService,
    private assessmentService: AssessmentService,
    private dashboardService: DashboardService
  ) {}

  ngOnInit(): void {
    this.loadTeacherCourses();
  }

  loadTeacherCourses(): void {
    this.courseService.listMine(0, 100).subscribe({
      next: (res) => {
        this.courses = res.content;
        if (this.courses.length > 0) this.onCourseChange(this.courses[0].id);
      },
      error: () => this.setError('Failed to load your courses.')
    });
  }

  onCourseChange(courseId: number): void {
    this.selectedCourseId = courseId;
    this.selectedQuizId = undefined;
    this.questions = [];
    this.loadQuizzes();
  }

  loadQuizzes(): void {
    if (!this.selectedCourseId) return;
    this.loading = true;
    this.assessmentService.listQuizzesForCourse(this.selectedCourseId).subscribe({
      next: (quizzes) => {
        this.quizzes = quizzes;
        this.loading = false;
        if (quizzes.length > 0) this.selectQuiz(quizzes[0].id);
      },
      error: () => {
        this.loading = false;
        this.setError('Failed to load quizzes for this course.');
      }
    });
  }

  createQuiz(): void {
    if (!this.selectedCourseId || !this.quizTitle.trim()) return;
    this.creatingQuiz = true;
    this.clearMessages();
    this.assessmentService.createQuiz({
      courseId: this.selectedCourseId,
      title: this.quizTitle.trim()
    }).subscribe({
      next: (quiz) => {
        this.creatingQuiz = false;
        this.quizTitle = '';
        this.quizzes = [quiz, ...this.quizzes];
        this.selectQuiz(quiz.id);
        this.message = 'Quiz created successfully.';
      },
      error: (err) => {
        this.creatingQuiz = false;
        this.setError(err?.error?.message || 'Failed to create quiz.');
      }
    });
  }

  selectQuiz(quizId: number): void {
    this.selectedQuizId = quizId;
    this.loadQuestions();
    this.loadSubmissions();
  }

  loadQuestions(): void {
    if (!this.selectedQuizId) return;
    this.assessmentService.listQuestionsForQuiz(this.selectedQuizId).subscribe({
      next: (questions) => this.questions = questions,
      error: () => this.setError('Failed to load quiz questions.')
    });
  }

  addChoice(): void {
    this.questionForm.choices.push({ choiceText: '', correct: false });
  }

  setCorrectChoice(index: number): void {
    this.questionForm.choices = this.questionForm.choices.map((choice, i) => ({ ...choice, correct: i === index }));
  }

  addQuestion(): void {
    if (!this.selectedQuizId || !this.questionForm.questionText.trim()) return;
    this.clearMessages();

    const request: QuestionCreateRequest = {
      quizId: this.selectedQuizId,
      type: this.questionForm.type,
      questionText: this.questionForm.questionText.trim(),
      points: this.questionForm.points
    };

    if (this.questionForm.type === QuestionType.MCQ) {
      const choices = this.questionForm.choices
        .filter(c => c.choiceText.trim())
        .map(c => ({ choiceText: c.choiceText.trim(), correct: c.correct }));
      if (choices.length < 2 || choices.filter(c => c.correct).length !== 1) {
        this.setError('MCQ needs at least 2 choices and exactly 1 correct answer.');
        return;
      }
      request.choices = choices;
    } else {
      if (!this.questionForm.correctAnswerText.trim()) {
        this.setError('Correct answer is required for this question type.');
        return;
      }
      request.correctAnswerText = this.questionForm.correctAnswerText.trim();
    }

    this.addingQuestion = true;
    this.assessmentService.addQuestion(request).subscribe({
      next: () => {
        this.addingQuestion = false;
        this.resetQuestionForm();
        this.loadQuestions();
        this.message = 'Question added successfully.';
      },
      error: (err) => {
        this.addingQuestion = false;
        this.setError(err?.error?.message || 'Failed to add question.');
      }
    });
  }

  publishSelectedQuiz(): void {
    if (!this.selectedQuizId) return;
    this.publishing = true;
    this.clearMessages();
    this.assessmentService.publishQuiz(this.selectedQuizId).subscribe({
      next: (quiz) => {
        this.publishing = false;
        this.quizzes = this.quizzes.map(q => q.id === quiz.id ? quiz : q);
        this.message = 'Quiz published successfully. Synchronizing interface...';
        // Reload after a short delay to show the success message
        setTimeout(() => {
          window.location.reload();
        }, 1500);
      },
      error: (err) => {
        this.publishing = false;
        this.setError(err?.error?.message || 'Failed to publish quiz.');
      }
    });
  }

  loadSubmissions(): void {
    if (!this.selectedQuizId) return;
    this.dashboardService.getQuizSubmissions(this.selectedQuizId).subscribe({
      next: (rows) => this.submissions = rows,
      error: () => this.submissions = []
    });
  }

  get quizTotalPoints(): number {
    return this.questions.reduce((total, question) => total + (question.points || 0), 0);
  }

  private resetQuestionForm(): void {
    this.questionForm = {
      type: QuestionType.MCQ,
      questionText: '',
      points: 1,
      choices: [
        { choiceText: '', correct: true },
        { choiceText: '', correct: false }
      ],
      correctAnswerText: ''
    };
  }

  private clearMessages(): void {
    this.message = '';
    this.error = '';
  }

  private setError(message: string): void {
    this.error = message;
    this.message = '';
  }
}
