export interface AnnouncementCreateRequest {
  courseId: number;
  title: string;
  content: string;
}

export interface AnnouncementResponse {
  id: number;
  courseId: number;
  createdById: number;
  title: string;
  content: string;
  createdAt: string | Date;
}
