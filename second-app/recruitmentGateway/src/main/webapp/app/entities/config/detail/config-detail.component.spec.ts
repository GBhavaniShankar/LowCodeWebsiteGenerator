import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { ConfigDetailComponent } from './config-detail.component';

describe('Config Management Detail Component', () => {
  let comp: ConfigDetailComponent;
  let fixture: ComponentFixture<ConfigDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfigDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./config-detail.component').then(m => m.ConfigDetailComponent),
              resolve: { config: () => of({ id: 11663 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(ConfigDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfigDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load config on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ConfigDetailComponent);

      // THEN
      expect(instance.config()).toEqual(expect.objectContaining({ id: 11663 }));
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
