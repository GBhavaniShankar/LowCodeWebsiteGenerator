import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IApplicant } from 'app/entities/applicant/applicant.model';
import { ApplicantService } from 'app/entities/applicant/service/applicant.service';
import { IApplicationFeeCategory } from 'app/entities/application-fee-category/application-fee-category.model';
import { ApplicationFeeCategoryService } from 'app/entities/application-fee-category/service/application-fee-category.service';
import { ApplicationStatus } from 'app/entities/enumerations/application-status.model';
import { ApplicationService } from '../service/application.service';
import { IApplication } from '../application.model';
import { ApplicationFormGroup, ApplicationFormService } from './application-form.service';

@Component({
  selector: 'jhi-application-update',
  templateUrl: './application-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ApplicationUpdateComponent implements OnInit {
  isSaving = false;
  application: IApplication | null = null;
  applicationStatusValues = Object.keys(ApplicationStatus);

  applicantsSharedCollection: IApplicant[] = [];
  applicationFeeCategoriesSharedCollection: IApplicationFeeCategory[] = [];

  protected applicationService = inject(ApplicationService);
  protected applicationFormService = inject(ApplicationFormService);
  protected applicantService = inject(ApplicantService);
  protected applicationFeeCategoryService = inject(ApplicationFeeCategoryService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ApplicationFormGroup = this.applicationFormService.createApplicationFormGroup();

  compareApplicant = (o1: IApplicant | null, o2: IApplicant | null): boolean => this.applicantService.compareApplicant(o1, o2);

  compareApplicationFeeCategory = (o1: IApplicationFeeCategory | null, o2: IApplicationFeeCategory | null): boolean =>
    this.applicationFeeCategoryService.compareApplicationFeeCategory(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ application }) => {
      this.application = application;
      if (application) {
        this.updateForm(application);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const application = this.applicationFormService.getApplication(this.editForm);
    if (application.id !== null) {
      this.subscribeToSaveResponse(this.applicationService.update(application));
    } else {
      this.subscribeToSaveResponse(this.applicationService.create(application));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IApplication>>): void {
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

  protected updateForm(application: IApplication): void {
    this.application = application;
    this.applicationFormService.resetForm(this.editForm, application);

    this.applicantsSharedCollection = this.applicantService.addApplicantToCollectionIfMissing<IApplicant>(
      this.applicantsSharedCollection,
      application.applicant,
    );
    this.applicationFeeCategoriesSharedCollection =
      this.applicationFeeCategoryService.addApplicationFeeCategoryToCollectionIfMissing<IApplicationFeeCategory>(
        this.applicationFeeCategoriesSharedCollection,
        application.feeCategory,
      );
  }

  protected loadRelationshipsOptions(): void {
    this.applicantService
      .query()
      .pipe(map((res: HttpResponse<IApplicant[]>) => res.body ?? []))
      .pipe(
        map((applicants: IApplicant[]) =>
          this.applicantService.addApplicantToCollectionIfMissing<IApplicant>(applicants, this.application?.applicant),
        ),
      )
      .subscribe((applicants: IApplicant[]) => (this.applicantsSharedCollection = applicants));

    this.applicationFeeCategoryService
      .query()
      .pipe(map((res: HttpResponse<IApplicationFeeCategory[]>) => res.body ?? []))
      .pipe(
        map((applicationFeeCategories: IApplicationFeeCategory[]) =>
          this.applicationFeeCategoryService.addApplicationFeeCategoryToCollectionIfMissing<IApplicationFeeCategory>(
            applicationFeeCategories,
            this.application?.feeCategory,
          ),
        ),
      )
      .subscribe(
        (applicationFeeCategories: IApplicationFeeCategory[]) => (this.applicationFeeCategoriesSharedCollection = applicationFeeCategories),
      );
  }
}
