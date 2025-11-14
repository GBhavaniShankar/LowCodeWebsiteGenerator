import dayjs from 'dayjs/esm';
import { IApplicant } from 'app/entities/applicant/applicant.model';
import { IApplicationFeeCategory } from 'app/entities/application-fee-category/application-fee-category.model';
import { ApplicationStatus } from 'app/entities/enumerations/application-status.model';

export interface IApplication {
  id: number;
  uniqueNumber?: string | null;
  submissionDate?: dayjs.Dayjs | null;
  status?: keyof typeof ApplicationStatus | null;
  paymentSuccessful?: boolean | null;
  applicant?: Pick<IApplicant, 'id' | 'username'> | null;
  feeCategory?: Pick<IApplicationFeeCategory, 'id'> | null;
}

export type NewApplication = Omit<IApplication, 'id'> & { id: null };
