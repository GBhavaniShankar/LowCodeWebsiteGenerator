import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IApplicationFeeCategory, NewApplicationFeeCategory } from '../application-fee-category.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IApplicationFeeCategory for edit and NewApplicationFeeCategoryFormGroupInput for create.
 */
type ApplicationFeeCategoryFormGroupInput = IApplicationFeeCategory | PartialWithRequiredKeyOf<NewApplicationFeeCategory>;

type ApplicationFeeCategoryFormDefaults = Pick<NewApplicationFeeCategory, 'id'>;

type ApplicationFeeCategoryFormGroupContent = {
  id: FormControl<IApplicationFeeCategory['id'] | NewApplicationFeeCategory['id']>;
  name: FormControl<IApplicationFeeCategory['name']>;
  fee: FormControl<IApplicationFeeCategory['fee']>;
};

export type ApplicationFeeCategoryFormGroup = FormGroup<ApplicationFeeCategoryFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ApplicationFeeCategoryFormService {
  createApplicationFeeCategoryFormGroup(
    applicationFeeCategory: ApplicationFeeCategoryFormGroupInput = { id: null },
  ): ApplicationFeeCategoryFormGroup {
    const applicationFeeCategoryRawValue = {
      ...this.getFormDefaults(),
      ...applicationFeeCategory,
    };
    return new FormGroup<ApplicationFeeCategoryFormGroupContent>({
      id: new FormControl(
        { value: applicationFeeCategoryRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(applicationFeeCategoryRawValue.name, {
        validators: [Validators.required],
      }),
      fee: new FormControl(applicationFeeCategoryRawValue.fee, {
        validators: [Validators.required, Validators.min(0)],
      }),
    });
  }

  getApplicationFeeCategory(form: ApplicationFeeCategoryFormGroup): IApplicationFeeCategory | NewApplicationFeeCategory {
    return form.getRawValue() as IApplicationFeeCategory | NewApplicationFeeCategory;
  }

  resetForm(form: ApplicationFeeCategoryFormGroup, applicationFeeCategory: ApplicationFeeCategoryFormGroupInput): void {
    const applicationFeeCategoryRawValue = { ...this.getFormDefaults(), ...applicationFeeCategory };
    form.reset(
      {
        ...applicationFeeCategoryRawValue,
        id: { value: applicationFeeCategoryRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ApplicationFeeCategoryFormDefaults {
    return {
      id: null,
    };
  }
}
