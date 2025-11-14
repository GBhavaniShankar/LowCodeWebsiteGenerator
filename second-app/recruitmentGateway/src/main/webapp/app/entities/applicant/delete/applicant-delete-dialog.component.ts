import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IApplicant } from '../applicant.model';
import { ApplicantService } from '../service/applicant.service';

@Component({
  templateUrl: './applicant-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ApplicantDeleteDialogComponent {
  applicant?: IApplicant;

  protected applicantService = inject(ApplicantService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.applicantService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
