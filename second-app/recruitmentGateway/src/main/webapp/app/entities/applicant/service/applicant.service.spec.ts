import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IApplicant } from '../applicant.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../applicant.test-samples';

import { ApplicantService } from './applicant.service';

const requireRestSample: IApplicant = {
  ...sampleWithRequiredData,
};

describe('Applicant Service', () => {
  let service: ApplicantService;
  let httpMock: HttpTestingController;
  let expectedResult: IApplicant | IApplicant[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ApplicantService);
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

    it('should create a Applicant', () => {
      const applicant = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(applicant).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Applicant', () => {
      const applicant = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(applicant).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Applicant', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Applicant', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Applicant', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addApplicantToCollectionIfMissing', () => {
      it('should add a Applicant to an empty array', () => {
        const applicant: IApplicant = sampleWithRequiredData;
        expectedResult = service.addApplicantToCollectionIfMissing([], applicant);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(applicant);
      });

      it('should not add a Applicant to an array that contains it', () => {
        const applicant: IApplicant = sampleWithRequiredData;
        const applicantCollection: IApplicant[] = [
          {
            ...applicant,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addApplicantToCollectionIfMissing(applicantCollection, applicant);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Applicant to an array that doesn't contain it", () => {
        const applicant: IApplicant = sampleWithRequiredData;
        const applicantCollection: IApplicant[] = [sampleWithPartialData];
        expectedResult = service.addApplicantToCollectionIfMissing(applicantCollection, applicant);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(applicant);
      });

      it('should add only unique Applicant to an array', () => {
        const applicantArray: IApplicant[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const applicantCollection: IApplicant[] = [sampleWithRequiredData];
        expectedResult = service.addApplicantToCollectionIfMissing(applicantCollection, ...applicantArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const applicant: IApplicant = sampleWithRequiredData;
        const applicant2: IApplicant = sampleWithPartialData;
        expectedResult = service.addApplicantToCollectionIfMissing([], applicant, applicant2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(applicant);
        expect(expectedResult).toContain(applicant2);
      });

      it('should accept null and undefined values', () => {
        const applicant: IApplicant = sampleWithRequiredData;
        expectedResult = service.addApplicantToCollectionIfMissing([], null, applicant, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(applicant);
      });

      it('should return initial array if no Applicant is added', () => {
        const applicantCollection: IApplicant[] = [sampleWithRequiredData];
        expectedResult = service.addApplicantToCollectionIfMissing(applicantCollection, undefined, null);
        expect(expectedResult).toEqual(applicantCollection);
      });
    });

    describe('compareApplicant', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareApplicant(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 12167 };
        const entity2 = null;

        const compareResult1 = service.compareApplicant(entity1, entity2);
        const compareResult2 = service.compareApplicant(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 12167 };
        const entity2 = { id: 10883 };

        const compareResult1 = service.compareApplicant(entity1, entity2);
        const compareResult2 = service.compareApplicant(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 12167 };
        const entity2 = { id: 12167 };

        const compareResult1 = service.compareApplicant(entity1, entity2);
        const compareResult2 = service.compareApplicant(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
