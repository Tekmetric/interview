export interface Book {
    isbn: any;
    publisher: any;
    number_of_pages: any;
    subjects: any;
    key: string;
    title: string;
    authors: { name: string }[];
    first_publish_year: number;
    description: string;
    cover_i?: number;
  }
  