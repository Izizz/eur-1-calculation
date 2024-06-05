
import { useState} from 'react';
import axios from 'axios';
import { DownloadFiles } from '../DownloadFiles';
import '../../App.css';
import { toast} from 'react-toastify';
import { Formik, Field, Form, ErrorMessage } from "formik";
import { BASE_URL } from "../../utils/config";



export const DropzoneWithExpenses=()=>{

  const API_URL = BASE_URL + "/api/v1/calc";
  const API_URL_EXAMPLE_DOWNLOAD = BASE_URL+ "/api/v1/files/download";

  const [expenseFile,setExpenseFile] = useState([]);
  const [filePartsList,setFilePartsList] = useState([]);
  const [filesToDownload,setFilesToDownload] = useState([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [isDone,setIsDone] = useState(false);
  const token = window.localStorage.getItem('user');
  

  const onInputChangeForExpenses = (e:any) => {
    setExpenseFile(e.target.files)
  };

 const onInputChangeForPartsList = (e:any) => {
    setFilePartsList(e.target.files)
  };
 
const initialValues: {
    title: string;
  } = {
    title: "",
  };


const onSubmit = (formValue:{title:string}) => {
    const ProductTitle = formValue.title;
    
    const data = new FormData();
    
    setLoading(true);

    data.append('file',filePartsList[0]);
    data.append('expenses file',expenseFile[0]);
    data.append("product title",ProductTitle) ;
    axios.post(API_URL+ '/calculate', data,{
      headers:{
        "Authorization": "Bearer " + token,
      }
    }).then((res) => {
            toast.success('Calculation Success');
            setIsDone(true);
            setLoading(false);
            const arr : any = [res.data[0].fileDownloadUri,res.data[1].fileDownloadUri,res.data[2].fileDownloadUri];
            setFilesToDownload(arr);
        }).catch((error) => {
          if (error.response && error.response.data && error.response.data.errorMessages) {
            const errorMessages = error.response.data.errorMessages;
            errorMessages.forEach((errorMessage: string) => {
              toast.error(errorMessage, {
                toastId: errorMessage,
                autoClose: false,
              });
            });
          } else {
            toast.error('Calculation Error');
          }
          setLoading(false);
        });  
};

return(
  <section className="text-center">
    <div className="card-body py-5 px-md-5">
      <div className="row d-flex justify-content-center">
        <div className="col-lg-8">


          <div>
         
          <Formik initialValues={initialValues}
                  onSubmit={onSubmit} >
            <Form >
              <div className="form-outline mb-4" >
              <div className="login-header">
                <h3 className="fw-bold mb-5 " >Expenses file</h3>
              </div>
                
                <input type="file" 
                       className="form-control" 
                       onChange={onInputChangeForExpenses}
                       />        
              </div>

              <div className="login-header">
                <h3 className="fw-bold mb-5 ">Bill of materials</h3>

              </div>
         
              <div className="form-outline mb-4" >
                <input type="file" 
                       className="form-control" 
                       onChange={onInputChangeForPartsList}
                       />        
              </div>
              <div className="login-header">
                <h3 className="fw-bold mb-5 " >Product title</h3>
              </div>
         
              <div className="form-outline mb-4">
                <Field name="title" type="text" className="form-control" placeholder="Product title"/>       
                <ErrorMessage
                  name="title"
                  component="div"
                  className="alert alert-danger"
                />
              </div>

              <div className="form-group ">
            
                    {loading ? (
                      <button type="submit" className="btn btn-primary btn-block" disabled>
                        <div className="spinner-border text-light" role="status">
                          <span className="visually-hidden">Loading...</span>
                        </div>
                      </button>
                    ) : (
                      <button type="submit" className="btn btn-primary btn-block">
                        <span>Submit</span>
                      </button>
                    )}
              </div>

            </Form>
          </Formik>
          
          </div>
          {isDone &&
          <DownloadFiles files={filesToDownload} /> 
        }
        </div>
        </div>
      </div>
  </section>
  )
}
