export interface IAdvertisement {
  id: number;
  title?: string | null;
  content?: string | null;
}

export type NewAdvertisement = Omit<IAdvertisement, 'id'> & { id: null };
