import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import ApplicationResolve from './route/application-routing-resolve.service';

const applicationRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/application.component').then(m => m.ApplicationComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/application-detail.component').then(m => m.ApplicationDetailComponent),
    resolve: {
      application: ApplicationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/application-update.component').then(m => m.ApplicationUpdateComponent),
    resolve: {
      application: ApplicationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/application-update.component').then(m => m.ApplicationUpdateComponent),
    resolve: {
      application: ApplicationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default applicationRoute;
