export interface ReviewCreateRequest {
  rating: number; // 1..5
  comment?: string;
}

export interface ReviewResponse {
  id: number;
  courseId: number;
  studentId: number;
  studentFullName: string;
  rating: number;
  comment?: string;
  createdAt: string | Date;
}

export interface ReviewSummaryResponse {
  count: number;
  average: number;
}
