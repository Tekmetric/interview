import { useEffect, useState } from 'react';
import { DEFAULT_PAGE_SIZE } from '../constants/pagination';
import {
  DEFAULT_SORT_OPTION_ID,
  SORT_OPTIONS,
} from '../config/sortOptions';
import { fetchProducts } from '../api/products/fetchProducts';
import { getCategories } from '../api/products/getCategories';
import type {
  ProductCategory,
  ProductSortField,
  ProductSummary,
  SortOrder,
} from '../types/product';
import { pageToSkip } from '../components/pagination/paginationUtils';
import { scrollToTopRespectingMotion } from '../utils/scrollIntoViewRespectingMotion';

interface BuildProductFetchParams {
  trimmedQuery: string;
  selectedCategorySlug: string | null;
  sortParams: { sortBy?: ProductSortField; order?: SortOrder };
  page: number;
}

function buildProductFetch({
  trimmedQuery,
  selectedCategorySlug,
  sortParams,
  page,
}: BuildProductFetchParams) {
  const paginationParams = {
    limit: DEFAULT_PAGE_SIZE,
    skip: pageToSkip(page),
  };

  if (trimmedQuery) {
    return fetchProducts({
      mode: 'search',
      q: trimmedQuery,
      ...paginationParams,
      ...sortParams,
    });
  }

  if (selectedCategorySlug) {
    return fetchProducts({
      mode: 'category',
      category: selectedCategorySlug,
      ...paginationParams,
      ...sortParams,
    });
  }

  if (sortParams.sortBy && sortParams.order) {
    return fetchProducts({
      mode: 'sorted',
      sortBy: sortParams.sortBy,
      order: sortParams.order,
      ...paginationParams,
    });
  }

  return fetchProducts({ mode: 'default', ...paginationParams });
}

export function useProductListing() {
  const [products, setProducts] = useState<ProductSummary[]>([]);
  const [productTotal, setProductTotal] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [sortOptionId, setSortOptionId] = useState(DEFAULT_SORT_OPTION_ID);
  const [selectedCategorySlug, setSelectedCategorySlug] = useState<string | null>(
    null
  );
  const [selectedProductId, setSelectedProductId] = useState<number | null>(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [categories, setCategories] = useState<ProductCategory[]>([]);
  const [categoriesLoading, setCategoriesLoading] = useState(true);
  const [categoriesError, setCategoriesError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;

    async function loadCategories() {
      setCategoriesLoading(true);

      try {
        const response = await getCategories();

        if (!cancelled) {
          setCategories(response);
          setCategoriesError(null);
        }
      } catch {
        if (!cancelled) {
          setCategoriesError('Failed to load categories.');
        }
      } finally {
        if (!cancelled) {
          setCategoriesLoading(false);
        }
      }
    }

    void loadCategories();

    return () => {
      cancelled = true;
    };
  }, []);

  useEffect(() => {
    let cancelled = false;

    async function loadProducts() {
      setIsLoading(true);

      try {
        const trimmedQuery = searchQuery.trim();
        const sort = SORT_OPTIONS.find((option) => option.id === sortOptionId);
        const sortParams =
          sort?.sortBy && sort?.order
            ? { sortBy: sort.sortBy, order: sort.order }
            : {};

        const response = await buildProductFetch({
          trimmedQuery,
          selectedCategorySlug,
          sortParams,
          page: currentPage,
        });

        if (!cancelled) {
          setProducts(response.products);
          setProductTotal(response.total);
          setError(null);
        }
      } catch {
        if (!cancelled) {
          setError('Failed to load products. Please try again later.');
        }
      } finally {
        if (!cancelled) {
          setIsLoading(false);
        }
      }
    }

    void loadProducts();

    return () => {
      cancelled = true;
    };
  }, [searchQuery, sortOptionId, selectedCategorySlug, currentPage]);

  const handleSearch = (query: string) => {
    setSearchQuery(query);
    setCurrentPage(1);
  };

  const handleSortChange = (id: string) => {
    setSortOptionId(id);
    setCurrentPage(1);
  };

  const handleCategoryChange = (slug: string | null) => {
    setSelectedCategorySlug(slug);
    setCurrentPage(1);
    scrollToTopRespectingMotion();
  };

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
    scrollToTopRespectingMotion();
  };

  const trimmedQuery = searchQuery.trim();
  const selectedCategory = categories.find(
    (category) => category.slug === selectedCategorySlug
  );

  const heading = trimmedQuery
    ? `Search results for "${trimmedQuery}"`
    : selectedCategory
      ? selectedCategory.name
      : 'Products';

  return {
    products,
    productTotal,
    isLoading,
    error,
    currentPage,
    categories,
    categoriesLoading,
    categoriesError,
    selectedCategorySlug,
    selectedProductId,
    trimmedQuery,
    selectedCategory,
    heading,
    handleSearch,
    handleSortChange,
    handleCategoryChange,
    handlePageChange,
    setSelectedProductId,
    sortOptionId,
  };
}
