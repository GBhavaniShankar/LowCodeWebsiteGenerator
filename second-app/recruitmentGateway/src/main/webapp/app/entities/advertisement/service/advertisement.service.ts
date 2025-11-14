import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAdvertisement, NewAdvertisement } from '../advertisement.model';

export type PartialUpdateAdvertisement = Partial<IAdvertisement> & Pick<IAdvertisement, 'id'>;

export type EntityResponseType = HttpResponse<IAdvertisement>;
export type EntityArrayResponseType = HttpResponse<IAdvertisement[]>;

@Injectable({ providedIn: 'root' })
export class AdvertisementService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/advertisements', 'recruitmentbackend');

  create(advertisement: NewAdvertisement): Observable<EntityResponseType> {
    return this.http.post<IAdvertisement>(this.resourceUrl, advertisement, { observe: 'response' });
  }

  update(advertisement: IAdvertisement): Observable<EntityResponseType> {
    return this.http.put<IAdvertisement>(`${this.resourceUrl}/${this.getAdvertisementIdentifier(advertisement)}`, advertisement, {
      observe: 'response',
    });
  }

  partialUpdate(advertisement: PartialUpdateAdvertisement): Observable<EntityResponseType> {
    return this.http.patch<IAdvertisement>(`${this.resourceUrl}/${this.getAdvertisementIdentifier(advertisement)}`, advertisement, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IAdvertisement>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAdvertisement[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getAdvertisementIdentifier(advertisement: Pick<IAdvertisement, 'id'>): number {
    return advertisement.id;
  }

  compareAdvertisement(o1: Pick<IAdvertisement, 'id'> | null, o2: Pick<IAdvertisement, 'id'> | null): boolean {
    return o1 && o2 ? this.getAdvertisementIdentifier(o1) === this.getAdvertisementIdentifier(o2) : o1 === o2;
  }

  addAdvertisementToCollectionIfMissing<Type extends Pick<IAdvertisement, 'id'>>(
    advertisementCollection: Type[],
    ...advertisementsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const advertisements: Type[] = advertisementsToCheck.filter(isPresent);
    if (advertisements.length > 0) {
      const advertisementCollectionIdentifiers = advertisementCollection.map(advertisementItem =>
        this.getAdvertisementIdentifier(advertisementItem),
      );
      const advertisementsToAdd = advertisements.filter(advertisementItem => {
        const advertisementIdentifier = this.getAdvertisementIdentifier(advertisementItem);
        if (advertisementCollectionIdentifiers.includes(advertisementIdentifier)) {
          return false;
        }
        advertisementCollectionIdentifiers.push(advertisementIdentifier);
        return true;
      });
      return [...advertisementsToAdd, ...advertisementCollection];
    }
    return advertisementCollection;
  }
}
