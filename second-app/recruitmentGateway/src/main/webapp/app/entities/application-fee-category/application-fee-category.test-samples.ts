import { IApplicationFeeCategory, NewApplicationFeeCategory } from './application-fee-category.model';

export const sampleWithRequiredData: IApplicationFeeCategory = {
  id: 27920,
  name: 'black',
  fee: 14641.96,
};

export const sampleWithPartialData: IApplicationFeeCategory = {
  id: 1260,
  name: 'kaleidoscopic aside',
  fee: 9759.29,
};

export const sampleWithFullData: IApplicationFeeCategory = {
  id: 22906,
  name: 'bloom',
  fee: 4218.47,
};

export const sampleWithNewData: NewApplicationFeeCategory = {
  name: 'lavish geez',
  fee: 15027.45,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
