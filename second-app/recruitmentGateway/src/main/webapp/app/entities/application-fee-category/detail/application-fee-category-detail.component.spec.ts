import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { ApplicationFeeCategoryDetailComponent } from './application-fee-category-detail.component';

describe('ApplicationFeeCategory Management Detail Component', () => {
  let comp: ApplicationFeeCategoryDetailComponent;
  let fixture: ComponentFixture<ApplicationFeeCategoryDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ApplicationFeeCategoryDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./application-fee-category-detail.component').then(m => m.ApplicationFeeCategoryDetailComponent),
              resolve: { applicationFeeCategory: () => of({ id: 14679 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(ApplicationFeeCategoryDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ApplicationFeeCategoryDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load applicationFeeCategory on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ApplicationFeeCategoryDetailComponent);

      // THEN
      expect(instance.applicationFeeCategory()).toEqual(expect.objectContaining({ id: 14679 }));
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
