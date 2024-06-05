import React, { useEffect, useState } from "react";
import { NavigateFunction, useNavigate } from 'react-router-dom';
import { Formik, Field, Form, ErrorMessage } from "formik";
import * as Yup from "yup";
import "./login.css";
import 'bootstrap/dist/css/bootstrap.min.css';

import { login } from "../../services/auth-service";

type Props = {}

const Login: React.FC<Props> = () => {
  const navigateTo = useNavigate();
  
  useEffect(()=>{
    if(window.localStorage.getItem("user")){
       navigateTo("/");
    }
  },);
  let navigate: NavigateFunction = useNavigate();

  const [loading, setLoading] = useState<boolean>(false);

  const initialValues: {
    username: string;
    password: string;
  } = {
    username: "",
    password: "",
  };

  const validationSchema = Yup.object().shape({
    username: Yup.string().required("This field is required!"),
    password: Yup.string().required("This field is required!"),
  });

  const handleLogin = (formValue: { username: string; password: string }) => {
    const { username, password } = formValue;

    setLoading(true);

    login(username, password).then(
      () => {
        navigate("/");
        window.location.reload();
      },
      (error) => {
          (error.response &&
            error.response.data &&
            error.response.data.message) ||
          error.message ||
          error.toString();

        setLoading(false);
      }
    );
  };

  return (

    <div className="login-container">
    <section className="text-center">
    <div className="card-body py-5 px-md-5">

      <div className="row d-flex justify-content-center">
        <div className="col-lg-8">
          <div className="login-header">
           <h2 className="fw-bold mb-5 " >Welcome To EUR-1 Certificate Calculation Service  </h2>
          </div>
          <Formik
          initialValues={initialValues}
          validationSchema={validationSchema}
          onSubmit={handleLogin}>
            <Form>
              <div className="form-outline mb-4">
                <Field name="username" type="text" className="form-control" placeholder="Username" />
                <ErrorMessage
                  name="username"
                  component="div"
                  className="alert alert-danger"
                />
              </div>

              <div className="form-outline mb-4">
                <Field name="password" type="password" className="form-control" placeholder="Password"/>
                <ErrorMessage
                  name="password"
                  component="div"
                  className="alert alert-danger"
                />
              </div>

              
              <div className="form-group ">
                <button type="submit" className="btn btn-primary btn-block" disabled={loading}>
                  {loading && (
                    <span className="spinner-border spinner-border-sm"></span>
                  )}
                  <span>Sign In</span>
                </button>
              </div>

            </Form>
          </Formik>
        </div>
      </div>
    </div>
</section>
</div>
  );
};

export default Login;
