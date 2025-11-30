import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IApplicant } from 'app/entities/applicant/applicant.model';
import { ApplicantService } from 'app/entities/applicant/service/applicant.service';
import { IApplicationFeeCategory } from 'app/entities/application-fee-category/application-fee-category.model';
import { ApplicationFeeCategoryService } from 'app/entities/application-fee-category/service/application-fee-category.service';
import { IApplication } from '../application.model';
import { ApplicationService } from '../service/application.service';
import { ApplicationFormService } from './application-form.service';

import { ApplicationUpdateComponent } from './application-update.component';

describe('Application Management Update Component', () => {
  let comp: ApplicationUpdateComponent;
  let fixture: ComponentFixture<ApplicationUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let applicationFormService: ApplicationFormService;
  let applicationService: ApplicationService;
  let applicantService: ApplicantService;
  let applicationFeeCategoryService: ApplicationFeeCategoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ApplicationUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ApplicationUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ApplicationUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    applicationFormService = TestBed.inject(ApplicationFormService);
    applicationService = TestBed.inject(ApplicationService);
    applicantService = TestBed.inject(ApplicantService);
    applicationFeeCategoryService = TestBed.inject(ApplicationFeeCategoryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Applicant query and add missing value', () => {
      const application: IApplication = { id: 27535 };
      const applicant: IApplicant = { id: 12167 };
      application.applicant = applicant;

      const applicantCollection: IApplicant[] = [{ id: 12167 }];
      jest.spyOn(applicantService, 'query').mockReturnValue(of(new HttpResponse({ body: applicantCollection })));
      const additionalApplicants = [applicant];
      const expectedCollection: IApplicant[] = [...additionalApplicants, ...applicantCollection];
      jest.spyOn(applicantService, 'addApplicantToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ application });
      comp.ngOnInit();

      expect(applicantService.query).toHaveBeenCalled();
      expect(applicantService.addApplicantToCollectionIfMissing).toHaveBeenCalledWith(
        applicantCollection,
        ...additionalApplicants.map(expect.objectContaining),
      );
      expect(comp.applicantsSharedCollection).toEqual(expectedCollection);
    });

    it('should call ApplicationFeeCategory query and add missing value', () => {
      const application: IApplication = { id: 27535 };
      const feeCategory: IApplicationFeeCategory = { id: 14679 };
      application.feeCategory = feeCategory;

      const applicationFeeCategoryCollection: IApplicationFeeCategory[] = [{ id: 14679 }];
      jest.spyOn(applicationFeeCategoryService, 'query').mockReturnValue(of(new HttpResponse({ body: applicationFeeCategoryCollection })));
      const additionalApplicationFeeCategories = [feeCategory];
      const expectedCollection: IApplicationFeeCategory[] = [...additionalApplicationFeeCategories, ...applicationFeeCategoryCollection];
      jest.spyOn(applicationFeeCategoryService, 'addApplicationFeeCategoryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ application });
      comp.ngOnInit();

      expect(applicationFeeCategoryService.query).toHaveBeenCalled();
      expect(applicationFeeCategoryService.addApplicationFeeCategoryToCollectionIfMissing).toHaveBeenCalledWith(
        applicationFeeCategoryCollection,
        ...additionalApplicationFeeCategories.map(expect.objectContaining),
      );
      expect(comp.applicationFeeCategoriesSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const application: IApplication = { id: 27535 };
      const applicant: IApplicant = { id: 12167 };
      application.applicant = applicant;
      const feeCategory: IApplicationFeeCategory = { id: 14679 };
      application.feeCategory = feeCategory;

      activatedRoute.data = of({ application });
      comp.ngOnInit();

      expect(comp.applicantsSharedCollection).toContainEqual(applicant);
      expect(comp.applicationFeeCategoriesSharedCollection).toContainEqual(feeCategory);
      expect(comp.application).toEqual(application);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IApplication>>();
      const application = { id: 8867 };
      jest.spyOn(applicationFormService, 'getApplication').mockReturnValue(application);
      jest.spyOn(applicationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ application });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: application }));
      saveSubject.complete();

      // THEN
      expect(applicationFormService.getApplication).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(applicationService.update).toHaveBeenCalledWith(expect.objectContaining(application));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IApplication>>();
      const application = { id: 8867 };
      jest.spyOn(applicationFormService, 'getApplication').mockReturnValue({ id: null });
      jest.spyOn(applicationService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ application: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: application }));
      saveSubject.complete();

      // THEN
      expect(applicationFormService.getApplication).toHaveBeenCalled();
      expect(applicationService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IApplication>>();
      const application = { id: 8867 };
      jest.spyOn(applicationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ application });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(applicationService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareApplicant', () => {
      it('should forward to applicantService', () => {
        const entity = { id: 12167 };
        const entity2 = { id: 10883 };
        jest.spyOn(applicantService, 'compareApplicant');
        comp.compareApplicant(entity, entity2);
        expect(applicantService.compareApplicant).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareApplicationFeeCategory', () => {
      it('should forward to applicationFeeCategoryService', () => {
        const entity = { id: 14679 };
        const entity2 = { id: 16364 };
        jest.spyOn(applicationFeeCategoryService, 'compareApplicationFeeCategory');
        comp.compareApplicationFeeCategory(entity, entity2);
        expect(applicationFeeCategoryService.compareApplicationFeeCategory).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
