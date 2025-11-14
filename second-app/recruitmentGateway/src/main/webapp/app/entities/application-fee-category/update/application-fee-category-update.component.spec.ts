import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ApplicationFeeCategoryService } from '../service/application-fee-category.service';
import { IApplicationFeeCategory } from '../application-fee-category.model';
import { ApplicationFeeCategoryFormService } from './application-fee-category-form.service';

import { ApplicationFeeCategoryUpdateComponent } from './application-fee-category-update.component';

describe('ApplicationFeeCategory Management Update Component', () => {
  let comp: ApplicationFeeCategoryUpdateComponent;
  let fixture: ComponentFixture<ApplicationFeeCategoryUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let applicationFeeCategoryFormService: ApplicationFeeCategoryFormService;
  let applicationFeeCategoryService: ApplicationFeeCategoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ApplicationFeeCategoryUpdateComponent],
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
      .overrideTemplate(ApplicationFeeCategoryUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ApplicationFeeCategoryUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    applicationFeeCategoryFormService = TestBed.inject(ApplicationFeeCategoryFormService);
    applicationFeeCategoryService = TestBed.inject(ApplicationFeeCategoryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const applicationFeeCategory: IApplicationFeeCategory = { id: 16364 };

      activatedRoute.data = of({ applicationFeeCategory });
      comp.ngOnInit();

      expect(comp.applicationFeeCategory).toEqual(applicationFeeCategory);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IApplicationFeeCategory>>();
      const applicationFeeCategory = { id: 14679 };
      jest.spyOn(applicationFeeCategoryFormService, 'getApplicationFeeCategory').mockReturnValue(applicationFeeCategory);
      jest.spyOn(applicationFeeCategoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ applicationFeeCategory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: applicationFeeCategory }));
      saveSubject.complete();

      // THEN
      expect(applicationFeeCategoryFormService.getApplicationFeeCategory).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(applicationFeeCategoryService.update).toHaveBeenCalledWith(expect.objectContaining(applicationFeeCategory));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IApplicationFeeCategory>>();
      const applicationFeeCategory = { id: 14679 };
      jest.spyOn(applicationFeeCategoryFormService, 'getApplicationFeeCategory').mockReturnValue({ id: null });
      jest.spyOn(applicationFeeCategoryService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ applicationFeeCategory: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: applicationFeeCategory }));
      saveSubject.complete();

      // THEN
      expect(applicationFeeCategoryFormService.getApplicationFeeCategory).toHaveBeenCalled();
      expect(applicationFeeCategoryService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IApplicationFeeCategory>>();
      const applicationFeeCategory = { id: 14679 };
      jest.spyOn(applicationFeeCategoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ applicationFeeCategory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(applicationFeeCategoryService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
