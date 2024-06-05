import React, { useEffect } from 'react';
import './App.css';
import Login from "./components/auth/Login";
import Home from "./components/home/Home";
import Calculation from "./components/calculation/Calculation";
import { Navbar } from './components/navbar/Navbar';
import { Routes, Route } from 'react-router-dom';
import FileTable from './components/files/Files';
import FileUpload from './components/files/FileUpload';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import ProtectedRoute from './components/routes/PrivateRoute';
import AdminComponent from './components/admin/AdminComponent';
import NotFound from './components/notFound/NotFound';
import { Conversion } from './components/conversion/Conversion';

const App: React.FC = () => {


  return (

      <div className="app">
        <ToastContainer />
        <Navbar />
        <Routes>
          <Route path="/" element={<Home/>} />
          <Route path="/home" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/calculate" element={<Calculation />} />
          <Route path="/files" element={<FileTable />} />
          <Route path="/upload" element={<FileUpload />} />
          <Route path="/users" element={<ProtectedRoute element={<AdminComponent />} path={''} />} />
          <Route path="/convert" element={<Conversion />} />
          <Route path="*" element={<NotFound />} />
        </Routes>
      </div>

  );
}

export default App;
