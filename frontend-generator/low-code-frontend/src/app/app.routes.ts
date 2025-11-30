import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';

// UNCOMMENT THIS LINE LATER (after running your generator script):
import { GENERATED_ROUTES } from './features/generated/routes.gen';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  
  // UNCOMMENT THIS LINE LATER:
  ...GENERATED_ROUTES,

  // Fallback route
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' }
];