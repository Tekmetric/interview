/* -------------------------------------------------------------------------- */
/*    This file contains the types of the data that the service will return   */
/* -------------------------------------------------------------------------- */

export enum Categories {
  Electronics = 'electronics',
  Jewelery = 'jewelery',
  MensClothing = "men's clothing",
  WomensClothing = "women's clothing",
}

export type Category = `${Categories}`;

export type Product = {
  id: number;
  title: string;
  price: number;
  description: string;
  category: string;
  image: string;
  rating: {
    rate: number;
    count: number;
  };
};
