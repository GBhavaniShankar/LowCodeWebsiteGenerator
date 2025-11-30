import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IConfig, NewConfig } from '../config.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IConfig for edit and NewConfigFormGroupInput for create.
 */
type ConfigFormGroupInput = IConfig | PartialWithRequiredKeyOf<NewConfig>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IConfig | NewConfig> = Omit<T, 'startDate' | 'endDate'> & {
  startDate?: string | null;
  endDate?: string | null;
};

type ConfigFormRawValue = FormValueOf<IConfig>;

type NewConfigFormRawValue = FormValueOf<NewConfig>;

type ConfigFormDefaults = Pick<NewConfig, 'id' | 'portalActive' | 'startDate' | 'endDate'>;

type ConfigFormGroupContent = {
  id: FormControl<ConfigFormRawValue['id'] | NewConfig['id']>;
  portalActive: FormControl<ConfigFormRawValue['portalActive']>;
  startDate: FormControl<ConfigFormRawValue['startDate']>;
  endDate: FormControl<ConfigFormRawValue['endDate']>;
  sampleFormUrl: FormControl<ConfigFormRawValue['sampleFormUrl']>;
};

export type ConfigFormGroup = FormGroup<ConfigFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ConfigFormService {
  createConfigFormGroup(config: ConfigFormGroupInput = { id: null }): ConfigFormGroup {
    const configRawValue = this.convertConfigToConfigRawValue({
      ...this.getFormDefaults(),
      ...config,
    });
    return new FormGroup<ConfigFormGroupContent>({
      id: new FormControl(
        { value: configRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      portalActive: new FormControl(configRawValue.portalActive),
      startDate: new FormControl(configRawValue.startDate),
      endDate: new FormControl(configRawValue.endDate),
      sampleFormUrl: new FormControl(configRawValue.sampleFormUrl),
    });
  }

  getConfig(form: ConfigFormGroup): IConfig | NewConfig {
    return this.convertConfigRawValueToConfig(form.getRawValue() as ConfigFormRawValue | NewConfigFormRawValue);
  }

  resetForm(form: ConfigFormGroup, config: ConfigFormGroupInput): void {
    const configRawValue = this.convertConfigToConfigRawValue({ ...this.getFormDefaults(), ...config });
    form.reset(
      {
        ...configRawValue,
        id: { value: configRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ConfigFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      portalActive: false,
      startDate: currentTime,
      endDate: currentTime,
    };
  }

  private convertConfigRawValueToConfig(rawConfig: ConfigFormRawValue | NewConfigFormRawValue): IConfig | NewConfig {
    return {
      ...rawConfig,
      startDate: dayjs(rawConfig.startDate, DATE_TIME_FORMAT),
      endDate: dayjs(rawConfig.endDate, DATE_TIME_FORMAT),
    };
  }

  private convertConfigToConfigRawValue(
    config: IConfig | (Partial<NewConfig> & ConfigFormDefaults),
  ): ConfigFormRawValue | PartialWithRequiredKeyOf<NewConfigFormRawValue> {
    return {
      ...config,
      startDate: config.startDate ? config.startDate.format(DATE_TIME_FORMAT) : undefined,
      endDate: config.endDate ? config.endDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
