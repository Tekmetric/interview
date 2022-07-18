import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';

import {
  Grid,
  Container,
  Typography,
  ImageList,
  ImageListItem,
  Paper,
  List,
  ListItem,
  ListItemText,
  Breadcrumbs,
  Link,
  Rating,
} from '@mui/material';
import ProductService from './Product.service';

import './Product.scss';

function Product() {
  const { category, id } = useParams();

  const [product, setProduct] = useState();
  const [productError, setProductError] = useState([]);

  useEffect(() => {
    async function fetchCategories() {
      const categoriesData = await ProductService.get(id);
      if (categoriesData.error) {
        setProductError(categoriesData.error.message);
      }
      setProduct(categoriesData);
    }
    fetchCategories();
  }, []);

  return (
    <Container id="product" maxWidth="lg">
      <Breadcrumbs aria-label="breadcrumb" className="breadcrumbs">
        <Link underline="hover" color="inherit" href="/categories">
          Categories
        </Link>
        <Link underline="hover" color="inherit" href="/categories">
          {category}
        </Link>
        <Typography color="text.primary">{product && product.title}</Typography>
      </Breadcrumbs>

      {!product && (
      <Typography component="h6">
        <div>Products loading...</div>
      </Typography>
      )}
      {productError && (
      <Typography component="h6" color="red">
        <div>{ productError }</div>
      </Typography>
      )}

      {product && (
      <Grid container spacing={4}>
        <Grid item xs={12} sm={12} md={12}>
          <Paper variant="outlined" square>
            <Grid container spacing={4} justify="space-between" alignItems="center">
              <Grid item xs={12} sm={12} md={12} className="titleGrid">
                <div className="title-price">
                  <Typography component="h1" variant="h6">
                    {product.title}
                  </Typography>
                  <Typography component="h1" variant="h5">
                    {`${product.price}$`}
                  </Typography>
                </div>
                <div className="rating">
                  <Rating name="read-only" value={product.rating} size="small" readOnly />
                  <Typography component="h6">
                    (
                    {product.rating}
                    )
                  </Typography>
                </div>
              </Grid>
            </Grid>
          </Paper>
        </Grid>
        <Grid item xs={12} sm={12} md={12}>
          <Paper variant="outlined" square className="paper">
            <List>
              <ListItem>
                <ListItemText primary="Description" secondary={product.description} />
              </ListItem>
              <ListItem>
                <ListItemText primary="Brand" secondary={product.brand} />
              </ListItem>
            </List>
          </Paper>
        </Grid>
        <Grid item xs={12} sm={12} md={12}>
          <Paper variant="outlined" square className="paper">
            <ImageList sx={{ width: '100%', height: 800 }} cols={3} rowHeight={164}>
              {product.images.map((url) => (
                <ImageListItem key={url}>
                  <img src={url} alt="" loading="lazy" />
                </ImageListItem>
              ))}
            </ImageList>
          </Paper>
        </Grid>
      </Grid>
      )}
    </Container>
  );
}

export default Product;
