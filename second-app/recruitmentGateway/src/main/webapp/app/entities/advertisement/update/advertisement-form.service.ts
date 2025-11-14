import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IAdvertisement, NewAdvertisement } from '../advertisement.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAdvertisement for edit and NewAdvertisementFormGroupInput for create.
 */
type AdvertisementFormGroupInput = IAdvertisement | PartialWithRequiredKeyOf<NewAdvertisement>;

type AdvertisementFormDefaults = Pick<NewAdvertisement, 'id'>;

type AdvertisementFormGroupContent = {
  id: FormControl<IAdvertisement['id'] | NewAdvertisement['id']>;
  title: FormControl<IAdvertisement['title']>;
  content: FormControl<IAdvertisement['content']>;
};

export type AdvertisementFormGroup = FormGroup<AdvertisementFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AdvertisementFormService {
  createAdvertisementFormGroup(advertisement: AdvertisementFormGroupInput = { id: null }): AdvertisementFormGroup {
    const advertisementRawValue = {
      ...this.getFormDefaults(),
      ...advertisement,
    };
    return new FormGroup<AdvertisementFormGroupContent>({
      id: new FormControl(
        { value: advertisementRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      title: new FormControl(advertisementRawValue.title, {
        validators: [Validators.required],
      }),
      content: new FormControl(advertisementRawValue.content),
    });
  }

  getAdvertisement(form: AdvertisementFormGroup): IAdvertisement | NewAdvertisement {
    return form.getRawValue() as IAdvertisement | NewAdvertisement;
  }

  resetForm(form: AdvertisementFormGroup, advertisement: AdvertisementFormGroupInput): void {
    const advertisementRawValue = { ...this.getFormDefaults(), ...advertisement };
    form.reset(
      {
        ...advertisementRawValue,
        id: { value: advertisementRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): AdvertisementFormDefaults {
    return {
      id: null,
    };
  }
}
