import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IApplicationFeeCategory } from '../application-fee-category.model';

@Component({
  selector: 'jhi-application-fee-category-detail',
  templateUrl: './application-fee-category-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class ApplicationFeeCategoryDetailComponent {
  applicationFeeCategory = input<IApplicationFeeCategory | null>(null);

  previousState(): void {
    window.history.back();
  }
}
