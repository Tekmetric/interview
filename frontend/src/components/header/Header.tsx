import React from 'react';
import styles from './Header.module.scss';
import {classNames} from "../../utils/Utils";
import Pokeball from './assets/pokeball.png';
export interface HeaderProps {};

/**
 * Header component to render on every page
 * @constructor
 */
const Header = () => {
    return (
        <header className={classNames(styles.header)}>
            <div className={classNames(styles.section, styles.left)}>
                <img src={Pokeball} alt={"Pokemon ball"} />
            </div>
            <div className={classNames(styles.section, styles.center)}>
            </div>
            <div className={classNames(styles.section, styles.right)}>
            </div>
        </header>
    )
};

export default Header;
