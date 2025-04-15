import type { FC } from 'react';
import { useEffect, useState } from 'react';

import { useAppDispatch, useAppSelector } from '../../../store/store';
import { clearNotice } from '../actions';

import styles from './noticeBanner.module.css';

const NoticeBanner: FC = () => {
  const dispatch = useAppDispatch();
  const notice = useAppSelector((state) => state.application.notice);
  const [visible, setVisible] = useState(false);

  useEffect(() => {
    if (notice.message) {
      setTimeout(() => {
        setVisible(true);
      }, 200);

      const timeout = setTimeout(() => {
        setVisible(false);
        setTimeout(() => dispatch(clearNotice()), 500);
      }, 3000);
      return () => clearTimeout(timeout);
    }
  }, [notice, dispatch]);

  if (!notice.message) return null;

  return (
    <div
      className={`${styles.alertBanner} ${styles[notice.type ?? '']} ${
        visible ? styles.visible : styles.hidden
      }`}
    >
      <p>{notice.message}</p>
    </div>
  );
};

export default NoticeBanner;
