// List/card endpoints — reviews included only to derive count
export const PRODUCT_SUMMARY_SELECT =
  'id,sku,title,brand,price,discountPercentage,rating,reviews,thumbnail,availabilityStatus';

// Detail drawer — only fields specified in requirements (+ sku for cart actions)
export const PRODUCT_DETAIL_SELECT =
  'id,sku,title,description,brand,price,discountPercentage,rating,stock,images,thumbnail,weight,dimensions,shippingInformation,availabilityStatus,returnPolicy,reviews';
