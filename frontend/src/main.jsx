import React from 'react';
import ReactDOM from 'react-dom/client';
import { RouterProvider } from 'react-router-dom';
import { SettingsProvider } from './context/SettingsContext';
import { LocaleProvider } from './i18n/LocaleProvider';
import { CollectionProvider } from './context/CollectionContext';
import { ArtworkModalProvider } from './context/ArtworkModalContext';
import { router } from './router';
import './index.css';

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <SettingsProvider>
      <LocaleProvider>
        <CollectionProvider>
          <ArtworkModalProvider>
            <RouterProvider router={router} />
          </ArtworkModalProvider>
        </CollectionProvider>
      </LocaleProvider>
    </SettingsProvider>
  </React.StrictMode>
);
