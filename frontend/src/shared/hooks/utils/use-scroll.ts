import { useEffect } from 'react';

type CallbackType = (offsetY?: number) => void;

const useScroll = (callback: CallbackType) => {
  useEffect(() => {
    const handleScroll = () => {
      callback(window.scrollY);
    };

    window.addEventListener('scroll', handleScroll);

    return () => {
      window.removeEventListener('scroll', handleScroll);
    };
  }, []);
};

export default useScroll;
