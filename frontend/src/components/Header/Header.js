import React from 'react';
import styles from './Header.module.css';
import Navbar from "../Navbar/Navbar";

const Header = () => (
    <div className={styles.Header}>
        <div id="header" className="w-full pb-24">
            <Navbar></Navbar>
            <div className="relative max-w-screen-xl px-4 sm:px-8 mx-auto grid grid-cols-12 gap-x-6 overflow-hidden">
                <div
                    className="col-span-12 lg:col-span-6 mt-12 xl:mt-10 space-y-4 sm:space-y-6 px-6 text-center
                     sm:text-left">
                    <span className="text-base text-gradient font-semibold uppercase">Tennis Federation</span>
                    <h1 className="text-[2.5rem] sm:text-5xl xl:text-6xl font-bold leading-tight capitalize
                    sm:pr-8 xl:pr-10">
                        The World's <br/><span className={styles.TextHeaderGradient}>Best Tennis Players</span>
                        <br/>
                        2023
                    </h1>
                    <p className="paragraph hidden sm:block">
                        Here it's a display of the world's best tennis players around the globe in 2023
                    </p>
                </div>
                <div className="hidden sm:block col-span-12 lg:col-span-6">
                    <div className="w-full"><img src="tennis.png" alt="" className="mt-8"/></div>
                </div>
            </div>
        </div>
    </div>
);

Header.propTypes = {};

Header.defaultProps = {};

export default Header;
