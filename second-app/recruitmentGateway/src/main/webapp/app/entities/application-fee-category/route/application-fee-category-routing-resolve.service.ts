import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IApplicationFeeCategory } from '../application-fee-category.model';
import { ApplicationFeeCategoryService } from '../service/application-fee-category.service';

const applicationFeeCategoryResolve = (route: ActivatedRouteSnapshot): Observable<null | IApplicationFeeCategory> => {
  const id = route.params.id;
  if (id) {
    return inject(ApplicationFeeCategoryService)
      .find(id)
      .pipe(
        mergeMap((applicationFeeCategory: HttpResponse<IApplicationFeeCategory>) => {
          if (applicationFeeCategory.body) {
            return of(applicationFeeCategory.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default applicationFeeCategoryResolve;
