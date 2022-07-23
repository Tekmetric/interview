import { Alert } from '@mui/material';
import React from 'react';

export const Disclaimer = () => {
	return (
		<Alert color="info" sx={{ marginBottom: 4 }}>
			I'm not a fan of the bellow videos, I just found them on{' '}
			<a
				href="https://github.com/andyklimczak/TheReportOfTheWeek-API/blob/master/seeds/reports.json"
				target="_blank"
				rel="noopener noreferrer"
			>
				this public API
			</a>{' '}
			and used it because it was easy to display thumbnails from youtube.
		</Alert>
	);
};
