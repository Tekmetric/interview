export interface Book {
    previewLink: string | undefined;
    key: string;
    title: string;
    authors: string[];
    first_publish_year: string;
    description?: string;
    cover_i?: string;
    isbn?: string[];
    publisher?: string;
    number_of_pages?: number;
    subjects?: string[];
  }
  