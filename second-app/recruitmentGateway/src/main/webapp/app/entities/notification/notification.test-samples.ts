import { INotification, NewNotification } from './notification.model';

export const sampleWithRequiredData: INotification = {
  id: 10110,
  title: 'reclassify makeover',
  generatedBy: 'ADMIN',
};

export const sampleWithPartialData: INotification = {
  id: 1745,
  title: 'during ouch',
  generatedBy: 'SYSTEM',
};

export const sampleWithFullData: INotification = {
  id: 5787,
  title: 'some extra-large',
  message: '../fake-data/blob/hipster.txt',
  isRead: true,
  generatedBy: 'ADMIN',
};

export const sampleWithNewData: NewNotification = {
  title: 'consequently voluntarily ew',
  generatedBy: 'SYSTEM',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
