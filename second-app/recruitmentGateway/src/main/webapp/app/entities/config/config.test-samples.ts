import dayjs from 'dayjs/esm';

import { IConfig, NewConfig } from './config.model';

export const sampleWithRequiredData: IConfig = {
  id: 16644,
};

export const sampleWithPartialData: IConfig = {
  id: 4318,
  portalActive: false,
  endDate: dayjs('2025-11-12T06:51'),
};

export const sampleWithFullData: IConfig = {
  id: 8405,
  portalActive: true,
  startDate: dayjs('2025-11-11T15:25'),
  endDate: dayjs('2025-11-12T05:40'),
  sampleFormUrl: 'leading oof potable',
};

export const sampleWithNewData: NewConfig = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
