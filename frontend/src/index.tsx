import { createRoot } from 'react-dom/client';
import './styles/normalize.css';
import './styles/globals.css';
import { App } from './App';

const container = document.getElementById('root');
const root = createRoot(container!);

root.render(<App />);

