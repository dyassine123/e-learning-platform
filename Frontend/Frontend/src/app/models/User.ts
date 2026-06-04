import { Role } from './Role';

export interface User {
  id: number;
  fullName: string;
  email: string;
  role: Role;
  bio?: string;
  avatarUrl?: string;
  createdAt: string | Date;
}
