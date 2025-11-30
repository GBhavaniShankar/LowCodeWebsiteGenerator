import dayjs from 'dayjs/esm';

export interface IConfig {
  id: number;
  portalActive?: boolean | null;
  startDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
  sampleFormUrl?: string | null;
}

export type NewConfig = Omit<IConfig, 'id'> & { id: null };
