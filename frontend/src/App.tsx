import { Component } from 'react';
import VehicleContainer from './features/vehicles';
import PageHeader from './components/PageHeader';
import PageFooter from './components/PageFooter';

import styles from './app.module.css';

class App extends Component {
  render() {
    return (
      <>
        <PageHeader />
        <VehicleContainer />
        <PageFooter />
      </>
    );
  }
}

export default App;
