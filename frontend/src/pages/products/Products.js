import React, { useEffect, useState } /* , { useEffect } */ from 'react';

import Container from '@mui/material/Container';
import { DataGrid } from '@mui/x-data-grid';
import Breadcrumbs from '@mui/material/Breadcrumbs';
import Link from '@mui/material/Link';
import Rating from '@mui/material/Rating';
import { Typography } from '@mui/material';

import { useNavigate, useParams } from 'react-router-dom';
import ProductsService from './Products.service';

import './Products.scss';

function Products() {
  const { category } = useParams();
  const navigate = useNavigate();

  const [products, setProducts] = useState();
  const [productsError, setProductsError] = useState([]);
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(3);

  const columns = [
    {
      // eslint-disable-next-line jsx-a11y/alt-text
      field: 'thumbnail',
      headerName: '',
      width: 400,
      height: 400,
      sortable: false,
      // eslint-disable-next-line jsx-a11y/alt-text
      renderCell: (params) => <img src={params.value} className="thumbnail" />,
    },
    {
      field: 'title', headerName: 'Title', flex: 1, minWidth: 300, sortable: false,
    },
    {
      field: 'rating',
      headerName: 'Rating',
      width: 180,
      sortable: false,
      renderCell: (params) => (
        <>
          <Rating name="read-only" value={params.value} size="small" readOnly />
          {' '}
          {params.value}
        </>
      ),
    },
    {
      field: 'price', headerName: 'Price', width: 100, sortable: false,
    },
  ];

  useEffect(() => {
    async function fetchCategories() {
      const productsData = await ProductsService.getAll({
        category,
        limit: 3,
        skip: page * 3,
      });
      if (productsData.error) {
        setProductsError(productsData.error.message);
      }
      setProducts(productsData);
    }
    fetchCategories();
  }, [page]);

  function handleRowClick(a) {
    navigate(`/categories/${category}/${a.id}`);
  }

  return (
    <Container id="products" maxWidth="lg">
      <Breadcrumbs aria-label="breadcrumb" className="breadcrumbs">
        <Link underline="hover" color="inherit" href="/categories">
          Categories
        </Link>
        <Typography color="text.primary">{category}</Typography>
      </Breadcrumbs>
      <div style={{ height: 800, width: '100%' }}>
        {!products && (
          <Typography component="h6">
            <div>Products loading...</div>
          </Typography>
        )}
        {productsError && (
          <Typography component="h6" color="red">
            <div>{ productsError }</div>
          </Typography>
        )}
        {products && (
        <DataGrid
          rows={products.products}
          rowCount={products.total}
          loading={!products}
          rowsPerPageOptions={[3]}
          pagination
          page={page}
          pageSize={pageSize}
          paginationMode="server"
          onPageChange={(newPage) => setPage(newPage)}
          onPageSizeChange={(newPageSize) => setPageSize(newPageSize)}
          columns={columns}
          disableColumnFilter
          disableColumnMenu
          rowHeight={200}
          onRowClick={(a) => handleRowClick(a)}
          sx={{
            border: 0,
          }}
        />
        )}
      </div>
    </Container>
  );
}

export default Products;
