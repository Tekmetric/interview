import { Grid } from '@mui/material';
import { Container } from '@mui/system';
import React from 'react';
import { Header } from '../components/Header';
import { ReviewCard } from '../components/ReviewCard';
import { useReviews } from '../hooks/review';

const List = () => {
	const { reviews } = useReviews();

	return reviews.map(review => (
		<Grid item xs={12} sm={6} md={'auto'} key={review.videoTitle}>
			<ReviewCard {...review} />
		</Grid>
	));
};

export const ReviewList = () => {
	return (
		<Container>
			<Header />

			<Grid container spacing={2}>
				<List />
			</Grid>
		</Container>
	);
};
