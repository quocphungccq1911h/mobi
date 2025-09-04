import { OrderItem } from './OrderItem';
import { User } from './User';

export interface Order {
  id: number;
  user: User;
  totalAmount: number; // BigDecimal -> number
  createdAt: string; // LocalDateTime -> string (ISO format từ backend)
  updatedAt: string;
  orderItems: OrderItem[];
}
