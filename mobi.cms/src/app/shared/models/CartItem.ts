import { Product } from './Product';
import { User } from './User';

export interface CartItem {
  id: number;
  user: User;
  product: Product;
  quantity: number;
  createdAt: string; // LocalDateTime -> string ISO (vd: "2025-09-04T21:00:00")
  updatedAt: string;
}
