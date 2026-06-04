import { User } from './User';

export interface UserCredential {
  userId: number;
  user?: User;
  passwordHash: string;
  enabled: boolean;
  locked: boolean;
  lastLoginAt?: string | Date;
}
