import { Navigate } from 'react-router-dom';
import jwt_decode from 'jwt-decode';

interface DecodedToken {
  roles: string[];
  sub: string;
  iat: number;
  exp: number;
}

const checkRole = (token: string | null): boolean => {
  if (!token) return false;
  try {
    const decoded: DecodedToken = jwt_decode(token);
    return decoded.roles.includes('ROLE_ADMIN');
  } catch (e) {
    return false;
  }
};

interface ProtectedRouteProps {
  path: string;
  element: React.ReactElement;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ element, ...rest }) => {
  const token = localStorage.getItem('user'); // Assume you store your token in localStorage
  const userIsAdmin = checkRole(token);

  return userIsAdmin ? element : <Navigate to="/login" />;
};

export default ProtectedRoute;
