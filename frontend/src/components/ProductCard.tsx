import React from "react";
import { Card, CardContent, CardMedia, Typography, Chip, Rating } from "@mui/material";
import { useProductStore } from "../store/useProductStore";
import type { Product } from "../types/Product";

type Props = { product: Product };

export default React.memo(function ProductCard({ product }: Props) {
  const handleClick = () => {
    useProductStore.getState().selectProduct(product);
  };

  return (
    <Card
      onClick={handleClick}
      className="cursor-pointer shadow-lg rounded-2xl border border-gray-100 hover:shadow-xl transition-all duration-300"
    >
      <CardMedia
        component="img"
        image={product.thumbnail}
        alt={product.title}
        loading="lazy"
        className="h-48 object-cover rounded-t-2xl"
      />
      <CardContent className="p-4 space-y-2">
        <div className="flex justify-between items-center">
          <Typography variant="subtitle2" className="text-gray-500">
            {product.category}
          </Typography>
          <Chip label={product.stock > 0 ? "In Stock" : "Out"} color={product.stock > 0 ? "success" : "error"} size="small" />
        </div>

        <Typography variant="h6" className="font-semibold text-gray-800 truncate">
          {product.title}
        </Typography>

        <Typography variant="body2" className="text-gray-500 line-clamp-2">
          {product.description}
        </Typography>

        <div className="flex items-center justify-between pt-2">
          <Typography variant="body1" className="font-bold text-blue-600">
            ${product.price.toFixed(2)}
          </Typography>
          <Rating value={product.rating} precision={0.1} readOnly size="small" />
        </div>

        {product.brand && (
          <Typography variant="caption" className="text-gray-400">
            {product.brand}
          </Typography>
        )}
      </CardContent>
    </Card>
  );
});
