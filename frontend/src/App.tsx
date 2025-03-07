import './App.css'
import { Navigate, Routes, Route } from 'react-router-dom'
import Home from './pages/Home'
import Login from './pages/Login'
import Books from './pages/Books'
import Authors from './pages/Authors'
import ReadingLists from './pages/ReadingLists'
import About from './pages/About'
import Sidebar from './components/Sidebar'
import Header from './components/Header'
import PrivateRoute from './components/PrivateRoute'
import { Typography } from '@mui/material'
import RegisterForm from './components/RegisterForm'


const App = () => {


  return (
    <div className="flex flex-col min-h-screen min-w-screen">
      <Header />
      <div className="flex flex-row flex-grow">
          <aside className="w-full md:w-24 p-4 bg-gray-200 order-1"><Sidebar /></aside>
          <main className="flex-grow p-4 order-2 md:order-1">
            <Routes>
              <Route path="/" element={<About />} />
              <Route path="/home" element={<Home />} />
              <Route path="/login" element={<Login />} />
              <Route path="/books" element={<PrivateRoute><Books /></PrivateRoute>} />
              <Route path="/authors" element={<PrivateRoute><Authors /></PrivateRoute>} />
              <Route path="/reading-lists" element={<PrivateRoute><ReadingLists /></PrivateRoute>} />
              <Route path="/about" element={<About />} />
              <Route path="/register" element={<RegisterForm />} />
              <Route path="*" element={<Navigate to="/" />} />
            </Routes>
          </main>
      </div>
      <footer className="bg-gray-300 p-1">
        <Typography variant="subtitle2">Created by Oğuz Aslantürk for Tekmetrik Interview @ March, 2025</Typography>
      </footer>
    </div>
  );
}

export default App;