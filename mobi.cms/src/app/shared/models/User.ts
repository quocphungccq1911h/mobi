import { Role } from './Role';

export interface User {
  id: number;
  username: string;
  email: string;
  password?: string; // optional, thường không trả về từ API
  roles: Role[];
}
