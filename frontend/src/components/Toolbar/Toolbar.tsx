import type { FC, FormEvent } from 'react';
import { useState, useRef } from 'react';
import { FaSearch, FaPlus } from 'react-icons/fa';
import { Link } from 'react-router-dom';

import styles from './toolBar.module.css';

interface ToolBarProps {
  handleSearch: (query: string) => void;
  currentSearch: string;
  resultCount: number | undefined;
}

const ToolBar: FC<ToolBarProps> = ({ handleSearch, currentSearch, resultCount }) => {
  const [searchQuery, setSearchQuery] = useState(currentSearch ?? '');
  const isCooldown = useRef(false);

  const searchBy = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (isCooldown.current) return;

    isCooldown.current = true;
    setTimeout(() => {
      isCooldown.current = false;
    }, 300);

    handleSearch(searchQuery);
  };

  const resultString = resultCount
    ? `${resultCount} search ${resultCount === 0 ? 'results' : 'result'} found`
    : '';

  return (
    <section className={styles.header}>
      <div className={styles.searchBar}>
        <Link className="link-button" to="/vehicle/create">
          <FaPlus /> <span>add new vehicle</span>
        </Link>
        <form className={styles.search} onSubmit={(e) => searchBy(e)}>
          <input
            data-testid="search"
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Enter Search Term"
            type="text"
            value={searchQuery}
          />
          <button type="submit" aria-label={`Search`}>
            <FaSearch aria-hidden="true" />
          </button>
        </form>
      </div>
      <div className={styles.results} data-id>
        <p>{resultString}</p>
      </div>
    </section>
  );
};

export default ToolBar;
