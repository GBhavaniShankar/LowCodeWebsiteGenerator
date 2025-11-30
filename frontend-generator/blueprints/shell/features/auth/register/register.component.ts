import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html'
})
export class RegisterComponent {
  registerForm: FormGroup;
  errorMsg: string = '';
  successMsg: string = '';
  isLoading: boolean = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit(): void {
    if (this.registerForm.invalid) return;

    this.isLoading = true;
    this.errorMsg = '';
    this.successMsg = '';

    this.authService.register(this.registerForm.value).subscribe({
      next: (response) => {
        this.isLoading = false;
        // Verify: response is now a simple string
        this.successMsg = response || 'Registration successful! You can now login.';
        this.registerForm.reset();
      },
      error: (err) => {
        this.isLoading = false;
        if (err.status === 400 || err.status === 500) {
           this.errorMsg = 'Email already in use or invalid data.'; 
        } else {
           this.errorMsg = 'Registration failed. Please try again.';
        }
      }
    });
  }
}