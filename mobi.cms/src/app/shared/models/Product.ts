import { Category } from './Category';

export interface Product {
  id: number;
  name: string;
  price: number; // BigDecimal bên Java map sang number ở TS
  description?: string;
  category?: Category; // Quan hệ Many-to-One
}
