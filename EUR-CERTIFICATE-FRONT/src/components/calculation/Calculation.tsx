import React, {useEffect } from "react";
import { useNavigate } from "react-router-dom";
import '../../App.css';
import './calculation.css';

import { DropzoneWithExpenses } from "./Dropzone";

const Home: React.FC = () => {
  const navigateTo = useNavigate();

    useEffect(()=>{
      if(!window.localStorage.getItem("user")){
         navigateTo("/login");
      }
    },);

  return (
    <div className="calculation-container">
    <section className="text-center">
      <div className="card-body py-5 px-md-5">
        <div className="row d-flex justify-content-center">
            <div >
              <div className="login-header">
                <h1 className="fw-bold mb-5"> Calculation </h1>
              </div>
              <header className="jumbotron">
                <DropzoneWithExpenses/>
              </header>
            </div>
        </div>
      </div>
    </section>
    </div>
  );
};

export default Home;

