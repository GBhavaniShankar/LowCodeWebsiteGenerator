export interface IApplicationFeeCategory {
  id: number;
  name?: string | null;
  fee?: number | null;
}

export type NewApplicationFeeCategory = Omit<IApplicationFeeCategory, 'id'> & { id: null };
