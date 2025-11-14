import { IApplicant, NewApplicant } from './applicant.model';

export const sampleWithRequiredData: IApplicant = {
  id: 3066,
  username: 'up enhance around',
  email: 'l%f@!S36$4U',
};

export const sampleWithPartialData: IApplicant = {
  id: 21879,
  username: 'loudly',
  email: '9S@M\\=4t,v',
  passwordHash: 'swing which given',
  firstName: 'Enoch',
  authorities: 'oh indeed unlike',
};

export const sampleWithFullData: IApplicant = {
  id: 4451,
  username: 'outside onto',
  email: 'N"%0S[@7;Vqkf3X',
  passwordHash: 'than including information',
  firstName: 'Claudine',
  lastName: 'McGlynn',
  isAccountActivated: true,
  authorities: 'furthermore aha',
};

export const sampleWithNewData: NewApplicant = {
  username: 'powerful ornate amidst',
  email: '}/@v{0X*Bj',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
