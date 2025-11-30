import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, Router } from '@angular/router';
import { AuthService } from './core/services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  // isLoggedIn = false;
  isLoggedIn = true; // TEMPORARY: to always show sidebar during development
  
  // This list will eventually be populated by your Generator Script logic 
  // or you can simply hardcode links for now.
  // For the final automation, we often make the sidebar dynamic too.
  menuItems = [
    { label: 'Teams', link: '/teams' },
    { label: 'Sprints', link: '/sprints' },
    { label: 'Tickets', link: '/tickets' }
  ];

  constructor(private authService: AuthService) {
    // Subscribe to auth state to show/hide sidebar
    this.authService.isAuthenticated$.subscribe(val => {
      this.isLoggedIn = val;
    });
  }

  logout() {
    this.authService.logout();
  }
}