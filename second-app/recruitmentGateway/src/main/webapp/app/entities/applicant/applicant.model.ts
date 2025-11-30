export interface IApplicant {
  id: number;
  username?: string | null;
  email?: string | null;
  passwordHash?: string | null;
  firstName?: string | null;
  lastName?: string | null;
  isAccountActivated?: boolean | null;
  authorities?: string | null;
}

export type NewApplicant = Omit<IApplicant, 'id'> & { id: null };
