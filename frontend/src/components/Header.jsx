import React, { useCallback, useState } from 'react';
import { AppBar, Toolbar, Typography } from '@mui/material';
import { Link } from 'react-router-dom';
import { useAppScroll } from '../hooks/browser';

const HeaderToolbar = () => {
	const [shadow, setShadow] = useState(false);

	const handleScroll = useCallback(scrollTop => {
		setShadow(scrollTop > 5);
	}, []);

	useAppScroll(handleScroll);

	return (
		<Toolbar
			sx={{
				boxShadow: shadow ? 'rgba(0, 0, 0, 0.08) 0px 1px 12px' : 'none'
			}}
		>
			<Typography
				component={Link}
				to="/"
				variant="h5"
				color="primary"
				noWrap
				sx={{
					textDecoration: 'none',
					color: theme => theme.palette.text.primary
				}}
			>
				Food Reviews
			</Typography>
		</Toolbar>
	);
};

export const Header = () => {
	return (
		<AppBar
			position="sticky"
			sx={{
				background: theme => theme.palette.background.default,
				paddingTop: {
					sx: 1
				},
				paddingBottom: {
					sx: 1
				}
			}}
			elevation={0}
		>
			<HeaderToolbar />
		</AppBar>
	);
};
