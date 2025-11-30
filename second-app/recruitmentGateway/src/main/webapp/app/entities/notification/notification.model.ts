import { IApplicant } from 'app/entities/applicant/applicant.model';
import { NotificationSource } from 'app/entities/enumerations/notification-source.model';

export interface INotification {
  id: number;
  title?: string | null;
  message?: string | null;
  isRead?: boolean | null;
  generatedBy?: keyof typeof NotificationSource | null;
  recipient?: Pick<IApplicant, 'id' | 'username'> | null;
}

export type NewNotification = Omit<INotification, 'id'> & { id: null };
