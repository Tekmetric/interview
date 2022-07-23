import React from 'react';
import {
	Card,
	CardActions,
	CardContent,
	CardMedia,
	Button,
	Typography,
	Tooltip,
	Link
} from '@mui/material';
import { ShareButton } from './ShareButton';

export const ReviewCard = props => {
	const { videoCode, videoTitle, product } = props;

	return (
		<Card sx={{ width: '100%' }}>
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
				<Tooltip title={videoTitle}>
					<Typography
						variant="body2"
						color="text.secondary"
						sx={{
							overflow: 'hidden',
							whiteSpace: 'nowrap',
							textOverflow: 'ellipsis'
						}}
					>
						{videoTitle}
					</Typography>
				</Tooltip>
			</CardContent>
			<CardActions>
				<ShareButton
					sharableText={`https://www.youtube.com/watch?v=${videoCode}`}
					size="small"
				>
					Share
				</ShareButton>
				<Link
					href={`https://youtube.com/watch?v=${videoCode}`}
					target="_blank"
					rel="noopener noreferrer"
					sx={{
						textDecoration: 'none'
					}}
				>
					<Button size="small">Watch</Button>
				</Link>
			</CardActions>
		</Card>
	);
};
