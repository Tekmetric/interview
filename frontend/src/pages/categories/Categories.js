import React, { useEffect, useState } from 'react';

import Grid from '@mui/material/Grid';
import Container from '@mui/material/Container';
import { Typography } from '@mui/material';
import Breadcrumbs from '@mui/material/Breadcrumbs';
import CategoriesService from './Categoriess.service';
import Category from './components/Category';

import './Categories.scss';

function Categories() {
  const [categories, setCategories] = useState();
  const [categoriesError, setCategoriesError] = useState([]);

  useEffect(() => {
    async function fetchCategories() {
      const categoriesData = await CategoriesService.getAll();
      if (categoriesData.error) {
        setCategoriesError(categoriesData.error.message);
      }
      setCategories(categoriesData);
    }
    fetchCategories();
  }, []);

  return (
    <Container id="categories" maxWidth="lg">
      <Breadcrumbs aria-label="breadcrumb" className="breadcrumbs">
        <Typography color="text.primary">Categories</Typography>
      </Breadcrumbs>
      {!categories && (
      <Typography component="h6">
        <div>Categories loading...</div>
      </Typography>
      )}
      {categoriesError && (
      <Typography component="h6" color="red">
        <div>{ categoriesError }</div>
      </Typography>
      )}
      <Grid container spacing={4}>
        {categories && categories.map((category) => (
          <Category category={category} key={category} />
        ))}
      </Grid>
    </Container>
  );
}

export default Categories;
