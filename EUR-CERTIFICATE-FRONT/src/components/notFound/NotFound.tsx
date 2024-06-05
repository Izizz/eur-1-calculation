import React from 'react';
import './notFound.css';
const NotFound: React.FC = () => {
  return (
    <div className="not-found-container">
      <h1 className="not-found-title">404 - Page Not Found</h1>
      <p className="not-found-message">The requested page could not be found.</p>
    </div>
  );
}

export default NotFound;
