import { IProduct, NewProduct } from './product.model';

export const sampleWithRequiredData: IProduct = {
  id: 11737,
  name: 'hm sleepily',
  price: 17510.56,
};

export const sampleWithPartialData: IProduct = {
  id: 14859,
  name: 'weighty log short-term',
  price: 29614.85,
};

export const sampleWithFullData: IProduct = {
  id: 4403,
  name: 'cafe',
  description: 'along amongst',
  price: 12917.62,
};

export const sampleWithNewData: NewProduct = {
  name: 'anti inject why',
  price: 26562.23,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
