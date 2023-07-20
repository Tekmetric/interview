import { IconButton } from '@mui/material';
import KeyboardArrowUpIcon from '@mui/icons-material/KeyboardArrowUp';
import { useState } from 'react';
import useScroll from '../shared/hooks/utils/use-scroll';

const BackToTop = () => {
  const [isVisible, setIsVisible] = useState(true);

  const hide = (offsetY?: number) => {
    if (offsetY !== undefined && offsetY < 400) {
      setIsVisible(false);
    }
    else {
      setIsVisible(true);
    }
  }

  useScroll(hide);

  const handleClick = () => {
    window['scrollTo']({ top: 0, behavior: 'smooth' });
  }

  const appearenceClasses = 'bg-slate-300 hover:bg-slate-400 hover:bg-opacity-70 ease-in-out transition-opacity';
  const positionClasses = 'fixed bottom-5 right-5';
  const visibilityClass = isVisible ? 'opacity-100 duration-500' : 'opacity-0 duration-300 invisible';

  const computedClasses = `${appearenceClasses} ${positionClasses} ${visibilityClass}`;

  return (
    <IconButton
      className={`${computedClasses}`}
      aria-label="Back to the top"
      title="Scroll to top"
      onClick={handleClick}
    >
      <KeyboardArrowUpIcon />
    </IconButton>
  );
};

export default BackToTop;
