import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IConfig } from '../config.model';
import { ConfigService } from '../service/config.service';

@Component({
  templateUrl: './config-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ConfigDeleteDialogComponent {
  config?: IConfig;

  protected configService = inject(ConfigService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.configService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
