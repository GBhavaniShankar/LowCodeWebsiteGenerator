import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IConfig } from '../config.model';
import { ConfigService } from '../service/config.service';

const configResolve = (route: ActivatedRouteSnapshot): Observable<null | IConfig> => {
  const id = route.params.id;
  if (id) {
    return inject(ConfigService)
      .find(id)
      .pipe(
        mergeMap((config: HttpResponse<IConfig>) => {
          if (config.body) {
            return of(config.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default configResolve;
