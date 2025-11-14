export interface IProduct {
  id: number;
  name?: string | null;
  description?: string | null;
  price?: number | null;
}

export type NewProduct = Omit<IProduct, 'id'> & { id: null };
