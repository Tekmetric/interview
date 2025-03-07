import React from 'react'
import { Navigate } from 'react-router-dom'
import { getAuthToken } from '../services/authService'

interface PrivateRouteProps {
  children: React.ReactNode
}

const PrivateRoute: React.FC<PrivateRouteProps> = ({ children }) => {
  if (!getAuthToken()) {
    return <Navigate to="/login" />;
  }

  return <>{children}</>
}

export default PrivateRoute
