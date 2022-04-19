import React, { Component } from 'react';
import HomePage from "./features/HomePage";
import {
    BrowserRouter as Router,
    Switch,
    Route,
    Redirect,
} from 'react-router-dom'
import Navbar from "./app/Navbar";
import ProductListPage from "./features/products/ProductListPage";

class App extends Component {
  render() {
    return (
        <Router>
            <Navbar />
            <div className="App">
                <Switch>
                    <Route
                        exact
                        path="/"
                        render={() => (
                            <React.Fragment>
                                <HomePage />
                            </React.Fragment>
                        )}
                    />
                    <Route exact path="/products" component={ProductListPage} />
                    <Redirect to="/" />
                </Switch>
            </div>
        </Router>

    );
  }
}

export default App;
