import { Routes } from '@angular/router';

export const adminLayoutRoutes: Routes = [
  {
    path: '',
    loadComponent: () => import('./admin-layout/admin-layout').then(m => m.AdminLayout),
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'products' },
      { path: 'products', loadChildren: () => import('../../features/products/product.routes').then(m => m.PRODUCT_ROUTES) },
      { path: 'orders',   loadChildren: () => import('../../features/orders/order.routes').then(m => m.ORDER_ROUTES) },
      { path: 'carts',    loadChildren: () => import('../../features/carts/cart.routes').then(m => m.CART_ROUTES) },
      { path: 'users',    loadChildren: () => import('../../features/users/user.routes').then(m => m.USER_ROUTES) },
    ]
  }
];
