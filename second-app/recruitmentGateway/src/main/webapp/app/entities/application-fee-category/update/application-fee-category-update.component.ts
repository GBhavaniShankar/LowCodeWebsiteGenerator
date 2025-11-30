import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IApplicationFeeCategory } from '../application-fee-category.model';
import { ApplicationFeeCategoryService } from '../service/application-fee-category.service';
import { ApplicationFeeCategoryFormGroup, ApplicationFeeCategoryFormService } from './application-fee-category-form.service';

@Component({
  selector: 'jhi-application-fee-category-update',
  templateUrl: './application-fee-category-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ApplicationFeeCategoryUpdateComponent implements OnInit {
  isSaving = false;
  applicationFeeCategory: IApplicationFeeCategory | null = null;

  protected applicationFeeCategoryService = inject(ApplicationFeeCategoryService);
  protected applicationFeeCategoryFormService = inject(ApplicationFeeCategoryFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ApplicationFeeCategoryFormGroup = this.applicationFeeCategoryFormService.createApplicationFeeCategoryFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ applicationFeeCategory }) => {
      this.applicationFeeCategory = applicationFeeCategory;
      if (applicationFeeCategory) {
        this.updateForm(applicationFeeCategory);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const applicationFeeCategory = this.applicationFeeCategoryFormService.getApplicationFeeCategory(this.editForm);
    if (applicationFeeCategory.id !== null) {
      this.subscribeToSaveResponse(this.applicationFeeCategoryService.update(applicationFeeCategory));
    } else {
      this.subscribeToSaveResponse(this.applicationFeeCategoryService.create(applicationFeeCategory));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IApplicationFeeCategory>>): void {
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

  protected updateForm(applicationFeeCategory: IApplicationFeeCategory): void {
    this.applicationFeeCategory = applicationFeeCategory;
    this.applicationFeeCategoryFormService.resetForm(this.editForm, applicationFeeCategory);
  }
}
