import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { {{ResourceName}}Service } from './{{ResourceNameLower}}.service';
import { {{ResourceName}} } from './{{ResourceNameLower}}.model';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-{{ResourceNameLower}}-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './{{ResourceNameLower}}-list.component.html'
})
export class {{ResourceName}}ListComponent implements OnInit {
  items: {{ResourceName}}[] = [];
  canCreate = false;

  constructor(
    private service: {{ResourceName}}Service,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const allowedRoles: string[] = [{{AllowedRoles}}]; 
    this.canCreate = this.authService.hasPermission(allowedRoles);

    // Try to load ALL items first
    this.service.getAll().subscribe({
      next: (data) => this.items = data,
      error: (err) => {
        // If 403 Forbidden, it means we might only have "view-own" permission.
        // Fallback to "getMy()"
        if (err.status === 403) {
          console.warn('View-All denied. Falling back to View-Own.');
          this.service.getMy().subscribe({
            next: (data) => this.items = data,
            error: (e) => console.error('Failed to load {{ResourceNameLower}}s', e)
          });
        } else {
          console.error('Failed to load {{ResourceNameLower}}s', err);
        }
      }
    });
  }
}