import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IApplicationFeeCategory } from '../application-fee-category.model';
import { ApplicationFeeCategoryService } from '../service/application-fee-category.service';

@Component({
  templateUrl: './application-fee-category-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ApplicationFeeCategoryDeleteDialogComponent {
  applicationFeeCategory?: IApplicationFeeCategory;

  protected applicationFeeCategoryService = inject(ApplicationFeeCategoryService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.applicationFeeCategoryService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
