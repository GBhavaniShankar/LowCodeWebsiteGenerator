import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import ApplicantResolve from './route/applicant-routing-resolve.service';

const applicantRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/applicant.component').then(m => m.ApplicantComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/applicant-detail.component').then(m => m.ApplicantDetailComponent),
    resolve: {
      applicant: ApplicantResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/applicant-update.component').then(m => m.ApplicantUpdateComponent),
    resolve: {
      applicant: ApplicantResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/applicant-update.component').then(m => m.ApplicantUpdateComponent),
    resolve: {
      applicant: ApplicantResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default applicantRoute;
