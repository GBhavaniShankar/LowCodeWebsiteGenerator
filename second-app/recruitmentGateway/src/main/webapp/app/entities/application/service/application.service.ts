import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IApplication, NewApplication } from '../application.model';

export type PartialUpdateApplication = Partial<IApplication> & Pick<IApplication, 'id'>;

type RestOf<T extends IApplication | NewApplication> = Omit<T, 'submissionDate'> & {
  submissionDate?: string | null;
};

export type RestApplication = RestOf<IApplication>;

export type NewRestApplication = RestOf<NewApplication>;

export type PartialUpdateRestApplication = RestOf<PartialUpdateApplication>;

export type EntityResponseType = HttpResponse<IApplication>;
export type EntityArrayResponseType = HttpResponse<IApplication[]>;

@Injectable({ providedIn: 'root' })
export class ApplicationService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/applications', 'recruitmentbackend');

  create(application: NewApplication): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(application);
    return this.http
      .post<RestApplication>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(application: IApplication): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(application);
    return this.http
      .put<RestApplication>(`${this.resourceUrl}/${this.getApplicationIdentifier(application)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(application: PartialUpdateApplication): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(application);
    return this.http
      .patch<RestApplication>(`${this.resourceUrl}/${this.getApplicationIdentifier(application)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestApplication>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestApplication[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getApplicationIdentifier(application: Pick<IApplication, 'id'>): number {
    return application.id;
  }

  compareApplication(o1: Pick<IApplication, 'id'> | null, o2: Pick<IApplication, 'id'> | null): boolean {
    return o1 && o2 ? this.getApplicationIdentifier(o1) === this.getApplicationIdentifier(o2) : o1 === o2;
  }

  addApplicationToCollectionIfMissing<Type extends Pick<IApplication, 'id'>>(
    applicationCollection: Type[],
    ...applicationsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const applications: Type[] = applicationsToCheck.filter(isPresent);
    if (applications.length > 0) {
      const applicationCollectionIdentifiers = applicationCollection.map(applicationItem => this.getApplicationIdentifier(applicationItem));
      const applicationsToAdd = applications.filter(applicationItem => {
        const applicationIdentifier = this.getApplicationIdentifier(applicationItem);
        if (applicationCollectionIdentifiers.includes(applicationIdentifier)) {
          return false;
        }
        applicationCollectionIdentifiers.push(applicationIdentifier);
        return true;
      });
      return [...applicationsToAdd, ...applicationCollection];
    }
    return applicationCollection;
  }

  protected convertDateFromClient<T extends IApplication | NewApplication | PartialUpdateApplication>(application: T): RestOf<T> {
    return {
      ...application,
      submissionDate: application.submissionDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restApplication: RestApplication): IApplication {
    return {
      ...restApplication,
      submissionDate: restApplication.submissionDate ? dayjs(restApplication.submissionDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestApplication>): HttpResponse<IApplication> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestApplication[]>): HttpResponse<IApplication[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
