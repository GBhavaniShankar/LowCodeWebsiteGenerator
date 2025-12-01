import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { {{ResourceName}}Service } from './{{ResourceNameLower}}.service';

@Component({
  selector: 'app-{{ResourceNameLower}}-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './{{ResourceNameLower}}-form.component.html'
})
export class {{ResourceName}}FormComponent {
  form: FormGroup;

  constructor(
    private fb: FormBuilder,
    private service: {{ResourceName}}Service,
    private router: Router
  ) {
    this.form = this.fb.group({
{{FormControls}}
    });
  }

  onSubmit() {
    if (this.form.valid) {
      const payload = { ...this.form.value }

      {{RefLogic}}

      this.service.create(payload).subscribe(() => {
        this.router.navigate(['/{{ResourceNameLower}}s']);
      });
    }
  }
}