import React from 'react';
import ReactDOM from 'react-dom';
import './assets/css/index.css';
import App from './App'; 
import { Provider, Client } from 'urql'

const client = new Client({
    url: 'https://api.spacex.land/graphql/',
})


ReactDOM.render(
    <Provider value={client}>
        <App />
    </Provider>, 
document.getElementById('root'));

 