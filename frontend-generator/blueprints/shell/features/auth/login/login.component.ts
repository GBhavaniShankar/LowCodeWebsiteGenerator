import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html'
})
export class LoginComponent {
  loginForm: FormGroup;
  errorMsg: string = '';
  isLoading: boolean = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    // FIX: If user is already logged in, don't show login page.
    if (this.authService.getToken()) {
      this.router.navigate(['/teams']);
    }

    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit(): void {
    if (this.loginForm.invalid) return;

    this.isLoading = true;
    this.authService.login(this.loginForm.value).subscribe({
      next: () => {
        // FIX: Redirect to a real page upon success
        this.router.navigate(['/teams']);
      },
      error: (err) => {
        this.errorMsg = 'Invalid email or password';
        this.isLoading = false;
      }
    });
  }
}