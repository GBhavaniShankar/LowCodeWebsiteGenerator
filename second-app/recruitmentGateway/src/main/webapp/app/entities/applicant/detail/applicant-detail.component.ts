import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IApplicant } from '../applicant.model';

@Component({
  selector: 'jhi-applicant-detail',
  templateUrl: './applicant-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class ApplicantDetailComponent {
  applicant = input<IApplicant | null>(null);

  previousState(): void {
    window.history.back();
  }
}
