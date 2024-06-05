import { Link } from "react-router-dom";
import EventBus from "../../common/EventBus";
import * as AuthService from "../../services/auth-service";
import { useState, useEffect } from "react";
import './navbar.css';

export const Navbar=()=>{
  const [currentUser, setCurrentUser] = useState<String | undefined>();
  
    useEffect(() => {
      const user = AuthService.getCurrentUser();

      
      if (user) {
        setCurrentUser(user);
      }
      EventBus.on("logout", logOut);
  
      return () => {
        EventBus.remove("logout", logOut);
      };
    }, []);
  
    const logOut = () => {
      AuthService.logout();
      setCurrentUser(undefined);
    };
    return(
    <div className="navbar-container"  >
      <nav className="navbar navbar-expand navbar-body ">
        <Link to={"/home"} className="navbar-brand">
          <img className="navbar-logo" alt="logo" src={require('../../img/Logo256white.png')} />
        </Link>
        <div className="navbar-link-wrapper">
        <div className="navbar-link-container">
          {currentUser && (
            <div className="navbar-link">
              <Link to={"/calculate"} className="">
                Calculation
              </Link>   
            </div>
          )}
           {currentUser && (
          <div className="navbar-link">
              <Link to={"/files"} className="">
                Files
              </Link>
            </div>
          )}
          {currentUser && (
          <div className="navbar-link">
              <Link to={"/upload"} className="">
                Upload files
              </Link>
            </div>
          )}
        </div>

        {currentUser ? (
          <div className="navbar-link-container">
          
            <div className="navbar-link">
              <a href="/login" className="" onClick={logOut}>
                LogOut
              </a>
            </div>
            <div className="navbar-link">
              <span className="current-user"> {currentUser}</span>
            </div>
          </div>
        ) : (
          <div className="navbar-link">            
              <Link to={"/login"} className="navbar-link">
                Login
              </Link>
          </div>
        )}
      </div>
      </nav>
    </div>
    );
}