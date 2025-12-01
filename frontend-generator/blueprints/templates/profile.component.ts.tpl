import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './profile.component.html',
  styles: [`
    .profile-card { max-width: 500px; margin: 0 auto; background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
    .profile-field { margin-bottom: 1.5rem; }
    .label { font-weight: bold; color: #666; display: block; margin-bottom: 0.5rem; }
    .value { font-size: 1.2rem; color: #333; }
    .role-badge { background: #e0e7ff; color: #4338ca; padding: 4px 8px; border-radius: 4px; font-size: 0.9rem; }
  `]
})
export class ProfileComponent implements OnInit {
  email: string = '';
  role: string = '';

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    // We decode the token to get user details
    const token = this.authService.getToken();
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        this.email = payload.sub; // 'sub' is standard for username/email
        this.role = this.authService.getUserRole();
      } catch (e) {
        console.error('Failed to decode token');
      }
    }
  }
}