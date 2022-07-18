import React from 'react';
import PropTypes, { string } from 'prop-types';

import CardActionArea from '@mui/material/CardActionArea';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import CardMedia from '@mui/material/CardMedia';
import Divider from '@mui/material/Divider';
import Grid from '@mui/material/Grid';

import Typography from '@mui/material/Typography';
import { useNavigate } from 'react-router-dom';

function Category({ category }) {
  const navigate = useNavigate();

  function handleCardClick() {
    navigate(`/categories/${category}`);
  }

  return (
    <Grid item xs={12} sm={6} md={4}>
      <CardActionArea className="cardActionArea" onClick={() => handleCardClick()}>
        <Card className="card">
          <CardMedia
            component="img"
            height="140"
            // eslint-disable-next-line global-require
            image={`/images/${category}.jpg`}
            title={category}
          />
          <Divider />
          <CardContent className="cardContent">
            <Typography gutterBottom variant="h5" component="div">
              {category}
            </Typography>
          </CardContent>
        </Card>
      </CardActionArea>
    </Grid>
  );
}

Category.propTypes = {
  category: PropTypes.arrayOf(string).isRequired,
};

export default Category;
