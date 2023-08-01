import React from 'react';
import { Product } from '../../model/Product';
import { CardContent, CardMedia } from '@mui/material';
import ActionButton from '../buttons/ActionButton';
import { formatNumberValue } from '../../helpers/utils';

interface Props {
  product: Product;
}

const ProductItem = ({ product }: Props) => (
  <div className="order-gray-200 rounded-lg shadow">
    <CardContent>
      <CardMedia
        sx={{ height: 100, width: 100 }}
        component="img"
        image={product.images[0]}
        alt={product.title}
      />
      <div className="ml-3">
        <p className="text-sm font-medium text-gray-900">{product.title}</p>
        <p className="text-sm text-gray-500">
          {formatNumberValue(product.price)}
        </p>
        <ActionButton product={product}>cart</ActionButton>
      </div>
    </CardContent>
  </div>
);

export default ProductItem;
