import { useParams } from 'react-router';

import ProductNotFound from '../../components/ProductDetail/ProductNotFound.tsx';
import ProductSkeleton from '../../components/ProductDetail/ProductSkeleton.tsx';
import ProductDetail from '../../components/ProductDetail/ProductDetail.tsx';
import useProduct from '../../hooks/useProduct.tsx';

const ProductPage = () => {
  const { id } = useParams();
  const { product, error, isLoading } = useProduct(id);

  if (isLoading) return <ProductSkeleton />;
  if (error || !product) return <ProductNotFound />;

  return <ProductDetail product={product} />;
};

export default ProductPage;
