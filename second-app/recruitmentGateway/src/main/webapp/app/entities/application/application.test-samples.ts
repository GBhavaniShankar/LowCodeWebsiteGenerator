import dayjs from 'dayjs/esm';

import { IApplication, NewApplication } from './application.model';

export const sampleWithRequiredData: IApplication = {
  id: 15800,
  uniqueNumber: 'hm',
  status: 'SAVED',
};

export const sampleWithPartialData: IApplication = {
  id: 16062,
  uniqueNumber: 'speedily',
  status: 'WITHDRAWN',
};

export const sampleWithFullData: IApplication = {
  id: 12884,
  uniqueNumber: 'hmph intrigue',
  submissionDate: dayjs('2025-11-11T23:59'),
  status: 'SUBMITTED',
  paymentSuccessful: false,
};

export const sampleWithNewData: NewApplication = {
  uniqueNumber: 'where fun',
  status: 'SAVED',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
