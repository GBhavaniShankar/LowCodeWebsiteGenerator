import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IConfig } from '../config.model';
import { ConfigService } from '../service/config.service';
import { ConfigFormGroup, ConfigFormService } from './config-form.service';

@Component({
  selector: 'jhi-config-update',
  templateUrl: './config-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ConfigUpdateComponent implements OnInit {
  isSaving = false;
  config: IConfig | null = null;

  protected configService = inject(ConfigService);
  protected configFormService = inject(ConfigFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ConfigFormGroup = this.configFormService.createConfigFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ config }) => {
      this.config = config;
      if (config) {
        this.updateForm(config);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const config = this.configFormService.getConfig(this.editForm);
    if (config.id !== null) {
      this.subscribeToSaveResponse(this.configService.update(config));
    } else {
      this.subscribeToSaveResponse(this.configService.create(config));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IConfig>>): void {
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

  protected updateForm(config: IConfig): void {
    this.config = config;
    this.configFormService.resetForm(this.editForm, config);
  }
}
