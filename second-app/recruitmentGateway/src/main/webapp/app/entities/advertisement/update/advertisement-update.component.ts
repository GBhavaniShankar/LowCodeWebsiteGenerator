import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { AdvertisementService } from '../service/advertisement.service';
import { IAdvertisement } from '../advertisement.model';
import { AdvertisementFormGroup, AdvertisementFormService } from './advertisement-form.service';

@Component({
  selector: 'jhi-advertisement-update',
  templateUrl: './advertisement-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class AdvertisementUpdateComponent implements OnInit {
  isSaving = false;
  advertisement: IAdvertisement | null = null;

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected advertisementService = inject(AdvertisementService);
  protected advertisementFormService = inject(AdvertisementFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: AdvertisementFormGroup = this.advertisementFormService.createAdvertisementFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ advertisement }) => {
      this.advertisement = advertisement;
      if (advertisement) {
        this.updateForm(advertisement);
      }
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(
          new EventWithContent<AlertError>('recruitmentGatewayApp.error', { ...err, key: `error.file.${err.key}` }),
        ),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const advertisement = this.advertisementFormService.getAdvertisement(this.editForm);
    if (advertisement.id !== null) {
      this.subscribeToSaveResponse(this.advertisementService.update(advertisement));
    } else {
      this.subscribeToSaveResponse(this.advertisementService.create(advertisement));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAdvertisement>>): void {
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

  protected updateForm(advertisement: IAdvertisement): void {
    this.advertisement = advertisement;
    this.advertisementFormService.resetForm(this.editForm, advertisement);
  }
}
