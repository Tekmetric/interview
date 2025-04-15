import type { FC } from 'react';
import styles from './skeleton.module.css';

interface SkeletonProps {
  count: number;
}

const Skeleton: FC<SkeletonProps> = ({ count = 1 }) => {
  return (
    <div className={styles.skeleton}>
      {[...Array(count)].map((el, key) => (
        <div key={`skeleton_${key}`} />
      ))}
    </div>
  );
};

export default Skeleton;
