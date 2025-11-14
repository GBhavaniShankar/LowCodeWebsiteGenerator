import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IApplicationFeeCategory } from '../application-fee-category.model';
import {
  sampleWithFullData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithRequiredData,
} from '../application-fee-category.test-samples';

import { ApplicationFeeCategoryService } from './application-fee-category.service';

const requireRestSample: IApplicationFeeCategory = {
  ...sampleWithRequiredData,
};

describe('ApplicationFeeCategory Service', () => {
  let service: ApplicationFeeCategoryService;
  let httpMock: HttpTestingController;
  let expectedResult: IApplicationFeeCategory | IApplicationFeeCategory[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ApplicationFeeCategoryService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a ApplicationFeeCategory', () => {
      const applicationFeeCategory = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(applicationFeeCategory).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ApplicationFeeCategory', () => {
      const applicationFeeCategory = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(applicationFeeCategory).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ApplicationFeeCategory', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ApplicationFeeCategory', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ApplicationFeeCategory', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addApplicationFeeCategoryToCollectionIfMissing', () => {
      it('should add a ApplicationFeeCategory to an empty array', () => {
        const applicationFeeCategory: IApplicationFeeCategory = sampleWithRequiredData;
        expectedResult = service.addApplicationFeeCategoryToCollectionIfMissing([], applicationFeeCategory);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(applicationFeeCategory);
      });

      it('should not add a ApplicationFeeCategory to an array that contains it', () => {
        const applicationFeeCategory: IApplicationFeeCategory = sampleWithRequiredData;
        const applicationFeeCategoryCollection: IApplicationFeeCategory[] = [
          {
            ...applicationFeeCategory,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addApplicationFeeCategoryToCollectionIfMissing(applicationFeeCategoryCollection, applicationFeeCategory);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ApplicationFeeCategory to an array that doesn't contain it", () => {
        const applicationFeeCategory: IApplicationFeeCategory = sampleWithRequiredData;
        const applicationFeeCategoryCollection: IApplicationFeeCategory[] = [sampleWithPartialData];
        expectedResult = service.addApplicationFeeCategoryToCollectionIfMissing(applicationFeeCategoryCollection, applicationFeeCategory);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(applicationFeeCategory);
      });

      it('should add only unique ApplicationFeeCategory to an array', () => {
        const applicationFeeCategoryArray: IApplicationFeeCategory[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const applicationFeeCategoryCollection: IApplicationFeeCategory[] = [sampleWithRequiredData];
        expectedResult = service.addApplicationFeeCategoryToCollectionIfMissing(
          applicationFeeCategoryCollection,
          ...applicationFeeCategoryArray,
        );
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const applicationFeeCategory: IApplicationFeeCategory = sampleWithRequiredData;
        const applicationFeeCategory2: IApplicationFeeCategory = sampleWithPartialData;
        expectedResult = service.addApplicationFeeCategoryToCollectionIfMissing([], applicationFeeCategory, applicationFeeCategory2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(applicationFeeCategory);
        expect(expectedResult).toContain(applicationFeeCategory2);
      });

      it('should accept null and undefined values', () => {
        const applicationFeeCategory: IApplicationFeeCategory = sampleWithRequiredData;
        expectedResult = service.addApplicationFeeCategoryToCollectionIfMissing([], null, applicationFeeCategory, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(applicationFeeCategory);
      });

      it('should return initial array if no ApplicationFeeCategory is added', () => {
        const applicationFeeCategoryCollection: IApplicationFeeCategory[] = [sampleWithRequiredData];
        expectedResult = service.addApplicationFeeCategoryToCollectionIfMissing(applicationFeeCategoryCollection, undefined, null);
        expect(expectedResult).toEqual(applicationFeeCategoryCollection);
      });
    });

    describe('compareApplicationFeeCategory', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareApplicationFeeCategory(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 14679 };
        const entity2 = null;

        const compareResult1 = service.compareApplicationFeeCategory(entity1, entity2);
        const compareResult2 = service.compareApplicationFeeCategory(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 14679 };
        const entity2 = { id: 16364 };

        const compareResult1 = service.compareApplicationFeeCategory(entity1, entity2);
        const compareResult2 = service.compareApplicationFeeCategory(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 14679 };
        const entity2 = { id: 14679 };

        const compareResult1 = service.compareApplicationFeeCategory(entity1, entity2);
        const compareResult2 = service.compareApplicationFeeCategory(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
