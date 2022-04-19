import React from 'react';
import {Link} from "react-router-dom";

const Navbar = () => {
    return (
        <nav>
            <section>
                <h1>Tekmetric Demo App</h1>

                <div className="navContent">
                    <div className="navLinks">
                        <Link to="/">HomePage</Link>
                        <Link to="/products">Products</Link>
                    </div>
                </div>
            </section>
        </nav>
    );
};

export default Navbar;
