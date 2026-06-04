import { Role } from '../models/Role';

export interface CreateUserRequest {
  fullName: string;
  email: string;
  role: Role;
}

export interface UpdateProfileRequest {
  fullName?: string;
  bio?: string;
  avatarUrl?: string;
}

export interface UserResponse {
  id: number;
  fullName: string;
  email: string;
  role: Role;
  bio?: string;
  avatarUrl?: string;
  createdAt: string | Date;
  enabled: boolean;
}
