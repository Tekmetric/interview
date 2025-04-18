import { FC } from 'react';
import { Link } from 'react-router-dom';
import styles from './underConstruction.module.css';
import UnderConstructionImage from '../../assets/images/under_construction.png';

const UnderConstruction: FC = () => {
  return (
    <div className={styles.container}>
      <div className={styles.container__inner}>
        <h2>under construction</h2>
        <p>
          We&apos;re busy tuning things up under the hood. This page isn&apos;t quite ready yet, but
          working hard to get it running smoothly. Check back soon!
        </p>
        <img src={UnderConstructionImage} alt="Under Construction" />
        <Link className="link-button" to="/">
          Take Me Home
        </Link>
      </div>
    </div>
  );
};

export default UnderConstruction;
