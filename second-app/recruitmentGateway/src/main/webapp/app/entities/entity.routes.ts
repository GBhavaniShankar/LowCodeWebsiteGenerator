import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'recruitmentGatewayApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'application',
    data: { pageTitle: 'recruitmentGatewayApp.application.home.title' },
    loadChildren: () => import('./application/application.routes'),
  },
  {
    path: 'config',
    data: { pageTitle: 'recruitmentGatewayApp.config.home.title' },
    loadChildren: () => import('./config/config.routes'),
  },
  {
    path: 'notification',
    data: { pageTitle: 'recruitmentGatewayApp.notification.home.title' },
    loadChildren: () => import('./notification/notification.routes'),
  },
  {
    path: 'advertisement',
    data: { pageTitle: 'recruitmentGatewayApp.advertisement.home.title' },
    loadChildren: () => import('./advertisement/advertisement.routes'),
  },
  {
    path: 'applicant',
    data: { pageTitle: 'recruitmentGatewayApp.applicant.home.title' },
    loadChildren: () => import('./applicant/applicant.routes'),
  },
  {
    path: 'application-fee-category',
    data: { pageTitle: 'recruitmentGatewayApp.applicationFeeCategory.home.title' },
    loadChildren: () => import('./application-fee-category/application-fee-category.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
