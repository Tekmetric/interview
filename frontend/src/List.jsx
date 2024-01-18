import React, { Component } from 'react';
import './List.scss';
import { toUSD, toPercent } from './utils';

const BASE_URL = 'http://localhost:3001/api';

const color = (number) => {
    return number < 0 ? 'red' : 'green';
}

export class List extends Component {
  constructor(props) {
    super(props);
    this.state = {
        data: {},
        selected: null,
        isLoading: false,
        error: null
    };
  }

  componentDidMount() {
    this.setState({ isLoading: true });
    fetch(BASE_URL + '/v1/cryptocurrency/listings/latest')
      .then(response => {
        if (response.ok) {
          return response.json();
        } else {
          throw new Error('Something went wrong ...');
        }
      }) 
      .then(data => this.setState({ data, isLoading: false }))
      .catch(error => this.setState({ error, isLoading: false }));
  }

  // functions as a toggle for selecting a row
  select = (symbol) => () => {
    if (this.state.selected === symbol) {
      return this.setState({ selected: null });
    }
    this.setState({ selected: symbol });
  }

  isSelected = (symbol) => {
    return this.state.selected === symbol ? 'selected' : '';
  }

  render() {
    const { data, isLoading, error } = this.state;

    if (error) {
      return <div className="red">{error.message}</div>;
    }

    if (isLoading) {
        return <div>Loading...</div>;
    }

    return (
      <div className="coin-list">
        <table>
          <thead>
            <tr>
              <th className="left">#</th>
              <th className="left">Name</th>
              <th className="left">Symbol</th>
              <th className="right">Price</th>
              <th className="right">Market Cap</th>
              <th className="right">Change (24h)</th>
            </tr>
          </thead>
          <tbody>
            {data.data && data.data.map((coin, index) => (
              <tr key={coin.id} className={this.isSelected(coin.symbol)} onClick={this.select(coin.symbol)}>
                <td className="left">{index + 1}</td>
                <td className="left">{coin.name}</td>
                <td className="left">{coin.symbol}</td>
                <td className="right">{toUSD(coin.quote.USD.price)}</td>
                <td className="right">{toUSD(coin.quote.USD.market_cap)}</td>
                <td className={`right ${color(coin.quote.USD.percent_change_24h)}`}>
                  {toPercent(coin.quote.USD.percent_change_24h / 100)}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    );
  }
}