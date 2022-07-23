import React from 'react';
import {
	Card,
	CardActions,
	CardContent,
	CardMedia,
	Button,
	Typography
} from '@mui/material';

export const ReviewCard = props => {
	const { videoCode, videoTitle, product } = props;

	return (
		<Card sx={{ maxWidth: 345 }}>
			<CardMedia
				component="img"
				height="140"
				image={`https://img.youtube.com/vi/${videoCode}/hqdefault.jpg`}
				alt={videoTitle}
			/>
			<CardContent>
				<Typography gutterBottom variant="h5" component="div">
					{product}
				</Typography>
				<Typography variant="body2" color="text.secondary">
					{videoTitle}
				</Typography>
			</CardContent>
			<CardActions>
				<Button size="small">Share</Button>
				<Button size="small">Watch</Button>
			</CardActions>
		</Card>
	);
};
