import type { FC } from 'react';
import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { FaUser, FaCar, FaCalendarCheck } from 'react-icons/fa';

import { useAppSelector, useAppDispatch } from '../../../store/store';

import NoticeBanner from '../NoticeBanner';
import styles from './dashboard.module.css';

interface DashboardProps {
  children: React.ReactNode;
}

const Dashboard: FC<DashboardProps> = ({ children }) => {
  const user = useAppSelector((state) => state.application.user);

  const [menuOpen, setMenuOpen] = useState(false);

  return (
    <div className={styles.dashboard}>
      <NoticeBanner />

      <section className={`${styles.sidebar} ${menuOpen ? styles.active : undefined}`}>
        <button className={styles.toggle} onClick={() => setMenuOpen(!menuOpen)}>
          ☰
        </button>
        <div className={styles.sidebar__inner}>
          <Link className={styles.logo} to={'/'}>
            <span>Tekmetric</span>
            <span>Candidate Screen</span>
          </Link>

          <div className={styles.sidebar__profile}>
            <Link to="/profile">
              <img
                src={`${user.image}`}
                alt={`user profile, ${user.name}`}
                onError={({ currentTarget }) => {
                  currentTarget.onerror = null;
                  currentTarget.src = 'https://placehold.co/300';
                }}
              />
            </Link>
            <h2>{user.name}</h2>
            <h3>{user.role}</h3>
          </div>
          <ul>
            <li>
              <FaCar aria-hidden="true" />
              <Link to="/">Inventory</Link>
            </li>
            <li>
              <FaCalendarCheck aria-hidden="true" />
              <Link to="/scheduling">Schedules</Link>
            </li>
            <li>
              <FaUser aria-hidden="true" />
              <Link to="/profile">Profile</Link>
            </li>
          </ul>
        </div>
      </section>
      <section className={styles.grid_container}>{children}</section>
    </div>
  );
};

export default Dashboard;
