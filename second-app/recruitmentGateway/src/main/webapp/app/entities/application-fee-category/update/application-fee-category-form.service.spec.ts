import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../application-fee-category.test-samples';

import { ApplicationFeeCategoryFormService } from './application-fee-category-form.service';

describe('ApplicationFeeCategory Form Service', () => {
  let service: ApplicationFeeCategoryFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ApplicationFeeCategoryFormService);
  });

  describe('Service methods', () => {
    describe('createApplicationFeeCategoryFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createApplicationFeeCategoryFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            fee: expect.any(Object),
          }),
        );
      });

      it('passing IApplicationFeeCategory should create a new form with FormGroup', () => {
        const formGroup = service.createApplicationFeeCategoryFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            fee: expect.any(Object),
          }),
        );
      });
    });

    describe('getApplicationFeeCategory', () => {
      it('should return NewApplicationFeeCategory for default ApplicationFeeCategory initial value', () => {
        const formGroup = service.createApplicationFeeCategoryFormGroup(sampleWithNewData);

        const applicationFeeCategory = service.getApplicationFeeCategory(formGroup) as any;

        expect(applicationFeeCategory).toMatchObject(sampleWithNewData);
      });

      it('should return NewApplicationFeeCategory for empty ApplicationFeeCategory initial value', () => {
        const formGroup = service.createApplicationFeeCategoryFormGroup();

        const applicationFeeCategory = service.getApplicationFeeCategory(formGroup) as any;

        expect(applicationFeeCategory).toMatchObject({});
      });

      it('should return IApplicationFeeCategory', () => {
        const formGroup = service.createApplicationFeeCategoryFormGroup(sampleWithRequiredData);

        const applicationFeeCategory = service.getApplicationFeeCategory(formGroup) as any;

        expect(applicationFeeCategory).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IApplicationFeeCategory should not enable id FormControl', () => {
        const formGroup = service.createApplicationFeeCategoryFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewApplicationFeeCategory should disable id FormControl', () => {
        const formGroup = service.createApplicationFeeCategoryFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
