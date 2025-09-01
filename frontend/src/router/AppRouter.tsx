import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';

import { Layout } from '../components/Layout';
import { FeaturesPage } from '../pages/help/FeaturesPage';
import { GettingStartedPage } from '../pages/help/GettingStartedPage';
import { UserManagementPage } from '../pages/help/UserManagementPage';
import { HelpPage } from '../pages/HelpPage';
import { HomePage } from '../pages/HomePage';
import { NotFoundPage } from '../pages/NotFoundPage';
import { UserDetailPage } from '../pages/UserDetailPage';
import { UsersPage } from '../pages/UsersPage';

export const AppRouter: React.FC = () => {
  return (
    <Router>
      <Routes>
        <Route path='/' element={<Layout />}>
          <Route index element={<HomePage />} />

          <Route path='users' element={<UsersPage />} />
          <Route path='users/new' element={<UserDetailPage newUser={true} />} />
          <Route path='users/:id' element={<UserDetailPage />} />
          <Route path='users/:id/edit' element={<UserDetailPage editMode={true} />} />

          <Route path='/help' element={<HelpPage />}>
            <Route path='getting-started' element={<GettingStartedPage />} />
            <Route path='user-management' element={<UserManagementPage />} />
            <Route path='features' element={<FeaturesPage />} />
          </Route>

          <Route path='*' element={<NotFoundPage />} />
        </Route>
      </Routes>
    </Router>
  );
};
