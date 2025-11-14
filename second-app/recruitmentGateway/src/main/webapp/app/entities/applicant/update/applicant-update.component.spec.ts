import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ApplicantService } from '../service/applicant.service';
import { IApplicant } from '../applicant.model';
import { ApplicantFormService } from './applicant-form.service';

import { ApplicantUpdateComponent } from './applicant-update.component';

describe('Applicant Management Update Component', () => {
  let comp: ApplicantUpdateComponent;
  let fixture: ComponentFixture<ApplicantUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let applicantFormService: ApplicantFormService;
  let applicantService: ApplicantService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ApplicantUpdateComponent],
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
      .overrideTemplate(ApplicantUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ApplicantUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    applicantFormService = TestBed.inject(ApplicantFormService);
    applicantService = TestBed.inject(ApplicantService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const applicant: IApplicant = { id: 10883 };

      activatedRoute.data = of({ applicant });
      comp.ngOnInit();

      expect(comp.applicant).toEqual(applicant);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IApplicant>>();
      const applicant = { id: 12167 };
      jest.spyOn(applicantFormService, 'getApplicant').mockReturnValue(applicant);
      jest.spyOn(applicantService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ applicant });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: applicant }));
      saveSubject.complete();

      // THEN
      expect(applicantFormService.getApplicant).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(applicantService.update).toHaveBeenCalledWith(expect.objectContaining(applicant));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IApplicant>>();
      const applicant = { id: 12167 };
      jest.spyOn(applicantFormService, 'getApplicant').mockReturnValue({ id: null });
      jest.spyOn(applicantService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ applicant: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: applicant }));
      saveSubject.complete();

      // THEN
      expect(applicantFormService.getApplicant).toHaveBeenCalled();
      expect(applicantService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IApplicant>>();
      const applicant = { id: 12167 };
      jest.spyOn(applicantService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ applicant });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(applicantService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
