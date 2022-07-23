import React, { useState } from 'react';
import { Grid, LinearProgress, Pagination } from '@mui/material';
import { Container } from '@mui/system';
import { Disclaimer } from '../components/Disclaimer';
import { Header } from '../components/Header';
import { ReviewCard } from '../components/ReviewCard';
import { useReviews } from '../hooks/review';

const List = ({ reviews }) => {
	return reviews.map(review => (
		<Grid item xs={12} sm={6} md={4} key={review.videoCode}>
			<ReviewCard {...review} />
		</Grid>
	));
};

export const ReviewList = () => {
	const [page, setPage] = useState(1);
	const { reviews, loading } = useReviews(page);

	return (
		<>
			{loading && (
				<LinearProgress
					sx={{
						position: 'fixed',
						top: 0,
						left: 0,
						width: '100%',
						zIndex: 9999
					}}
				/>
			)}

			<Header />

			<Container sx={{ paddingTop: 4, paddingBottom: 20 }}>
				<Disclaimer />

				<Pagination
					page={page}
					onChange={(_event, value) => setPage(value)}
					count={4}
					shape="circular"
					sx={{ marginTop: 2, float: 'right' }}
				/>

				<Grid container spacing={3}>
					<List reviews={reviews} />
				</Grid>
			</Container>
		</>
	);
};
