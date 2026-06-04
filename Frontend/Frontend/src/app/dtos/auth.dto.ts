import { Role } from '../models/Role';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  fullName: string;
  email: string;
  password: string;
  role: Role;
}

export interface AuthResponse {
  tokenType: string;
  accessToken: string;
  expiresAt: string | Date;
}

export interface MeResponse {
  id: number;
  email: string;
  role: Role;
}
