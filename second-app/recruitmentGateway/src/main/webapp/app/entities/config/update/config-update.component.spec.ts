import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ConfigService } from '../service/config.service';
import { IConfig } from '../config.model';
import { ConfigFormService } from './config-form.service';

import { ConfigUpdateComponent } from './config-update.component';

describe('Config Management Update Component', () => {
  let comp: ConfigUpdateComponent;
  let fixture: ComponentFixture<ConfigUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let configFormService: ConfigFormService;
  let configService: ConfigService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ConfigUpdateComponent],
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
      .overrideTemplate(ConfigUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ConfigUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    configFormService = TestBed.inject(ConfigFormService);
    configService = TestBed.inject(ConfigService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const config: IConfig = { id: 9038 };

      activatedRoute.data = of({ config });
      comp.ngOnInit();

      expect(comp.config).toEqual(config);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IConfig>>();
      const config = { id: 11663 };
      jest.spyOn(configFormService, 'getConfig').mockReturnValue(config);
      jest.spyOn(configService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ config });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: config }));
      saveSubject.complete();

      // THEN
      expect(configFormService.getConfig).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(configService.update).toHaveBeenCalledWith(expect.objectContaining(config));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IConfig>>();
      const config = { id: 11663 };
      jest.spyOn(configFormService, 'getConfig').mockReturnValue({ id: null });
      jest.spyOn(configService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ config: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: config }));
      saveSubject.complete();

      // THEN
      expect(configFormService.getConfig).toHaveBeenCalled();
      expect(configService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IConfig>>();
      const config = { id: 11663 };
      jest.spyOn(configService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ config });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(configService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
