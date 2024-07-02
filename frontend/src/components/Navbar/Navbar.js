import React from 'react';
import styles from './Navbar.module.css';
import {NavLink} from "react-router-dom";

const Navbar = () => (
    <div className={styles.Navbar}>
        <nav id="navbar" className="relative z-10 w-full text-neutral-800">
            <div className="flex flex-col max-w-screen-xl px-8 mx-auto lg:items-center lg:justify-between lg:flex-row
            py-4">
                <div className="flex flex-col lg:flex-row items-center space-x-4 xl:space-x-8">
                    <div className="w-full flex flex-row items-center justify-between py-6">
                        <h1 className={styles.TextLogo}>Tennis</h1>
                    </div>
                    <ul className="w-full h-auto flex flex-col flex-grow lg:items-center pb-4 lg:pb-0 lg:justify-end
                     lg:flex-row origin-top duration-300 xl:space-x-2 space-y-3 lg:space-y-0 hidden lg:flex">
                        <NavLink to="/" className={({isActive}) =>
                            isActive ? "md:px-4 py-2 text-sm bg-transparent rounded-lg text-[#666666] " +
                                "hover:text-gray-900 focus:outline-none focus:shadow-outline "
                                + styles.active : "md:px-4 py-2 text-sm bg-transparent rounded-lg text-[#666666] " +
                                "hover:text-gray-900 focus:outline-none focus:shadow-outline"}>
                            Home
                        </NavLink>
                    </ul>
                </div>
            </div>
        </nav>
    </div>
);

Navbar.propTypes = {};

Navbar.defaultProps = {};

export default Navbar;
