import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import { ReviewList } from './pages/ReviewList';

export const AppRoutes = () => {
	return (
		<Router>
			<Routes>
				<Route path="*" element={<ReviewList />} />
			</Routes>
		</Router>
	);
};
