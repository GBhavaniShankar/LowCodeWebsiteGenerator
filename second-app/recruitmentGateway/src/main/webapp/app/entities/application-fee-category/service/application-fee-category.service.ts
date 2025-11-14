import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IApplicationFeeCategory, NewApplicationFeeCategory } from '../application-fee-category.model';

export type PartialUpdateApplicationFeeCategory = Partial<IApplicationFeeCategory> & Pick<IApplicationFeeCategory, 'id'>;

export type EntityResponseType = HttpResponse<IApplicationFeeCategory>;
export type EntityArrayResponseType = HttpResponse<IApplicationFeeCategory[]>;

@Injectable({ providedIn: 'root' })
export class ApplicationFeeCategoryService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/application-fee-categories', 'recruitmentbackend');

  create(applicationFeeCategory: NewApplicationFeeCategory): Observable<EntityResponseType> {
    return this.http.post<IApplicationFeeCategory>(this.resourceUrl, applicationFeeCategory, { observe: 'response' });
  }

  update(applicationFeeCategory: IApplicationFeeCategory): Observable<EntityResponseType> {
    return this.http.put<IApplicationFeeCategory>(
      `${this.resourceUrl}/${this.getApplicationFeeCategoryIdentifier(applicationFeeCategory)}`,
      applicationFeeCategory,
      { observe: 'response' },
    );
  }

  partialUpdate(applicationFeeCategory: PartialUpdateApplicationFeeCategory): Observable<EntityResponseType> {
    return this.http.patch<IApplicationFeeCategory>(
      `${this.resourceUrl}/${this.getApplicationFeeCategoryIdentifier(applicationFeeCategory)}`,
      applicationFeeCategory,
      { observe: 'response' },
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IApplicationFeeCategory>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IApplicationFeeCategory[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getApplicationFeeCategoryIdentifier(applicationFeeCategory: Pick<IApplicationFeeCategory, 'id'>): number {
    return applicationFeeCategory.id;
  }

  compareApplicationFeeCategory(o1: Pick<IApplicationFeeCategory, 'id'> | null, o2: Pick<IApplicationFeeCategory, 'id'> | null): boolean {
    return o1 && o2 ? this.getApplicationFeeCategoryIdentifier(o1) === this.getApplicationFeeCategoryIdentifier(o2) : o1 === o2;
  }

  addApplicationFeeCategoryToCollectionIfMissing<Type extends Pick<IApplicationFeeCategory, 'id'>>(
    applicationFeeCategoryCollection: Type[],
    ...applicationFeeCategoriesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const applicationFeeCategories: Type[] = applicationFeeCategoriesToCheck.filter(isPresent);
    if (applicationFeeCategories.length > 0) {
      const applicationFeeCategoryCollectionIdentifiers = applicationFeeCategoryCollection.map(applicationFeeCategoryItem =>
        this.getApplicationFeeCategoryIdentifier(applicationFeeCategoryItem),
      );
      const applicationFeeCategoriesToAdd = applicationFeeCategories.filter(applicationFeeCategoryItem => {
        const applicationFeeCategoryIdentifier = this.getApplicationFeeCategoryIdentifier(applicationFeeCategoryItem);
        if (applicationFeeCategoryCollectionIdentifiers.includes(applicationFeeCategoryIdentifier)) {
          return false;
        }
        applicationFeeCategoryCollectionIdentifiers.push(applicationFeeCategoryIdentifier);
        return true;
      });
      return [...applicationFeeCategoriesToAdd, ...applicationFeeCategoryCollection];
    }
    return applicationFeeCategoryCollection;
  }
}
