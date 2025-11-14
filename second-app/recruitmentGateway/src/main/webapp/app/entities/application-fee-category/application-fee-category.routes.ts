import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import ApplicationFeeCategoryResolve from './route/application-fee-category-routing-resolve.service';

const applicationFeeCategoryRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/application-fee-category.component').then(m => m.ApplicationFeeCategoryComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/application-fee-category-detail.component').then(m => m.ApplicationFeeCategoryDetailComponent),
    resolve: {
      applicationFeeCategory: ApplicationFeeCategoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/application-fee-category-update.component').then(m => m.ApplicationFeeCategoryUpdateComponent),
    resolve: {
      applicationFeeCategory: ApplicationFeeCategoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/application-fee-category-update.component').then(m => m.ApplicationFeeCategoryUpdateComponent),
    resolve: {
      applicationFeeCategory: ApplicationFeeCategoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default applicationFeeCategoryRoute;
