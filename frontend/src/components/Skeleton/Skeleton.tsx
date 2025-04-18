import type { FC } from 'react';
import styles from './skeleton.module.css';

interface SkeletonProps {
  count?: number;
}

const Skeleton: FC<SkeletonProps> = ({ count = 1 }) => {
  return (
    <div className={styles.skeleton}>
      {Array.from({ length: count }).map((_, index) => (
        <div key={`skeleton_${index}`} />
      ))}
    </div>
  );
};

export default Skeleton;
