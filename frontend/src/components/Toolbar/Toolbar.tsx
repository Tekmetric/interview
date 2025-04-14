import type { FC } from 'react';
import { Link } from 'react-router-dom';

import styles from './toolBar.module.css';

const ToolBar: FC = () => {
  return (
    <section className={styles.header}>
      <Link className="link-button" to="/vehicle/create">
        add new vehicle
      </Link>
      <input type="text" placeholder="search" />
    </section>
  );
};

export default ToolBar;
