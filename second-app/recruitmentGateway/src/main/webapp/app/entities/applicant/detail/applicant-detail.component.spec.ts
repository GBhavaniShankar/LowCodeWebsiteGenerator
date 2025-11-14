import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { ApplicantDetailComponent } from './applicant-detail.component';

describe('Applicant Management Detail Component', () => {
  let comp: ApplicantDetailComponent;
  let fixture: ComponentFixture<ApplicantDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ApplicantDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./applicant-detail.component').then(m => m.ApplicantDetailComponent),
              resolve: { applicant: () => of({ id: 12167 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(ApplicantDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ApplicantDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load applicant on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ApplicantDetailComponent);

      // THEN
      expect(instance.applicant()).toEqual(expect.objectContaining({ id: 12167 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
