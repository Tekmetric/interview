import { Grid } from '@mui/material';
import { Container } from '@mui/system';
import React from 'react';
import { Disclaimer } from '../components/Disclaimer';
import { Header } from '../components/Header';
import { ReviewCard } from '../components/ReviewCard';
import { useReviews } from '../hooks/review';

const List = () => {
	const { reviews } = useReviews();

	return reviews.map(review => (
		<Grid item xs={12} sm={6} md={4} key={review.videoTitle}>
			<ReviewCard {...review} />
		</Grid>
	));
};

export const ReviewList = () => {
	return (
		<>
			<Header />

			<Container sx={{ paddingTop: 4, paddingBottom: 4 }}>
				<Disclaimer />

				<Grid container spacing={3}>
					<List />
				</Grid>
			</Container>
		</>
	);
};
