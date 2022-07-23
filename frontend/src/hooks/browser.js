import { useEffect } from 'react';

export const useAppScroll = callback => {
	useEffect(() => {
		if (!callback) return;

		const handleScroll = () => {
			callback(window.scrollY);
		};

		window.addEventListener('scroll', handleScroll);

		return () => {
			window.removeEventListener('scroll', handleScroll);
		};
	}, [callback]);
};
