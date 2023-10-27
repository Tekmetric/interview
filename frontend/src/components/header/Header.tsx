import React from 'react';
import styles from './Header.module.scss';
import {classNames} from "../../utils/Utils";
export interface HeaderProps {};

/**
 * Header component to render on every page
 * @constructor
 */
const Header = () => {
    return (
        <header className={classNames(styles.header)}>
            <div className={classNames(styles.section, styles.left)}>
                <img src={"https://www.svgrepo.com/show/381093/ball-game-poke-sport-sports.svg"} alt={"Pokemon ball"} />
            </div>
            <div className={classNames(styles.section, styles.center)}>
            </div>
            <div className={classNames(styles.section, styles.right)}>
            </div>
        </header>
    )
};

export default Header;
