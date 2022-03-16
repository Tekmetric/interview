import React, { Component } from 'react';
import { Footer, MissionList, Hero } from './components';
 
class App extends Component {
  render() {
    return (
      <>
      <Hero/>
      <MissionList/>
      <Footer/>
      </>

    );
  }
}

export default App;
