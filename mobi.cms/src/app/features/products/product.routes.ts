import { Routes } from '@angular/router';

export const PRODUCT_ROUTES: Routes = [
    { path: '', loadComponent: () => import('./ui/product-list/product-list').then(m => m.ProductList) },
    { path: 'new', loadComponent: () => import('./ui/product-form/product-form').then(m => m.ProductForm) },
    { path: ':id', loadComponent: () => import('./ui/product-form/product-form').then(m => m.ProductForm) },
];
