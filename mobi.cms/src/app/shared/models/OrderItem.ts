import { Product } from './Product';

export interface OrderItem {
  id: number;
  quantity: number;
  priceAtPurchase: number; // BigDecimal -> number
  product: Product;
  // ⚠️ Không nên include cả Order để tránh vòng lặp JSON
}
