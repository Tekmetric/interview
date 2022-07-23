import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import { setupApi } from './api';

setupApi();

ReactDOM.render(<App />, document.getElementById('root'));
