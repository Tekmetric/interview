import React from 'react';

import './App.css';
import { ReactComponentI } from './interfaces/components';

const App: React.FC<ReactComponentI> = ({ children }) => {
  return <>{children}</>;
};

export default App;
