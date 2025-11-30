import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { {{ResourceName}}Service } from './{{ResourceNameLower}}.service';
import { {{ResourceName}} } from './{{ResourceNameLower}}.model';

@Component({
  selector: 'app-{{ResourceNameLower}}-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './{{ResourceNameLower}}-list.component.html'
})
export class {{ResourceName}}ListComponent implements OnInit {
  items: {{ResourceName}}[] = [];

  constructor(private service: {{ResourceName}}Service) {}

  ngOnInit(): void {
    this.service.getAll().subscribe({
      next: (data) => this.items = data,
      error: (err) => console.error('Failed to load {{ResourceNameLower}}s', err)
    });
  }
}