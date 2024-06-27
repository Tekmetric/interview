export interface Book {
    key: string;
    title: string;
    authors: { name: string }[];
    first_publish_year: number;
    description: string;
    cover_i?: number;
  }
  