import { IAdvertisement, NewAdvertisement } from './advertisement.model';

export const sampleWithRequiredData: IAdvertisement = {
  id: 23794,
  title: 'whose',
};

export const sampleWithPartialData: IAdvertisement = {
  id: 24095,
  title: 'unethically crushing indeed',
};

export const sampleWithFullData: IAdvertisement = {
  id: 17436,
  title: 'bank beyond',
  content: '../fake-data/blob/hipster.txt',
};

export const sampleWithNewData: NewAdvertisement = {
  title: 'bewail materialise communicate',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
