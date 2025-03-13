import { useQuery } from '@tanstack/react-query';
import { useParams } from 'react-router';
import { getProduct } from '../../api/product.ts';
import ProductDetail from '../../components/ProductDetail/ProductDetail.tsx';
import ProductNotFound from '../../components/ProductDetail/ProductNotFound.tsx';
import ProductSkeleton from '../../components/ProductDetail/ProductSkeleton.tsx';

const ProductPage = () => {
  const { id } = useParams();
  const {
    data: product,
    isLoading,
    error,
  } = useQuery({
    queryKey: ['product', id],
    queryFn: () => getProduct(id!),
    staleTime: 1000 * 60 * 5, // 5 minutes
    enabled: !!id,
    retry: 1,
  });

  if (isLoading) return <ProductSkeleton />;
  if (error || !product) return <ProductNotFound />;

  return <ProductDetail product={product} />;
};

export default ProductPage;
