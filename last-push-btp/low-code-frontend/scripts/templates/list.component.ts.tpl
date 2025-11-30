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
    // Check Permissions based on the generated config
    const allowedRoles = [{{AllowedRoles}}]; 
    this.canCreate = this.authService.hasPermission(allowedRoles);

    this.service.getAll().subscribe({
      next: (data) => this.items = data,
      error: (err) => console.error('Failed to load {{ResourceNameLower}}s', err)
    });
  }
}