import React, { Component } from 'react';
import './App.scss';
import { List } from './List';
import Bitcoin from './Bitcoin.svg';

class App extends Component {
  render() {
    return (
      <div className="app">
        <header className="app-header">
          <img src={Bitcoin} className="app-logo" alt="logo" />
          <h1>CryptoTracker</h1>
        </header>

        <h2>Top 100 Cryptocurrencies by Market Capitalization</h2>

        <List />

        <footer>
          <p>Data from <a href="https://coinmarketcap.com/api/">CoinMarketCap</a></p>
        </footer>
      </div>
    );
  }
}

export default App;
