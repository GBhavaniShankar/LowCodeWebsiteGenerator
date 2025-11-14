import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IApplicant, NewApplicant } from '../applicant.model';

export type PartialUpdateApplicant = Partial<IApplicant> & Pick<IApplicant, 'id'>;

export type EntityResponseType = HttpResponse<IApplicant>;
export type EntityArrayResponseType = HttpResponse<IApplicant[]>;

@Injectable({ providedIn: 'root' })
export class ApplicantService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/applicants', 'recruitmentbackend');

  create(applicant: NewApplicant): Observable<EntityResponseType> {
    return this.http.post<IApplicant>(this.resourceUrl, applicant, { observe: 'response' });
  }

  update(applicant: IApplicant): Observable<EntityResponseType> {
    return this.http.put<IApplicant>(`${this.resourceUrl}/${this.getApplicantIdentifier(applicant)}`, applicant, { observe: 'response' });
  }

  partialUpdate(applicant: PartialUpdateApplicant): Observable<EntityResponseType> {
    return this.http.patch<IApplicant>(`${this.resourceUrl}/${this.getApplicantIdentifier(applicant)}`, applicant, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IApplicant>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IApplicant[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getApplicantIdentifier(applicant: Pick<IApplicant, 'id'>): number {
    return applicant.id;
  }

  compareApplicant(o1: Pick<IApplicant, 'id'> | null, o2: Pick<IApplicant, 'id'> | null): boolean {
    return o1 && o2 ? this.getApplicantIdentifier(o1) === this.getApplicantIdentifier(o2) : o1 === o2;
  }

  addApplicantToCollectionIfMissing<Type extends Pick<IApplicant, 'id'>>(
    applicantCollection: Type[],
    ...applicantsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const applicants: Type[] = applicantsToCheck.filter(isPresent);
    if (applicants.length > 0) {
      const applicantCollectionIdentifiers = applicantCollection.map(applicantItem => this.getApplicantIdentifier(applicantItem));
      const applicantsToAdd = applicants.filter(applicantItem => {
        const applicantIdentifier = this.getApplicantIdentifier(applicantItem);
        if (applicantCollectionIdentifiers.includes(applicantIdentifier)) {
          return false;
        }
        applicantCollectionIdentifiers.push(applicantIdentifier);
        return true;
      });
      return [...applicantsToAdd, ...applicantCollection];
    }
    return applicantCollection;
  }
}
