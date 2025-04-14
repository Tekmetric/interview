import { FC, useEffect } from 'react';

import { useAppDispatch, useAppSelector } from '../../../store/store';
import { clearNotice } from '../actions';

import styles from './noticeBanner.module.css';

const NoticeBanner: FC = () => {
  const dispatch = useAppDispatch();
  const notice = useAppSelector((state) => state.application.notice);

  useEffect(() => {
    if (notice.message) {
      const timeout = setTimeout(() => dispatch(clearNotice()), 4000);
      return () => clearTimeout(timeout);
    }
  }, [notice, dispatch]);

  if (!notice.message) return null;

  return (
    <div className={`${styles.alertBanner} ${styles[notice.type ?? '']}`}>
      <p>{notice.message}</p>
    </div>
  );
};

export default NoticeBanner;
