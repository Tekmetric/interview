import React from 'react';

import './App.css';
import { ReactComponent } from './interfaces/components';

const App: React.FC<ReactComponent> = ({ children }) => {
  return <>{children}</>;
};

export default App;
