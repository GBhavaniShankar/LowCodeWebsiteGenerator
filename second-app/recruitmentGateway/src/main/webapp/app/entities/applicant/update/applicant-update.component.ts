import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IApplicant } from '../applicant.model';
import { ApplicantService } from '../service/applicant.service';
import { ApplicantFormGroup, ApplicantFormService } from './applicant-form.service';

@Component({
  selector: 'jhi-applicant-update',
  templateUrl: './applicant-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ApplicantUpdateComponent implements OnInit {
  isSaving = false;
  applicant: IApplicant | null = null;

  protected applicantService = inject(ApplicantService);
  protected applicantFormService = inject(ApplicantFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ApplicantFormGroup = this.applicantFormService.createApplicantFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ applicant }) => {
      this.applicant = applicant;
      if (applicant) {
        this.updateForm(applicant);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const applicant = this.applicantFormService.getApplicant(this.editForm);
    if (applicant.id !== null) {
      this.subscribeToSaveResponse(this.applicantService.update(applicant));
    } else {
      this.subscribeToSaveResponse(this.applicantService.create(applicant));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IApplicant>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(applicant: IApplicant): void {
    this.applicant = applicant;
    this.applicantFormService.resetForm(this.editForm, applicant);
  }
}
