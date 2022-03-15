import React, { Component } from 'react';
import { Footer, Nav } from './components';
//import logo from './logo.svg';
 

class App extends Component {
  render() {
    return (
      <>
      <Nav/>
      <div className="App">
        <header className="App-header">
          <h1 className="text-3xl font-bold underline">SpaceX Launch Locator</h1>

          <p>
            Edit <code>src/App.js</code> and save to reload.
          </p>

        <or>
          <li>Fetch Data from a public API <a href="https://github.com/toddmotto/public-apis">Samples</a></li>
          <li>Display data from API onto your page (Table, List, etc.)</li>
          <li>Apply a styling solution of your choice to make your page look different (CSS, SASS, CSS-in-JS)</li> 
        </or>   
       
        </header>
      </div>
      <Footer/>
      </>

    );
  }
}

export default App;
