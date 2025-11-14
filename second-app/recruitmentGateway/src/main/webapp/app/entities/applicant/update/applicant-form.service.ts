import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IApplicant, NewApplicant } from '../applicant.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IApplicant for edit and NewApplicantFormGroupInput for create.
 */
type ApplicantFormGroupInput = IApplicant | PartialWithRequiredKeyOf<NewApplicant>;

type ApplicantFormDefaults = Pick<NewApplicant, 'id' | 'isAccountActivated'>;

type ApplicantFormGroupContent = {
  id: FormControl<IApplicant['id'] | NewApplicant['id']>;
  username: FormControl<IApplicant['username']>;
  email: FormControl<IApplicant['email']>;
  passwordHash: FormControl<IApplicant['passwordHash']>;
  firstName: FormControl<IApplicant['firstName']>;
  lastName: FormControl<IApplicant['lastName']>;
  isAccountActivated: FormControl<IApplicant['isAccountActivated']>;
  authorities: FormControl<IApplicant['authorities']>;
};

export type ApplicantFormGroup = FormGroup<ApplicantFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ApplicantFormService {
  createApplicantFormGroup(applicant: ApplicantFormGroupInput = { id: null }): ApplicantFormGroup {
    const applicantRawValue = {
      ...this.getFormDefaults(),
      ...applicant,
    };
    return new FormGroup<ApplicantFormGroupContent>({
      id: new FormControl(
        { value: applicantRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      username: new FormControl(applicantRawValue.username, {
        validators: [Validators.required, Validators.minLength(5)],
      }),
      email: new FormControl(applicantRawValue.email, {
        validators: [Validators.required, Validators.pattern('^[^@\\s]+@[^@\\s]+.[^@\\s]+$')],
      }),
      passwordHash: new FormControl(applicantRawValue.passwordHash),
      firstName: new FormControl(applicantRawValue.firstName),
      lastName: new FormControl(applicantRawValue.lastName),
      isAccountActivated: new FormControl(applicantRawValue.isAccountActivated),
      authorities: new FormControl(applicantRawValue.authorities),
    });
  }

  getApplicant(form: ApplicantFormGroup): IApplicant | NewApplicant {
    return form.getRawValue() as IApplicant | NewApplicant;
  }

  resetForm(form: ApplicantFormGroup, applicant: ApplicantFormGroupInput): void {
    const applicantRawValue = { ...this.getFormDefaults(), ...applicant };
    form.reset(
      {
        ...applicantRawValue,
        id: { value: applicantRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ApplicantFormDefaults {
    return {
      id: null,
      isAccountActivated: false,
    };
  }
}
