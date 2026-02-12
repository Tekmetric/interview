import {
  Drawer,
  Box,
  Typography,
  Divider,
  IconButton,
  Rating,
  Chip,
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import type { Product } from "../types/Product";

type ProductDrawerProps = {
  open: boolean;
  onClose: () => void;
  product: Product | null;
};
const drawerWidth = 360;
export default function ProductDrawer({ open, onClose, product }: ProductDrawerProps) {
  if (!product) return null;

  return (
    <Drawer anchor="left" open={open} onClose={onClose}>
      <Box
        sx={{ width: drawerWidth }}
        className="flex flex-col h-full bg-white overflow-y-auto"
      >
        <Box className="flex justify-between items-center p-4 border-b">
          <Typography variant="h6" className="font-semibold text-gray-800">
            {product.title}
          </Typography>
          <IconButton onClick={onClose}>
            <CloseIcon />
          </IconButton>
        </Box>
        <img
          src={product.thumbnail}
          alt={product.title}
          className="w-full h-64 object-cover"
        />
        <Box className="p-4 space-y-3">
          <div className="flex justify-between items-center">
            <Typography variant="h6" className="text-blue-600 font-bold">
              ${product.price.toFixed(2)}
            </Typography>
            <Rating value={product.rating} precision={0.1} readOnly size="small" />
          </div>

          <Chip
            label={product.category}
            size="small"
            color="primary"
            className="uppercase"
          />

          <Typography variant="body2" className="text-gray-600">
            {product.description}
          </Typography>

          <Divider />

          <Box className="pt-2">
            <Typography variant="caption" className="text-gray-400">
              Brand:
            </Typography>
            <Typography variant="body2">{product.brand}</Typography>

            <Typography variant="caption" className="text-gray-400 mt-2 block">
              Stock:
            </Typography>
            <Typography variant="body2">{product.stock}</Typography>

            <Typography variant="caption" className="text-gray-400 mt-2 block">
              Warranty:
            </Typography>
            <Typography variant="body2">{product.warrantyInformation}</Typography>

            <Typography variant="caption" className="text-gray-400 mt-2 block">
              Shipping:
            </Typography>
            <Typography variant="body2">{product.shippingInformation}</Typography>
          </Box>
        </Box>
      </Box>
    </Drawer>
  );
}
