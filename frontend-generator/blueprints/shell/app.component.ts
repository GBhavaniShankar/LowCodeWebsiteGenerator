import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, Router } from '@angular/router';
import { AuthService } from './core/services/auth.service';
import { GENERATED_MENU } from './features/generated/menu.gen';

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
  
  menuItems = GENERATED_MENU
  

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