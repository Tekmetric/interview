import { useEffect, useState } from 'react';
import { Routes, Route, Link } from "react-router-dom";
import AuthService from './services/AuthService';
import LoginForm from './components/LoginForm';
import RegisterForm from './components/RegisterForm';
import VehicleList from './components/VehicleList';
import ProfilePage from './components/ProfilePage';
import UserType from './types/UserType';
import Image from 'react-bootstrap/Image';
import CreateVehicleForm from './components/CreateVehicleForm';
import EditVehicleForm from './components/EditVehicleForm';
import logo from './logo.svg';
import "bootstrap/dist/css/bootstrap.min.css";
import './App.css';


export default function App() {
  const [currentUser, setCurrentUser] = useState<UserType | null>(null);

  useEffect(() => {
    const user = AuthService.getCurrentUser();
    if (user) {
      setCurrentUser(user);
    }
  }, []);

  function handleLogout() {
    AuthService.logout();
    setCurrentUser(null);
  }

  return (
    <div>
      <nav className="navbar navbar-expand navbar-dark bg-dark">
        <Link to={"/"} className="navbar mr">
          <Image src={logo} style={{height:20}}/>
        </Link>
        <div className="navbar-nav mr-auto">
          {currentUser !== null && (
            <li className="nav-item">
              <Link to={"/"} className="nav-link">
                Vehicles
              </Link>
            </li>
          )}
        </div>

        {currentUser ? (
          <div className="navbar-nav ml-auto">
            <li className="nav-item">
              <Link to={"/profile"} className="nav-link">
                {currentUser.firstName}
              </Link>
            </li>
            <li className="nav-item">
              <a href="/login" className="nav-link" onClick={handleLogout}>
                Logout
              </a>
            </li>
          </div>
        ) : (
          <div className="navbar-nav ml-auto">
            <li className="nav-item">
              <Link to={"/login"} className="nav-link">
                Login
              </Link>
            </li>

            <li className="nav-item">
              <Link to={"/register"} className="nav-link">
                Register
              </Link>
            </li>
          </div>
        )}
      </nav>

      <div className="container mt-3">
        <Routes>
          <Route path="/" element={<VehicleList />} />
          <Route path="/login" element={<LoginForm />} />
          <Route path="/register" element={<RegisterForm />} />
          <Route path="/profile" element={<ProfilePage />} />
          <Route path="/vehicle/create" element={<CreateVehicleForm />} />
          <Route path="/vehicle/edit/:id" element={<EditVehicleForm />} />
        </Routes>
      </div>
    </div>
  );
}
