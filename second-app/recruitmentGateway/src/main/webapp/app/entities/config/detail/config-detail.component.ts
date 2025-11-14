import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IConfig } from '../config.model';

@Component({
  selector: 'jhi-config-detail',
  templateUrl: './config-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class ConfigDetailComponent {
  config = input<IConfig | null>(null);

  previousState(): void {
    window.history.back();
  }
}
