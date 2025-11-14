import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IApplicant } from '../applicant.model';
import { ApplicantService } from '../service/applicant.service';

const applicantResolve = (route: ActivatedRouteSnapshot): Observable<null | IApplicant> => {
  const id = route.params.id;
  if (id) {
    return inject(ApplicantService)
      .find(id)
      .pipe(
        mergeMap((applicant: HttpResponse<IApplicant>) => {
          if (applicant.body) {
            return of(applicant.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default applicantResolve;
