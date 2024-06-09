import React, { Component } from 'react';
//import logo from './logo.svg';
import {BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import AddBookComponent from './components/AddBookComponent';
import HeaderComponent from "./components/HeaderComponent";
import FooterComponent from "./components/FooterComponent";
import BookListComponent from "./components/BookListComponent";


class App extends Component {
  render() {
    return (
      <div>
        <Router>
          <HeaderComponent />
          <div className="container">
            <Routes>
              <Route exact path="/" Component = {BookListComponent}></Route>
              <Route path="/books" Component = {BookListComponent}></Route>
              <Route path="/add-book" Component = {AddBookComponent}></Route>
              <Route path="/edit-book/:id" Component = {AddBookComponent}></Route>
            </Routes>
          </div>
          <FooterComponent />
        </Router>
      </div>
    );
  }
}

export default App;
