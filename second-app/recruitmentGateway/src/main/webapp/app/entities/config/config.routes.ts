import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import ConfigResolve from './route/config-routing-resolve.service';

const configRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/config.component').then(m => m.ConfigComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/config-detail.component').then(m => m.ConfigDetailComponent),
    resolve: {
      config: ConfigResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/config-update.component').then(m => m.ConfigUpdateComponent),
    resolve: {
      config: ConfigResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/config-update.component').then(m => m.ConfigUpdateComponent),
    resolve: {
      config: ConfigResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default configRoute;
