import { Book } from '../types';
import { getCache, setCache } from './cache';

const API_URL = 'https://www.googleapis.com/books/v1/volumes';

export const fetchBooks = async (query: string, category: string, page: number) => {
  const limit = 30;
  const startIndex = (page - 1) * limit;
  const apiKey = 'AIzaSyDFrJaEsdYgSv5MaObhEDZGb08YQxB_DUI';
  let searchQuery = `${API_URL}?q=`;

  if (query) {
    // Fetch without caching for search queries
    searchQuery += encodeURIComponent(query);
  } else {
    // Use cache for category results
    const cacheKey = `${category}-${page}`;
    const cachedData = getCache<{ books: Book[], totalBooks: number }>(cacheKey);
    if (cachedData) return cachedData;

    searchQuery += encodeURIComponent(`subject:${category}`);
  }

  searchQuery += `&startIndex=${startIndex}&maxResults=${limit}&key=${apiKey}`;

  const response = await fetch(searchQuery);
  if (!response.ok) throw new Error('Failed to fetch');

  const data = await response.json();
  const books: Book[] = data.items.map((item: any) => ({
    key: item.etag,
    title: item.volumeInfo.title,
    authors: item.volumeInfo.authors || [],
    first_publish_year: item.volumeInfo.publishedDate,
    description: item.volumeInfo.description,
    cover_i: item.volumeInfo.imageLinks?.thumbnail,
    isbn: item.volumeInfo.industryIdentifiers?.map((id: any) => id.identifier),
    publisher: item.volumeInfo.publisher,
    number_of_pages: item.volumeInfo.pageCount,
    subjects: item.volumeInfo.categories || [],
    previewLink: item.volumeInfo.previewLink,
  }));

  const result = { books, totalBooks: data.totalItems };

  if (!query) {
    // Cache the category results
    const cacheKey = `${category}-${page}`;
    setCache(cacheKey, result);
  }

  return result;
};