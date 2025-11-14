import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IApplication } from '../application.model';

@Component({
  selector: 'jhi-application-detail',
  templateUrl: './application-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class ApplicationDetailComponent {
  application = input<IApplication | null>(null);

  previousState(): void {
    window.history.back();
  }
}
